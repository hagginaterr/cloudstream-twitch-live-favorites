# v2.11 Patch Notes

Rollback-stable build based on v2.5. The only functional change is the Help-screen fix:

- Removed the Help route/page.
- Empty-state cards no longer open the Help page.
- Clicking an empty-state card now fails fast with a message rather than navigating away.
- Reverted later experimental changes, including direct-play cards and UI-refresh internals.

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
