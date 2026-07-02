# Twitch Live Favorites v2.11

This is a rollback-stable build based on the last known good v2.5 code path.

Changes from v2.5:

- Keeps the working automatic read-only import from normal CloudStream Twitch favorites.
- Keeps one Home row: Live Now.
- Keeps only actually-live streamers visible in Live Now.
- Removes the Help page trap. Empty-state cards now use a no-op action that shows an error/toast and leaves you on the current screen instead of opening a Help/profile page.
- Does not include the experimental direct-play or UI refresh changes from later builds.

# Twitch Live Favorites v2.11 for CloudStream

This is a separate CloudStream provider named **Twitch Live Favorites**. It is designed to coexist with the normal Twitch provider.

## What it does

- Separate provider name: `Twitch Live Favorites`
- Own saved favorites list stored locally by the plugin
- Search-to-add flow from inside CloudStream
- One home row only: `Live Now`
- `Live Now` only shows saved favorite streamers while they are currently live
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

This plugin does **not** read CloudStream's built-in Library/Favorites list. It maintains its own saved Twitch-channel list so the workflow can work fully inside the provider without patching the CloudStream app.

`Live Now` is intentionally live-only. Adding a streamer saves them to the plugin's internal list, but they will only be visible on the home page while TwitchTracker reports them as live.

Because CloudStream provider cards cannot create true custom buttons, add/remove actions are implemented as selectable cards. Opening an add/remove card performs the action and shows a confirmation page.

## Recent fixes

- v2.1 removed global Twitch extractor registration so the custom plugin should not interfere with the normal Twitch plugin.
- v2.2 replaced `getContext()` storage with CloudStream `getKey` / `setKey` storage to avoid Android TV runtime crashes.
- v2.3 changed internal add/remove URLs to normal HTTPS action URLs so Android TV passes them back to the provider correctly.
- v2.11 simplifies the home page to one row: `Live Now`.

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


## v2.11 note: existing CloudStream Twitch favorites

This build reads CloudStream's existing local Favorites list in read-only mode and automatically includes entries from the normal `Twitch` provider in the `Live Now` row. It does not modify or delete CloudStream's normal favorites. The visible `Live Now` row still only shows saved/imported streamers while they are currently live.

The `[Remove]` card only removes streamers saved directly by this custom plugin. If a streamer is still in CloudStream's normal Twitch favorites, remove it from the normal CloudStream Favorites/Library as well if you do not want it to appear in `Live Now`.
