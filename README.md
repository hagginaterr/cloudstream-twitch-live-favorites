# Twitch Live Favorites API v3.5 for CloudStream

Private CloudStream provider that shows a single **Live Now** row for Twitch channels saved in CloudStream's built-in favorites.

## v3.5 changes

- Keeps the stable v3.3/v3.4 Twitch API architecture: built-in CloudStream favorites only, no custom Add/Remove cards.
- Uses Twitch Helix for live detection/search/profile metadata.
- Does **not** call TwitchTracker.
- Keeps the original pwn.sh / Streamlink playback route.
- Keeps conservative 429 / Too Many Requests backoff.
- Keeps last-known-good in-memory Live Now fallback if Twitch API temporarily fails.
- Live Now is sorted by viewer count, highest first.
- Live Now cards use stream preview images where available, with subtitle text like `Just Chatting • 12.4K viewers`.
- Row title uses an absolute timestamp such as `Live Now • last checked 8:42 PM`.
- Auto-refresh is now API-safe: the timer only asks CloudStream to re-render the row. It does **not** force-expire the Twitch API cache.
- Navigation/playback no longer force-expires the Live Now cache, so opening/playing streams cannot cause extra Twitch API checks.

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

## v3.5 refresh behavior

The Live Now row schedules a guarded best-effort CloudStream home reload about every 5 minutes while the provider has recently been rendered. This is not a permanent tight polling loop: each timer is one-shot, and another timer is only scheduled if CloudStream reloads the provider again.

The important API-safety change from v3.4 is that the timer does **not** invalidate the cache. A Twitch Helix request is made only when the normal Live Now cache has expired, the favorites set changed, or no last-known-good result exists. This caps normal Live Now checks to roughly one batched Helix stream request per 5 minutes while active.

Opening a streamer page or starting playback does not force a new Twitch API request anymore. Playback still uses the same pwn.sh / Streamlink route as earlier working builds.
