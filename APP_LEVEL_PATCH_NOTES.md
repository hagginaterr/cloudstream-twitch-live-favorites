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
