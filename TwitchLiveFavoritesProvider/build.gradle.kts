// Use an integer for version numbers
version = 33

cloudstream {
    description = "Twitch Live Favorites API v3.3: Twitch Helix live detection with 429 backoff, last-known-good cache fallback, cleaned Live Now cards, and original pwn.sh playback."
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
