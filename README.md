# AquaSense

> The purifier that knows your water and who drinks it.

AquaSense is the ATOMQUEST 2026 Track 2 companion app for the edge-first smart
purifier described in `AquaSense Stage 1 Abstract - ATOMQUEST 2026.pptx.pdf`.
It brings together live water quality, automatic household hydration, and safe
remote care in a focused Android experience.

## What is included

- Native Android app built with Kotlin and Jetpack Compose.
- First-use onboarding that explains measurement, personal hydration, and safe
  remote care.
- Demo mode for a reliable stage presentation without Supabase, MQTT, or a
  Raspberry Pi.
- Supabase Auth sign-in/sign-up when credentials are configured.
- Fastify REST API client for devices, dashboard summaries, guest sessions,
  device claiming, and remote commands.
- Owner-aware flush/drain controls with an explicit confirmation and clear
  interlock copy.
- Responsive Compose layout with a hydration dashboard, live health cards,
  weekly insights, household activity, and privacy settings.
- Existing backend contract and tests under
  `aquasense-backend-main/aquasense-backend-main`.

## Project layout

```text
.
├── app/                         # Android Studio project module
│   └── src/main/java/com/aquasense/app/
│       ├── data/                # REST/Auth contract and demo data
│       ├── ui/                  # onboarding, auth, dashboard, insights
│       └── MainActivity.kt
├── aquasense-backend-main/      # Fastify + Supabase + MQTT backend
├── AquaSense Stage 1 Abstract...pdf
└── README.md
```

## Quick start: demo mode

1. Open this folder in Android Studio.
2. Let Android Studio sync the Gradle project.
3. Create an emulator with API 35 or connect an Android phone with USB
   debugging enabled.
4. Run the `app` configuration.
5. On first launch, step through onboarding and choose **Explore the demo
   mode**.

The demo shows a connected AquaSense One, live-looking TDS/tank/filter values,
hydration progress, household dispenses, insights, and safe simulated ACKs for
Flush filter and Drain tank. No credentials are required for this path.

## Connect the real backend

The backend guide is the source of truth for Supabase, database migrations,
Realtime, MQTT, and Raspberry Pi setup:

- `aquasense-backend-main/aquasense-backend-main/GUIDE.md`
- `aquasense-backend-main/aquasense-backend-main/README.md`

After the backend is running, create or edit `local.properties` in the project
root (Android Studio keeps this file out of Git):

```properties
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-client-anon-or-publishable-key
AQUASENSE_API_URL=http://10.0.2.2:3000
```

Use `http://10.0.2.2:3000` from the Android emulator for a backend running on
the development machine. Use the machine's LAN IP (for example,
`http://192.168.1.20:3000`) for a physical phone on the same network. Never put
the backend `SUPABASE_SERVICE_ROLE_KEY` or MQTT password in this file or the
mobile app.

When configured, the app signs users in through Supabase Auth, sends the JWT as
`Authorization: Bearer ...` to the Fastify `/v1` routes, and renders the
dashboard from the backend's `dashboard_summary` view. The refresh action is
the explicit live-data fallback while the backend's Supabase Realtime channel
is being wired for production push updates.

The app already accepts an optional `current_ph` field in a future dashboard
response and shows the calibrated demo value. The supplied Round 2 backend
schema currently publishes TDS, tank, filter, and temperature but not pH; add
that field to the backend/Supabase status contract when the pH sensor is ready
instead of treating the demo value as a live measurement.

## Backend checks

```bash
cd aquasense-backend-main/aquasense-backend-main
pnpm install
pnpm typecheck
pnpm test
pnpm build
```

The backend has its own `.env.example`; keep `.env` local. The public health
check is `curl http://localhost:3000/health`.

## App checks

From the project root with Android SDK/JDK available:

```bash
./gradlew :app:assembleDebug
```

Android Studio can also run the same build through **Build > Make Project**.
The supplied coding container does not expose the host Android SDK, so use the
Android Studio environment for the final APK/emulator smoke test.

## First-use demo script

1. Tap **Explore the demo mode**.
2. On **Overview**, point out the measured 86 ppm TDS, tank readiness, and
   automatic 2.41 L hydration total.
3. Open **Insights** to show the weekly intake rhythm and quality signals.
4. Return to **Overview**, open **Care**, and tap **Flush filter**. Confirm it
   and show the simulated command acknowledgement snackbar.
5. Open **Settings** to show the edge privacy and offline buffering promises.
6. For a real-device demo, configure the backend and use **Connect a purifier**
   with the UUID created by the Supabase seed step.

## Safety and privacy notes

The app only requests remote commands through the authenticated backend. The
hardware edge remains the safety authority: it must reject flush/drain while
dispensing, protect against dry runs, and keep on-device face embeddings local.
The mobile app does not receive or store face images.
