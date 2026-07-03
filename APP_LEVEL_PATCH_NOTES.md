# v3.3 Patch Notes

Built from the stable v3.2 API provider.

## Added

- Conservative 429 / Too Many Requests backoff.
- Last-known-good in-memory cache fallback for Live Now if Twitch API fails after a successful check.
- Last updated label in the Live Now row title.
- Viewer-count sorting for Live Now.
- Category + compact viewer count subtitles.

## Changed

- Live Now cards no longer add `[LIVE]` to titles.
- Detail tags no longer include redundant `Live` or `Live Favorite` chips.
- Live Now cards prefer stream preview thumbnails when available.

## Unchanged

- Built-in CloudStream favorites only.
- No TwitchTracker calls.
- No custom Add/Remove cards.
- Original pwn.sh / Streamlink playback route.
- No direct-play experiment.
- No auto-refresh timer.
