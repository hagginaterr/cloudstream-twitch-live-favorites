package recloudstream.twitchlivefavorites

import com.lagradost.cloudstream3.CloudStreamApp.Companion.getKey
import com.lagradost.cloudstream3.CloudStreamApp.Companion.setKey
import com.lagradost.cloudstream3.HomePageList
import com.lagradost.cloudstream3.HomePageResponse
import com.lagradost.cloudstream3.LiveSearchResponse
import com.lagradost.cloudstream3.LoadResponse
import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.MainPageRequest
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.fixUrl
import com.lagradost.cloudstream3.mainPageOf
import com.lagradost.cloudstream3.newHomePageResponse
import com.lagradost.cloudstream3.newLiveSearchResponse
import com.lagradost.cloudstream3.newLiveStreamLoadResponse
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.ExtractorLinkType
import com.lagradost.cloudstream3.utils.getQualityFromName
import com.lagradost.cloudstream3.utils.newExtractorLink
import com.lagradost.cloudstream3.utils.DataStoreHelper
import org.jsoup.nodes.Element
import java.lang.RuntimeException

class TwitchLiveFavoritesProvider : MainAPI() {
    override var mainUrl = "https://twitchtracker.com"
    override var name = "Twitch Live Favorites"
    override val supportedTypes = setOf(TvType.Live)
    override var lang = "uni"
    override val hasMainPage = true
    override var sequentialMainPage = true
    override var sequentialMainPageDelay = 350L

    private val liveFavoritesNowName = "Live Now"
    private val gamesName = "games"
    private val isHorizontal = true

    // Use normal HTTPS URLs for internal plugin actions. CloudStream may normalize or route
    // custom schemes such as cloudstream:// before they reach load(), which caused the older
    // build to treat the Help/Add cards as real TwitchTracker channel pages.
    private val actionMarker = "__twitch_live_favorites_action__"
    private val actionBase = "$mainUrl/$actionMarker"
    private val addPrefix = "$actionBase/add/"
    private val removePrefix = "$actionBase/remove/"
    private val noopPrefix = "$actionBase/noop/"

    private val prefsFolder = "twitch_live_favorites_provider_v2"
    private val channelsKey = "$prefsFolder/favorite_channels"

    override val mainPage = mainPageOf(
        "$actionBase/live" to liveFavoritesNowName,
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        return when (request.name) {
            liveFavoritesNowName -> {
                val favorites = parseFavoriteChannels()
                val liveFavorites = favorites.filter { it.isLive }
                singleHomeResponse(
                    liveFavoritesNowName,
                    if (liveFavorites.isEmpty()) {
                        listOf(emptyLiveFavoritesCard(favorites.isEmpty()))
                    } else {
                        liveFavorites.map { it.toChannelCard(showOfflineLabel = false) }
                    },
                    hasNext = false,
                )
            }

            gamesName -> newHomePageResponse(parseGames(), hasNext = false)

            else -> {
                val doc = app.get(request.data, params = mapOf("page" to page.toString())).document
                val channels = doc.select("table#channels tr")
                    .mapNotNull { element -> element.toChannelSummary()?.toChannelCard() }
                singleHomeResponse(request.name, channels, hasNext = true)
            }
        }
    }

    private fun singleHomeResponse(
        name: String,
        items: List<SearchResponse>,
        hasNext: Boolean,
    ): HomePageResponse {
        return newHomePageResponse(
            listOf(HomePageList(name, items, isHorizontalImages = isHorizontal)),
            hasNext = hasNext,
        )
    }

    private data class ChannelSummary(
        val channel: String,
        val displayName: String,
        val image: String? = null,
        val language: String? = null,
    )

    private data class FavoriteChannel(
        val channel: String,
        val displayName: String,
        val image: String?,
        val poster: String?,
        val isLive: Boolean,
        val language: String?,
        val rank: Int?,
        val description: String?,
    )

