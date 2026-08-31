# Magic Pro Camera

Android camera app prototype focused on manual Camera2 controls: ISO, shutter speed, white balance, exposure compensation, AF/manual focus, lens selection, grid and JPEG/RAW toggle UI.

## Build
Open the project in Android Studio (Ladybug or newer), let Gradle sync, then Run on the Honor Magic8 Pro.

## Important
Camera2 exposes only the manual controls that the phone's camera HAL actually supports. Some Honor camera modules may restrict RAW, manual focus, shutter speeds or specific lenses to certain camera IDs. The app detects capabilities at runtime and falls back safely.


## Presets V2
The app now includes presets for full sun, cloudy weather, sunrise, golden hour, blue hour, twilight, night, starry sky, indoor, artificial light, portrait and action. The “Auto intelligent” button selects a starting preset from the current local time. All preset values remain editable manually after selection.

Note: the preset is a starting point based on time of day, not a replacement for a light meter; actual exposure should be checked on the live preview.
