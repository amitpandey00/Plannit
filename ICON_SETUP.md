# Plannit App Icon Setup

## Current Status
✅ Adaptive icon XML files created
✅ Icon foreground (calendar with checkmark) created
✅ Icon background color set to #FF6B35 (orange)

## To Generate Full Icon Set

### Option 1: Using Android Studio (Recommended)
1. Right-click on `app/src/main/res` folder
2. Select **New > Image Asset**
3. Choose **Launcher Icons (Adaptive and Legacy)**
4. Select **Image** as Icon Type
5. Upload your icon image (the orange calendar icon you showed)
6. Adjust padding and preview
7. Click **Next** then **Finish**

### Option 2: Using Online Tool
1. Visit: https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html
2. Upload your icon image
3. Set background color to #FF6B35
4. Download the generated zip file
5. Extract and copy all mipmap folders to `app/src/main/res/`

### Option 3: Manual Icon Sizes
If you have the icon PNG, create these sizes:
- **mdpi**: 48x48px
- **hdpi**: 72x72px
- **xhdpi**: 96x96px
- **xxhdpi**: 144x144px
- **xxxhdpi**: 192x192px

Save as `ic_launcher.png` and `ic_launcher_round.png` in each mipmap folder.

## Current Icon Design
- **Background**: Orange (#FF6B35)
- **Foreground**: White calendar with orange checkmark
- **Style**: Material Design adaptive icon

The icon will automatically adapt to different device shapes (circle, square, rounded square).