    /**
     * Channels saved inside this custom plugin using the [Add] cards.
     * This is kept separate from CloudStream's built-in favorites so the custom plugin
     * never mutates the user's normal library.
     */
    private fun getPluginSavedFavoriteChannels(): List<String> {
        return getKey<String>(channelsKey, "")
            .orEmpty()
            .split('|')
            .map { normalizeChannel(it) }
            .filter { it.isNotBlank() }
            .distinct()
    }

    /**
     * Best-effort read of existing CloudStream favorites created by the normal Twitch provider.
     * This uses CloudStream internals, not the public provider API, but it is read-only.
     */
    private fun getCloudStreamTwitchFavoriteChannels(): List<String> {
        return runCatching {
            DataStoreHelper.getAllFavorites()
                .asSequence()
                .filter { favorite ->
                    favorite.apiName.equals("Twitch", ignoreCase = true) ||
                        favorite.url.contains("twitch", ignoreCase = true) ||
                        favorite.url.contains("twitchtracker", ignoreCase = true)
                }
                .mapNotNull { favorite ->
                    val fromUrl = normalizeChannel(favorite.url)
                    val fromName = normalizeChannel(favorite.name)
                    when {
                        fromUrl.isNotBlank() -> fromUrl
                        fromName.isNotBlank() -> fromName
                        else -> null
                    }
                }
                .filter { it.isNotBlank() && it != "twitch" && it != "twitchtracker" }
                .distinct()
                .toList()
        }.getOrDefault(emptyList())
    }

    /**
     * The visible Live Now list is the union of this plugin's saved list and the user's
     * existing CloudStream Twitch favorites. Offline channels are still hidden by getMainPage().
     */
    private fun getSavedFavoriteChannels(): List<String> {
        return (getPluginSavedFavoriteChannels() + getCloudStreamTwitchFavoriteChannels())
            .map { normalizeChannel(it) }
            .filter { it.isNotBlank() }
            .distinct()
    }

    private fun savePluginFavoriteChannels(channels: List<String>) {
        val normalized = channels
            .map { normalizeChannel(it) }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
            .joinToString("|")
        setKey(channelsKey, normalized)
    }

    private fun isFavorite(channel: String): Boolean {
        val normalized = normalizeChannel(channel)
        return normalized.isNotBlank() && getSavedFavoriteChannels().contains(normalized)
    }

    private fun isPluginSavedFavorite(channel: String): Boolean {
        val normalized = normalizeChannel(channel)
        return normalized.isNotBlank() && getPluginSavedFavoriteChannels().contains(normalized)
    }

    private fun addFavorite(channel: String): Boolean {
        val normalized = normalizeChannel(channel)
        if (normalized.isBlank()) return false
        val current = getPluginSavedFavoriteChannels()
        if (current.contains(normalized)) return false
        savePluginFavoriteChannels(current + normalized)
        return true
    }

    private fun removeFavorite(channel: String): Boolean {
        val normalized = normalizeChannel(channel)
        if (normalized.isBlank()) return false
        val current = getPluginSavedFavoriteChannels()
        if (!current.contains(normalized)) return false
        savePluginFavoriteChannels(current.filterNot { it == normalized })
        return true
    }

    private suspend fun parseFavoriteChannels(): List<FavoriteChannel> {
        return getSavedFavoriteChannels()
            .map { channel -> fetchChannel(channel) ?: fallbackChannel(channel) }
            .sortedWith(
                compareByDescending<FavoriteChannel> { it.isLive }
                    .thenBy { it.displayName.lowercase() },
            )
    }

