# Twitch Live Favorites API v3.3 for CloudStream

Private CloudStream provider that shows a single **Live Now** row for Twitch channels saved in CloudStream's built-in favorites.

## v3.3 changes

- Keeps the stable v3.2 behavior: built-in CloudStream favorites only, no custom Add/Remove cards.
- Uses Twitch Helix for live detection/search/profile metadata.
- Does **not** call TwitchTracker.
- Keeps the original pwn.sh / Streamlink playback route.
- Adds conservative 429 / Too Many Requests backoff.
- Adds last-known-good in-memory Live Now fallback if Twitch API temporarily fails.
- Shows `Live Now • updated 45s ago` / `updated 3m ago` when a successful check has happened.
- Sorts Live Now by viewer count, highest first.
- Cleans Live Now cards: no `[LIVE]`, no `Live Favorite`, no redundant `Live` tag.
- Uses stream preview images where available, with subtitle text like `Just Chatting • 12.4K viewers`.

## Required GitHub Actions secrets

Add these repository secrets before building:

```text
TWITCH_CLIENT_ID
TWITCH_CLIENT_SECRET
```

The workflow generates `TwitchCredentials.kt` during the build. Do not commit real credentials to the repo and do not share the built `.cs3` publicly.

## Usage

1. Keep the normal Twitch plugin installed if you still use it for regular Twitch browsing/favorites.
2. Install this `.cs3` manually or through your test workflow.
3. Select **Twitch Live Favorites API** as the Home source.
4. Favorite streamers using CloudStream's normal favorite button.
5. Open/refresh **Live Now** to see only currently live favorites.
