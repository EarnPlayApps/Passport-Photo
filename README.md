# Passport Photo

Malaysia-focused passport and document photo preparation app.

## Scope
- Malaysia only
- Passport Malaysia is the primary use case
- Requirement-specific photo preparation and compliance checking
- Local-first photo processing
- Free: AdMaven (publisher placement held until a real app-compatible placement is supplied)
- Pro: RM19.90 Lifetime via manual TNG activation
- No Google Play Billing
- No automatic photo upload

## Requirement policy
- Official Malaysian sources are preferred.
- Exact dimensions are only stored when the source explicitly states them.
- `OFFICIAL_EXPLICIT` means the official source provides an explicit requirement.
- `OFFICIAL_PARTIAL` means the official source confirms the photo requirement but does not provide all exact fields.
- No foreign-country requirement database is included.

## Build
GitHub Actions builds both debug and unsigned release APKs and validates release APK metadata and zip alignment. A signed production APK requires the project's real signing credentials; credentials are never hard-coded into the repository.

## QA
CI build validation is automated. Physical-device/emulator QA remains a separate gate when an Android device/emulator is available.