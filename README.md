# Twitch Live Favorites for CloudStream

Custom CloudStream provider that shows one home row: **Live Now**.

v3.0 removes TwitchTracker scraping from this custom plugin. Live status, profile metadata, and search are handled through the official Twitch Helix API. Playback is intentionally kept the same as the good v2.5 path: CloudStream sends `https://twitch.tv/<streamer>` to the pwn.sh / Streamlink API only when you actually press Play.

## What it does

- Shows a single **Live Now** row.
- Includes streamers saved inside this custom provider.
- Also imports existing favorites from the normal CloudStream **Twitch** provider, read-only.
- Displays only streamers who are currently live.
- Uses Twitch Helix `Get Streams`, batched up to 100 logins per request, instead of scraping one TwitchTracker page per favorite.
- Uses Twitch Helix search for streamer search.
- Keeps the original pwn.sh playback method from v2.5.
- Has no Help page and no TwitchTracker page loads.

## Required Twitch API setup

This private build needs Twitch Developer credentials:

- `TWITCH_CLIENT_ID`
- `TWITCH_CLIENT_SECRET`

Create them in the Twitch Developer Console by registering an application. A redirect URL like `http://localhost` is fine for this use case.

Then add them to your GitHub repository:

```text
Settings -> Secrets and variables -> Actions -> New repository secret
```

Add both secrets exactly as:

```text
TWITCH_CLIENT_ID
TWITCH_CLIENT_SECRET
```

Do not commit your Client Secret directly to the repo. The workflow generates `TwitchCredentials.kt` during the private build.

If the secrets are missing, the plugin still builds, but the TV will show a setup-needed card instead of a working Live Now list.

## Build

Run the GitHub Actions workflow. The artifact will contain `TwitchLiveFavoritesProvider.cs3`.

## Install with ADB

```cmd
adb connect YOUR_TV_IP:5555
adb shell mkdir -p /sdcard/Cloudstream3/plugins
adb shell rm -f /sdcard/Cloudstream3/plugins/TwitchLiveFavoritesProvider.cs3
adb push "C:\path\to\TwitchLiveFavoritesProvider.cs3" /sdcard/Cloudstream3/plugins/TwitchLiveFavoritesProvider.cs3
adb shell am force-stop com.lagradost.cloudstream3
adb shell monkey -p com.lagradost.cloudstream3 1
```

For prerelease CloudStream, replace the package name with:

```cmd
com.lagradost.cloudstream3.prerelease
```

## Notes

- Keep the normal **Twitch** plugin installed if you want this provider to import your existing normal Twitch favorites.
- Removing a streamer inside this provider only removes streamers saved by this provider. It will not delete normal CloudStream favorites.
- Live Now caches results for about 2 minutes to reduce API calls.
- The built `.cs3` contains your private Twitch credentials. Do not share it publicly.
