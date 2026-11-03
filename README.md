# Captain Rojo — Build a Dream (B.A.D)

Native Android (Kotlin + Jetpack Compose) studio for personal branding and AI ad production.

- **Logo Studio** — prompt + style → AI logo concept (ChatGPT/DALL-E, Ideogram, Adobe Firefly, or local FLUX).
- **Ad Studio** — 3-scene ad → AI video (Runway/Veo/Pika/Kling/local Wan) + piper voiceover → mux.
- **Play Link** — wire the downloaded "Falcon" app to the Play Store (account, device install, config mapping).
- **Health** — verify environment: ffmpeg/piper/cloudflared/model reachability.

## What's in this repo

```
app/            Composable screens + androidx.compose navigation shell
  ui/screens/   LogoStudioScreen, AdStudioScreen, PlayLinkScreen, HealthScreen
  ui/components CommonButton/PrimCard/Chip
  ui/features/  ExportFeature (zip a project)
  repository/   ProjectRepository (kotlinx.serialization JSON)
  services/     ToolRunner, AppGenerationService, AppLinkService, AppHealthService
  state/        AppViewModel (Hilt + StateFlow)
  di/           AppModule
  models/       Project, Logo, Ad, AdScene
build.gradle, gradle.properties, settings.gradle, .gitignore
res/              mipmap (5 densities + round), drawable/splash_background.xml
gradlew / gradlew.bat
README / RELEASE.md          <-- this file
```

## Download the code

The repo is public at:
`https://github.com/iksterbo-design/captain-rojo-android`

On GitHub there's a green **"Code"** button at the top-right → **Download ZIP**. That's the source-only download.

## Download an installable APK (one-click)

To get a ready-to-install APK on the Fold 8:

1. **Build** (Windows, with JDK 17 + Android SDK in `PATH`):
   ```powershell
   cd CaptainRojo-Android
   .\gradlew assembleDebug     # debug APK
   # or for release:
   .\gradlew bundleRelease
   ```
   Output: `app\build\outputs\apk\release\app-release.aab` (release), or `app\build\outputs\apk\debug\app-debug.apk` (debug).

2. **Attach as a GitHub Release** (so it's a one-click download):
   ```powershell
   gh release create v1.0.0 --title "Captain Rojo v1.0" --generate-notes
   gh release upload v1.0.0 app\build\outputs\apk\release\app-release.aab
   ```
   Then open the release page and click the APK to download.

## Install on a Galaxy Fold (or any Android)
```powershell
.\gradlew installDebug        # debug APK
adb install app\build\outputs\apk\debug\app-debug.apk
```
On the Fold: enable **Developer options → USB debugging → Install via USB** first, then connect with a data cable and accept the USB debugging prompt.

## Docs
- `AI_TOOL_STACK.md` — AI logo/video tool map
- `BRANDING_PIPELINE.md` — Captain Rojo / B.A.D workflow stages
- `TROUBLESHOOTING.md` — Falcon/Play Store linking runbook
- `REFERENCES.md` — source list
