package recloudstream.twitchlivefavorites

import com.lagradost.cloudstream3.plugins.BasePlugin
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin

@CloudstreamPlugin
class TwitchLiveFavoritesPlugin : BasePlugin() {
    override fun load() {
        registerMainAPI(TwitchLiveFavoritesProvider())
        registerExtractorAPI(TwitchLiveFavoritesProvider.TwitchLiveFavoritesExtractor())
    }
}