    private suspend fun fetchChannel(channel: String): FavoriteChannel? {
        val normalized = normalizeChannel(channel)
        if (normalized.isBlank()) return null

        return runCatching {
            val doc = app.get("$mainUrl/$normalized", referer = mainUrl).document
            val displayName = doc.select("div#app-title").text().ifBlank { normalized }
            if (displayName.isBlank()) return@runCatching null

            val image = doc.select("div#app-logo > img").attr("src").ifBlank { null }
            val poster = doc.select("div.embed-responsive > img").attr("src").ifBlank { image }
            val description = doc.select("div[style='word-wrap:break-word;font-size:12px;']").text().ifBlank { null }
            val language = doc.select("a.label.label-soft").text().ifBlank { null }
            val rank = doc.select("div.rank-badge > span").last()?.text()?.toIntOrNull()
            FavoriteChannel(
                channel = normalized,
                displayName = displayName,
                image = image,
                poster = poster,
                isLive = doc.select("div.live-indicator-container").isNotEmpty(),
                language = language,
                rank = rank,
                description = description,
            )
        }.getOrNull()
    }

    private fun fallbackChannel(channel: String): FavoriteChannel {
        val normalized = normalizeChannel(channel)
        return FavoriteChannel(
            channel = normalized,
            displayName = normalized,
            image = null,
            poster = null,
            isLive = false,
            language = null,
            rank = null,
            description = null,
        )
    }

    private fun normalizeChannel(value: String): String {
        val trimmed = value
            .trim()
            .removePrefix("@")
            .substringBefore("?")
            .substringBefore("#")
            .trim('/')
            .substringAfterLast("/")
            .lowercase()

        return trimmed.filter { it.isLetterOrDigit() || it == '_' }
    }

    private fun twitchUrl(channel: String): String = "https://twitch.tv/${normalizeChannel(channel)}"

    private fun FavoriteChannel.toChannelCard(showOfflineLabel: Boolean): LiveSearchResponse {
        val displayTitle = when {
            isLive -> "[LIVE] $displayName"
            showOfflineLabel -> "$displayName (offline)"
            else -> displayName
        }

        return newLiveSearchResponse(displayTitle, channel, TvType.Live, fix = false) {
            posterUrl = image
            lang = language
        }
    }

    private fun ChannelSummary.toChannelCard(): LiveSearchResponse {
        return newLiveSearchResponse(displayName, channel, TvType.Live, fix = false) {
            posterUrl = image
            lang = language
        }
    }

    private fun ChannelSummary.toAddCard(label: String = "Add $displayName to Live Favorites"): LiveSearchResponse {
        return newLiveSearchResponse("[Add] $label", "$addPrefix$channel", TvType.Live, fix = false) {
            posterUrl = image
            lang = "Live Favorites"
        }
    }

    private fun FavoriteChannel.toAddCard(label: String = "Add $displayName to Live Favorites"): LiveSearchResponse {
        return newLiveSearchResponse("[Add] $label", "$addPrefix$channel", TvType.Live, fix = false) {
            posterUrl = image
            lang = "Live Favorites"
        }
    }

    private fun FavoriteChannel.toRemoveCard(label: String = "Remove $displayName from Live Favorites"): LiveSearchResponse {
        return newLiveSearchResponse("[Remove] $label", "$removePrefix$channel", TvType.Live, fix = false) {
            posterUrl = image
            lang = "Live Favorites"
        }
    }

    private fun addCardForChannel(channel: String, image: String? = null): LiveSearchResponse {
        val normalized = normalizeChannel(channel)
        return newLiveSearchResponse("[Add] $normalized to Live Favorites", "$addPrefix$normalized", TvType.Live, fix = false) {
            posterUrl = image
            lang = "Live Favorites"
        }
    }

    private fun emptyFavoritesCard(): LiveSearchResponse {
        return newLiveSearchResponse(
            "No Live Favorites yet - search a streamer to add one",
            "${noopPrefix}no-favorites",
            TvType.Live,
            fix = false,
        ) {
            lang = "Live Favorites"
        }
    }

    private fun emptyLiveFavoritesCard(hasNoFavorites: Boolean): LiveSearchResponse {
        val title = if (hasNoFavorites) {
            "No Live Favorites yet - search a streamer to add one"
        } else {
            "No saved favorites are live right now"
        }
        val reason = if (hasNoFavorites) "no-favorites" else "none-live"
        return newLiveSearchResponse(title, "${noopPrefix}$reason", TvType.Live, fix = false) {
            lang = "Live Favorites"
        }
    }

