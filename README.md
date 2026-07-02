# Twitch Live Favorites API v3.1

CloudStream Android TV provider for showing Twitch favorites that are currently live.

## What this version does

- Imports existing CloudStream favorites from the normal Twitch provider.
- Imports streamers saved directly inside this custom provider.
- Uses Twitch Helix API for live detection and search instead of TwitchTracker.
- Keeps the original v2.5 playback path through pwn.sh / Streamlink.
- Shows only live streamers in the **Live Now** row.
- Uses the provider name **Twitch Live Favorites API** so it is easy to distinguish from older test builds.

## Required GitHub Actions secrets

Add these repository secrets before building:

- `TWITCH_CLIENT_ID`
- `TWITCH_CLIENT_SECRET`

The workflow will fail if either one is missing. Do not commit your real secret to the repo.

## Important install note

Delete old local builds from `/sdcard/Cloudstream3/plugins` before pushing this one. Then select **Twitch Live Favorites API** in CloudStream's source selector.
