# Build Errors - Quick Fix

## Issue
After adding Google Calendar integration dependencies, you're seeing "Unresolved reference" errors for:
- Room (androidx.room)
- Google Sign-In (com.google.android.gms)
- Retrofit (com.squareup.retrofit2)
- Gson (com.google.code.gson)

## Root Cause
Gradle hasn't synced the new dependencies yet. The code is correct, but the libraries haven't been downloaded.

## Solution

### Option 1: Sync Gradle (Recommended)
1. In Android Studio, click **File** → **Sync Project with Gradle Files**
2. Or click the "Sync Now" banner at the top of the editor
3. Wait for sync to complete (may take 1-2 minutes)
4. Errors should disappear

### Option 2: Rebuild Project
1. Click **Build** → **Clean Project**
2. Wait for clean to finish
3. Click **Build** → **Rebuild Project**
4. Wait for rebuild to complete

### Option 3: Invalidate Caches
If sync doesn't work:
1. Click **File** → **Invalidate Caches**
2. Check "Invalidate and Restart"
3. Click **Invalidate and Restart**
4. Wait for Android Studio to restart
5. Gradle will auto-sync

### Option 4: Command Line
If Android Studio sync fails, try command line:

```bash
# Windows
.\gradlew clean build --refresh-dependencies

# Mac/Linux
./gradlew clean build --refresh-dependencies
```

## Verify Dependencies

After sync, verify in `build.gradle.kts` that you see:

```kotlin
dependencies {
    // ... existing dependencies ...
    
    // Google Sign-In & Auth
    implementation(libs.play.services.auth)
    implementation(libs.google.api.client.android)
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.gson)
}
```

## Common Issues

### Issue: "Failed to resolve" errors
**Solution**: Check internet connection, Gradle may need to download libraries

### Issue: Version conflicts
**Solution**: Check `gradle/libs.versions.toml` for correct versions:
- googleSignIn = "21.2.0"
- retrofit = "2.9.0"
- okhttp = "4.12.0"
- gson = "2.10.1"

### Issue: KSP errors
**Solution**: Room requires KSP. Verify in `build.gradle.kts`:
```kotlin
plugins {
    // ...
    alias(libs.plugins.ksp)
}
```

## Expected Build Time
- First sync: 2-5 minutes (downloading dependencies)
- Subsequent builds: 30-60 seconds

## After Successful Sync

You should see:
- ✅ No red underlines in code
- ✅ Build output shows "BUILD SUCCESSFUL"
- ✅ All imports resolved
- ✅ Database version updated to 2

## Next Steps

Once build succeeds:
1. Run the app to test database migration
2. Verify no runtime errors
3. Continue with Phase 2 implementation (see IMPLEMENTATION_STATUS.md)

## Still Having Issues?

If errors persist after sync:
1. Check `Build Output` tab for specific error messages
2. Verify all new files were created correctly
3. Check for typos in package names
4. Ensure you're using Android Studio Hedgehog or newer
5. Try deleting `.gradle` folder and re-syncing

## Contact
If you continue to have issues, share:
- Full error message from Build Output
- Android Studio version
- Gradle version
- JDK version