    private fun Element.toChannelSummary(): ChannelSummary? {
        val link = this.select("a[href]")
            .firstOrNull { normalizeChannel(it.attr("href")).isNotBlank() }
            ?: return null
        val channel = normalizeChannel(link.attr("href"))
        if (channel.isBlank()) return null

        val displayName = this.select("a")
            .firstOrNull { it.text().isNotBlank() }
            ?.text()
            ?.ifBlank { null }
            ?: channel
        val image = this.select("img").attr("src").ifBlank { null }
        val language = this.select("a.label.label-soft").text().ifBlank { null }
        return ChannelSummary(channel, displayName, image, language)
    }

    private suspend fun parseGames(): List<HomePageList> {
        val doc = app.get("$mainUrl/games").document
        return doc.select("div.ranked-item")
            .take(5)
            .mapNotNull { element ->
                val game = element.select("div.ri-name > a")
                val url = fixUrl(game.attr("href"))
                val name = game.text()
                val searchResponses = parseGame(url).ifEmpty { return@mapNotNull null }
                HomePageList(name, searchResponses, isHorizontalImages = isHorizontal)
            }
    }

    private suspend fun parseGame(url: String): List<LiveSearchResponse> {
        val doc = app.get(url).document
        return doc.select("td.cell-slot.sm")
            .mapNotNull { element -> element.toChannelSummary()?.toChannelCard() }
    }

    override suspend fun load(url: String): LoadResponse {
        val action = parseActionUrl(url)
        return when (action?.first) {
            "add" -> addFavoriteResponse(action.second)
            "remove" -> removeFavoriteResponse(action.second)
            "noop", "help" -> throw RuntimeException(noopMessage(action.second))
            else -> channelLoadResponse(url)
        }
    }

    private fun parseActionUrl(url: String): Pair<String, String>? {
        val cleanUrl = url
            .substringBefore('?')
            .substringBefore('#')
            .trim()
            .trimEnd('/')

        val actionPath = when {
            cleanUrl.startsWith(actionBase) -> cleanUrl.removePrefix(actionBase).trimStart('/')
            cleanUrl.contains("/$actionMarker/") -> cleanUrl.substringAfter("/$actionMarker/")
            cleanUrl.startsWith("$actionMarker/") -> cleanUrl.removePrefix("$actionMarker/")
            cleanUrl == actionMarker -> return null
            else -> return null
        }

        val action = actionPath.substringBefore('/').ifBlank { return null }
        val value = actionPath.substringAfter('/', "")
        return action to value
    }

    private fun noopMessage(reason: String): String {
        return if (reason == "no-favorites") {
            "No Live Favorites yet. Search a streamer to add one. Existing normal Twitch favorites are imported automatically."
        } else {
            "No saved/imported Twitch favorites are live right now. Refresh later to check again."
        }
    }

    private suspend fun addFavoriteResponse(channel: String): LoadResponse {
        val normalized = normalizeChannel(channel)
        val changed = addFavorite(normalized)
        val info = fetchChannel(normalized) ?: fallbackChannel(normalized)
        val title = if (changed) {
            "Added ${info.displayName}"
        } else {
            "${info.displayName} is already saved"
        }
        val message = if (changed) {
            "${info.displayName} was added to Live Favorites. Return to the Twitch Live Favorites home page. They will only appear in Live Now while they are actually live."
        } else {
            "${info.displayName} is already in your Live Favorites list."
        }

        return newLiveStreamLoadResponse(title, twitchUrl(info.channel), twitchUrl(info.channel)) {
            plot = message
            posterUrl = info.image
            backgroundPosterUrl = info.poster
            tags = listOf(if (info.isLive) "Live" else "Offline", "Saved")
            recommendations = listOf(
                info.toChannelCard(showOfflineLabel = true),
                info.toRemoveCard(),
            )
        }
    }

