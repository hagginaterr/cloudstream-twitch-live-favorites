# Twitch Live Favorites v2.2 for CloudStream

This is a separate CloudStream provider named **Twitch Live Favorites**. It is designed to coexist with the normal Twitch provider.


## v2.2 runtime fix

v2.2 removes the direct `com.lagradost.api.getContext()` call that caused `Failed resolution of: Lcom/lagradost/api/ContextHelper_jvmKt` on Android TV. Favorites are now saved through CloudStream's own `CloudStreamApp.getKey` / `setKey` helpers.

## What v2 adds

- Separate provider name: `Twitch Live Favorites`
- Own saved favorites list stored locally by the plugin
- Search-to-add flow from inside CloudStream
- `Live Favorites Now` home row
- `Favorites - live first` home row
- `Manage Live Favorites` row with remove cards
- Channel detail page shows an add/remove action in recommendations
- Offline channels are labeled `(offline)` in the live-first list

## Android TV workflow

1. Open CloudStream.
2. Select provider **Twitch Live Favorites**.
3. Search a Twitch streamer, for example `shroud`.
4. Open the result card named `[Add] shroud to Live Favorites` or `[Add] Add <name> to Live Favorites`.
5. Return to the provider home page.
6. Use:
   - **Live Favorites Now** for saved channels that are live.
   - **Favorites - live first** for all saved channels, sorted live first.
   - **Manage Live Favorites** to remove saved channels.

## Important behavior notes

This plugin does **not** read CloudStream's built-in Library/Favorites list. It maintains its own saved Twitch-channel list so the workflow can work fully inside the provider without patching the CloudStream app.

Because CloudStream provider cards cannot create true custom buttons, add/remove actions are implemented as selectable cards. Opening an add/remove card performs the action and shows a confirmation page.

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


## v2.1 fix

This build no longer registers a global Twitch extractor, so it should not interfere with the normal Twitch plugin or older CloudStream favorites. Search now always shows an exact `[Add]` card even if TwitchTracker search fails.
