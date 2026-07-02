# Twitch Live Favorites v2.7 for CloudStream

This is a separate CloudStream provider named **Twitch Live Favorites**. It is designed to coexist with the normal Twitch provider.

## What it does

- Separate provider name: `Twitch Live Favorites`
- Own saved favorites list stored locally by the plugin
- Search-to-add flow from inside CloudStream
- One home row only: `Live Now`
- `Live Now` only shows saved/imported favorite streamers while they are currently live
- Best-effort home/library refresh request after plugin Add/Remove actions
- Experimental direct-play cards in `Live Now`, so a first click should start playback on most builds
- Offline saved favorites remain saved but stay hidden from `Live Now` until they go live
- To remove a saved streamer, search/open that streamer and use the `[Remove]` card

## Android TV workflow

1. Open CloudStream.
2. Select provider **Twitch Live Favorites**.
3. Search a Twitch streamer, for example `shroud`.
4. Open the result card named `[Add] shroud to Live Favorites` or `[Add] Add <name> to Live Favorites`.
5. Return to the provider home page.
6. Use **Live Now** to see saved streamers who are currently live.

## Important behavior notes

`Live Now` is intentionally live-only. The row is built from this plugin's saved list plus a read-only import of existing CloudStream favorites from the normal `Twitch` provider. Offline saved/imported streamers stay hidden until TwitchTracker reports them as live.

Because CloudStream provider cards cannot create true custom buttons, add/remove actions are implemented as selectable cards. Opening an add/remove card performs the action and shows a confirmation page.

After plugin Add/Remove actions, v2.7 asks CloudStream to refresh the home/library UI. That is a best-effort internal refresh request, not a real background timer. If you add/remove a favorite through the normal Twitch plugin instead of this custom plugin, you may still need to refresh or reload the `Twitch Live Favorites` home page.

The `Live Now` row uses experimental direct-play cards. On CloudStream builds that support the internal Resume Watching click path, clicking a live streamer should start playback immediately. If your build still opens the detail page, that part requires an app-side CloudStream change.

## Recent fixes

- v2.1 removed global Twitch extractor registration so the custom plugin should not interfere with the normal Twitch plugin.
- v2.2 replaced `getContext()` storage with CloudStream `getKey` / `setKey` storage to avoid Android TV runtime crashes.
- v2.3 changed internal add/remove URLs to normal HTTPS action URLs so Android TV passes them back to the provider correctly.
- v2.5 imported existing normal Twitch CloudStream favorites into `Live Now` in read-only mode.
- v2.7 requests a UI refresh after Add/Remove and makes `Live Now` cards direct-play on supported CloudStream builds.

## Build with GitHub Actions

1. Create a GitHub repository.
2. Upload all files from this folder.
3. Go to **Actions**.
4. Run **Build CloudStream CS3**.
5. Download the artifact named `TwitchLiveFavoritesProvider-cs3`.
6. Install the `.cs3` file into CloudStream.

## Build locally

If you have Java 17, Android SDK, and Gradle installed:

```bash
./gradlew TwitchLiveFavoritesProvider:make
```

or, if you do not have a wrapper but have Gradle installed:

```bash
gradle TwitchLiveFavoritesProvider:make
```

Then find the output:

```bash
find . -name "*.cs3"
```

On Windows Command Prompt:

```cmd
dir /s /b *.cs3
```

## Install on Android TV

Copy the built `.cs3` file to:

```text
/sdcard/Cloudstream3/plugins/
```

Then fully close and reopen CloudStream. If CloudStream cannot see local plugin files, grant it All Files Access in Android settings.


## v2.7 note: existing CloudStream Twitch favorites

This build reads CloudStream's existing local Favorites list in read-only mode and automatically includes entries from the normal `Twitch` provider in the `Live Now` row. It does not modify or delete CloudStream's normal favorites. The visible `Live Now` row still only shows saved/imported streamers while they are currently live.

The `[Remove]` card only removes streamers saved directly by this custom plugin. If a streamer is still in CloudStream's normal Twitch favorites, remove it from the normal CloudStream Favorites/Library as well if you do not want it to appear in `Live Now`.


## v2.7 update

- Removes the clickable Help/empty-state card completely.
- The Home page now only shows the `Live Now` row with live saved streamers.
- If nobody is live, the row is empty instead of opening a confusing Help page.
- Keeps v2.6's best-effort refresh event after Add/Remove and experimental direct-play Live Now cards.
