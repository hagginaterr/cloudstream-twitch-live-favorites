// Use an integer for version numbers
version = 9

cloudstream {
    description = "Twitch Live Favorites: stable single Live Now row, existing Twitch favorites import, search-to-add, safer URL parsing, and safe empty-state card."
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
