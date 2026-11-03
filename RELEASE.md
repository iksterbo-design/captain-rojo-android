# Captain Rojo — Play Store Release Checklist

## 1. Build the AAB
```bash
cd CaptainRojo-Android
./gradlew bundleRelease
```
Output: `app/build/outputs/bundle/release/app-release.aab`

## 2. Prepare the store listing
- **Title:** Captain Rojo
- **Short description:** AI logo + ad-video studio for Build a Dream (B.A.D).
- **Full description:** Logo studio (ChatGPT / Ideogram / Firefly / local FLUX),
  ad video (Runway / Veo / Pika / Kling / local Wan) with piper voiceover,
  Play Store linking, and environment health checks.
- **App icon / screenshots:** use `res/mipmap` + record the 4 top tabs.

## 3. Publish to Google Play Console
1. Go to https://play.google.com/console → Create app → "Captain Rojo".
2. **Release**: App bundles (`.aab`) → Upload `app/build/outputs/bundle/release/app-release.aab`.
3. **Setup** → Store listing, Content rating, Data safety, Target API 34.
4. **Review** → Submit for production.

## 4. Internal-test dry run (before production)
```bash
./gradlew installRelease
adb shell monkey -p com.rojo.app -c android.intent.category.LAUNCHER 1
```
Then promote the same `.aab` to Internal testing → production.

## 5. Versioning
- `app/build.gradle`: bump `versionCode` (integer) + `versionName` (string).
- Release flow: `bundleRelease` → upload `.aab` → Submit.
