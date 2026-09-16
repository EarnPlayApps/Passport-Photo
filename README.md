# Passport Photo

Malaysia-focused mobile app for preparing passport/travel-document photos from a camera or gallery image and producing a print-ready sheet.

## Product scope
- Passport Photo only; Malaysia-focused.
- User flow: choose photo type -> camera/gallery -> automatic preparation -> preview -> save/print.
- Official-source requirement profiles are used where exact requirements are verified.
- No universal 35 x 50 mm assumption when an official source does not state it.
- Adult Malaysian International Passport is treated separately because current Immigration guidance uses Facial Live Capture at the counter.
- Local-first photo processing; no automatic cloud upload in the core flow.

## Privacy and legal positioning
- Passport Photo is a third-party application and is not an official Government of Malaysia or Immigration Department application.
- The app does not guarantee acceptance by an authority; final acceptance follows the current authority requirement.
- Photos remain the user's property.
- Core processing is designed to happen on-device.
- Privacy Policy and Terms/Disclaimer are accessible from the home screen.
- Official sources and source dates should be maintained whenever requirement profiles are updated.

## Requirement policy
- Malaysia official sources are preferred.
- Exact dimensions are stored only when explicitly supported by the source.
- Current official requirements override legacy guidance; conflicts are not silently merged.
- Profiles with incomplete official dimensions must not invent a size.

## Build
GitHub Actions builds debug and unsigned release APKs and validates package metadata and alignment. A production-signed APK still requires real signing credentials; credentials are never hard-coded into the repository.
