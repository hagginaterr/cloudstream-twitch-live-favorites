# Twitch Live Favorites API v3.1

This build is intentionally a clean break from the older TwitchTracker-based test builds.

## Changes

- Provider is now named **Twitch Live Favorites API** so it is visibly distinct from stale older builds.
- Uses Twitch Helix for Live Now checks.
- Keeps the same pwn.sh / Streamlink playback route from v2.5.
- Uses a new internal action marker: `__twitch_live_favorites_api_v31_action__`.
- Recognizes and neutralizes legacy `__twitch_live_favorites_action__` URLs, including old `noop`/`help` cards, so stale cards should not fall through into a fake channel load.
- Home page no longer uses a clickable no-live placeholder card. If no favorites are live, the Live Now row is empty rather than clickable.
- GitHub Actions now fails the build if Twitch credentials are missing.

## Install notes

Remove every old local `.cs3` that contains Twitch/Live/Favorites before pushing this one. Then select **Twitch Live Favorites API** as the source, not the older **Twitch Live Favorites** entry.
