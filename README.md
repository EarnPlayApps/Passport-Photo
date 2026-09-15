# Passport Photo

Malaysia-focused passport and document photo preparation app.

## Scope
- Malaysia only
- Passport Malaysia is the primary use case
- Requirement-specific photo preparation and compliance checking
- Local-first photo processing
- Free: AdMaven configuration gate
- Pro: RM19.90 Lifetime via manual TNG activation

## Build
Core Android flow is implemented incrementally. Pull-request CI is the final build gate before an APK is considered ready.

## QA
The QA workflow builds the debug APK with Android SDK 35 and uploads the APK artifact on successful builds.
