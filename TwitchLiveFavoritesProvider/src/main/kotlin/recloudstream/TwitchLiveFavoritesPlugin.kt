package recloudstream.twitchlivefavorites

import com.lagradost.cloudstream3.plugins.BasePlugin
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin

@CloudstreamPlugin
class TwitchApiLiveFavoritesPlugin : BasePlugin() {
    override fun load() {
        registerMainAPI(TwitchApiLiveFavoritesProvider())
    }
}
