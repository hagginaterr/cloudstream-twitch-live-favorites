// Use an integer for version numbers
version = 7

cloudstream {
    description = "Twitch Live Favorites: Live Now only, existing CloudStream Twitch favorites import, no Help placeholder page, search-to-add, and experimental direct play."
    authors = listOf("CranberrySoup", "Custom live-favorites patch")

    /**
     * Status int as one of the following:
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta-only
     */
    status = 3
    tvTypes = listOf("Live")
    iconUrl = "https://www.google.com/s2/favicons?domain=twitch.tv&sz=%size%"
    isCrossPlatform = false
}
