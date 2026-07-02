# Twitch Live Favorites v2.9 for CloudStream

This is a separate CloudStream provider named **Twitch Live Favorites**. It is designed to coexist with the normal Twitch provider.

## What it does

- Separate provider name: `Twitch Live Favorites`
- Own saved favorites list stored locally by the plugin
- Search-to-add flow from inside CloudStream
- One home row only: `Live Now`
- `Live Now` only shows saved/imported favorite streamers while they are currently live
- Best-effort home/library refresh request after plugin Add/Remove actions
- Stable normal cards in `Live Now` so the row renders properly on Android TV
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

`Live Now` uses normal CloudStream provider cards for stability. Clicking a streamer opens its stream page; one-click autoplay would require an app-side CloudStream UI change or a safer CloudStream-supported card type.

## Recent fixes

- v2.1 removed global Twitch extractor registration so the custom plugin should not interfere with the normal Twitch plugin.
- v2.2 replaced `getContext()` storage with CloudStream `getKey` / `setKey` storage to avoid Android TV runtime crashes.
- v2.3 changed internal add/remove URLs to normal HTTPS action URLs so Android TV passes them back to the provider correctly.
- v2.5 imported existing normal Twitch CloudStream favorites into `Live Now` in read-only mode.
- v2.7 attempted experimental direct-play cards.
- v2.9 reverts Live Now to normal provider cards because direct-play cards rendered as an empty/black row on some Android TV builds.

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


## v2.9 note: existing CloudStream Twitch favorites

This build reads CloudStream's existing local Favorites list in read-only mode and automatically includes entries from the normal `Twitch` provider in the `Live Now` row. It does not modify or delete CloudStream's normal favorites. The visible `Live Now` row still only shows saved/imported streamers while they are currently live.

The `[Remove]` card only removes streamers saved directly by this custom plugin. If a streamer is still in CloudStream's normal Twitch favorites, remove it from the normal CloudStream Favorites/Library as well if you do not want it to appear in `Live Now`.


## v2.9 update

- The Home page still has only the `Live Now` row.
- If nobody is live, it shows a safe empty-state card instead of a blank black page or Help page.
- Reverts the experimental direct-play card type, which caused empty/black rows on some Android TV builds.
- Keeps best-effort refresh events after Add/Remove.


## v2.9 code-sweep changes

- Keeps the stable single `Live Now` row.
- Keeps offline saved favorites hidden from the home row.
- Improves Twitch/TwitchTracker URL normalization for imported CloudStream favorites.
- Removes unused homepage branches from the custom provider.
- Bumps the plugin version to 9 so CloudStream recognizes the update over v2.8.
