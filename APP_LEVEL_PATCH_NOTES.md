# Twitch Live Favorites API v3.5

Based on the stable v3.3/v3.4 API provider.

## Changes from v3.4

- Changed the Live Now cache TTL and auto-refresh interval to 5 minutes.
- Auto-refresh now only asks CloudStream to re-render the row. It no longer force-invalidates the Twitch API cache.
- Opening a streamer page no longer expires the Live Now cache.
- Starting playback no longer expires the Live Now cache.
- Streamer detail pages reuse cached Live Now metadata when available instead of immediately making extra Helix calls.
- Temporary API failures keep the last-known-good result and wait briefly before retrying, so repeated UI reloads cannot hammer Twitch.
- Updated internal action marker to v3.5 while safely trapping v3.1-v3.4 and old v2 action URLs.

## Intent

This version chooses the most conservative refresh design: a nearby/automatic refresh feel without generating extra Twitch API traffic from navigation, playback, or timer spam.
