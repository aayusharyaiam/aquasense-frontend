# AquaSense Android app

This module is a standard Android Studio project using Kotlin, Jetpack Compose,
Material 3, and the Android platform HTTP client. It intentionally avoids a
second networking framework so the backend contract is easy to inspect and
change during the hackathon.

## Screens

- **Onboarding:** three short pages for first-time users.
- **Auth:** Supabase email/password sign-in and sign-up, plus a safe demo path.
- **Overview:** hydration goal, live TDS/filter/tank cards, household activity,
  and owner-only remote care.
- **Insights:** weekly intake bars, quality signals, and recent dispenses.
- **Settings:** purifier connection, account, privacy, offline sync, and sign
  out.

## Configuration

Read the root `README.md` for the complete setup. `local.properties` keys are
read by `app/build.gradle.kts` into `BuildConfig`:

```properties
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=...
AQUASENSE_API_URL=http://10.0.2.2:3000
```

Only the Supabase client key belongs in the app. The backend service-role key
and MQTT credentials must remain on the Fastify server.
