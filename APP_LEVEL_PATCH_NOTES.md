# App-level patch notes

This v2 plugin avoids editing CloudStream app internals by storing its own live-favorites list in Android SharedPreferences and exposing add/remove actions as provider cards.

A true app-level implementation could still add deeper integration, such as:

- Reading CloudStream's built-in Library/Favorites database directly.
- Sorting the built-in Library page by Twitch live status.
- Dimming offline Twitch cards in the standard Library UI.
- Adding real custom action buttons instead of provider-card actions.

Those changes require modifying the CloudStream app, not only shipping a normal `.cs3` provider plugin.


## v2.2 note

The provider-owned favorites list uses CloudStream DataStore helpers instead of SharedPreferences via ContextHelper. This avoids the Android TV runtime crash caused by `ContextHelper_jvmKt` not being available in the app runtime.


## v2.3 note

Internal action cards now use normal HTTPS marker URLs to avoid Android TV/CloudStream route normalization of custom schemes.


## v2.5 behavior

The plugin now does a best-effort read of CloudStream's local Favorites list via internal DataStoreHelper APIs and includes entries from the normal Twitch provider in the custom `Live Now` row. This is read-only and does not mutate CloudStream's existing favorites.


## v2.6 notes

This plugin now requests CloudStream home/library refresh events after plugin Add/Remove actions and uses ResumeWatchingResult-style cards in Live Now as a best-effort way to start playback on first click. Both are internal CloudStream behaviors and may require app-side patches if future builds change them.


## v2.7 note

The provider no longer exposes a Help action card. Empty Live Now states are intentionally empty so TV focus does not get stuck on a Help page.