    private suspend fun removeFavoriteResponse(channel: String): LoadResponse {
        val normalized = normalizeChannel(channel)
        val info = fetchChannel(normalized) ?: fallbackChannel(normalized)
        val changed = removeFavorite(normalized)
        val title = if (changed) {
            "Removed ${info.displayName}"
        } else {
            "${info.displayName} was not saved"
        }
        val message = if (changed) {
            "${info.displayName} was removed from Live Favorites."
        } else {
            "${info.displayName} was not in your Live Favorites list."
        }

        return newLiveStreamLoadResponse(title, twitchUrl(info.channel), twitchUrl(info.channel)) {
            plot = message
            posterUrl = info.image
            backgroundPosterUrl = info.poster
            tags = listOf(if (info.isLive) "Live" else "Offline", "Not saved")
            recommendations = listOf(
                info.toChannelCard(showOfflineLabel = true),
                info.toAddCard(),
            )
        }
    }

    private suspend fun channelLoadResponse(url: String): LoadResponse {
        val channel = normalizeChannel(url)
        val info = fetchChannel(channel) ?: throw RuntimeException("Could not load page, please try again.\n")
        val tags = listOfNotNull(
            if (info.isLive) "Live" else "Offline",
            if (isFavorite(info.channel)) "Live Favorite" else null,
            info.language,
            info.rank?.let { "Rank: $it" },
        )
        val action = if (isPluginSavedFavorite(info.channel)) {
            info.toRemoveCard()
        } else {
            info.toAddCard()
        }
        val streamUrl = twitchUrl(info.channel)

        return newLiveStreamLoadResponse(info.displayName, streamUrl, streamUrl) {
            plot = info.description
            posterUrl = info.image
            backgroundPosterUrl = info.poster
            this@newLiveStreamLoadResponse.tags = tags
            recommendations = listOf(action)
        }
    }

    override suspend fun search(query: String): List<SearchResponse>? {
        val normalizedQuery = normalizeChannel(query)
        val exactAction = if (normalizedQuery.isNotBlank()) {
            if (isPluginSavedFavorite(normalizedQuery)) {
                listOf(fallbackChannel(normalizedQuery).toRemoveCard("Remove $normalizedQuery from Live Favorites"))
            } else {
                listOf(addCardForChannel(normalizedQuery))
            }
        } else {
            emptyList()
        }

        val results = runCatching {
            val document = app.get("$mainUrl/search", params = mapOf("q" to query), referer = mainUrl).document
            document.select("table.tops tr")
                .mapNotNull { it.toChannelSummary() }
                .distinctBy { it.channel }
        }.getOrElse { emptyList() }

        val addActions = results
            .filterNot { isPluginSavedFavorite(it.channel) }
            .take(8)
            .map { it.toAddCard() }

        val removeActions = results
            .filter { isPluginSavedFavorite(it.channel) }
            .take(8)
            .map { fallbackChannel(it.channel).copy(displayName = it.displayName, image = it.image, language = it.language).toRemoveCard("Remove ${it.displayName} from Live Favorites") }

        return (exactAction + removeActions + addActions + results.map { it.toChannelCard() })
            .distinctBy { it.url }
    }

    data class ApiResponse(
        val success: Boolean,
        val urls: Map<String, String>?,
    )

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit,
    ): Boolean {
        if (!data.startsWith("http", ignoreCase = true)) return false

        val response = runCatching {
            app.get("https://pwn.sh/tools/streamapi.py?url=$data").parsed<ApiResponse>()
        }.getOrNull() ?: return false

        var found = false
        response.urls?.forEach { (qualityName, streamUrl) ->
            val quality = getQualityFromName(qualityName.substringBefore("p"))
            callback.invoke(
                newExtractorLink(
                    name,
                    "$name ${qualityName.replace("${quality}p", "")}",
                    streamUrl,
                ) {
                    this.type = ExtractorLinkType.M3U8
                    this.quality = quality
                    this.referer = ""
                },
            )
            found = true
        }
        return found
    }
}
