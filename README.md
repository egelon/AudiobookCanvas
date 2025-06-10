# Audiobook Canvas

Audiobook Canvas is an Android application that converts written text into a narrated audiobook. The app breaks a text file into manageable pieces, identifies the speaker for each line, generates speech audio, and stitches everything into a final audio file.

## Features

- **Text chunking and sanitizing** – The app reads a text file, splits it into small blocks, and cleans up OCR artifacts or extra whitespace. It can handle languages where the dialogue start and end markers are the same.
- **Character recognition** – For each text block, the app sends a request to an OpenAI completion endpoint to perform Named Entity Recognition. This determines which character speaks each line of dialogue.
- **Text to speech** – Lines can be synthesized either by Android's built‑in `TextToSpeech` or by OpenAI's speech synthesis endpoint. Voice samples are stored so they can be reused across projects.
- **Audio mixing** – Individual WAV files are combined into a single audio book using the `android_audio_mixer` library. Background music can be added in parallel with narration.
- **Room database** – Projects, text blocks, and generated audio files are persisted locally using Room. Repositories and view models provide a clean separation between data and UI layers.
- **Navigation architecture** – A single navigation graph describes the user flow from creating a project to listening to the final mix.

## External Dependencies

The app relies on several external services and libraries:

- **OpenAI APIs** – Used for character recognition and optional text‑to‑speech synthesis.
- **OkHttp** and **Gson** – Handle HTTP requests and JSON parsing.
- **android_audio_mixer** – Mixes multiple WAV tracks into a final audio file.
- **Android TextToSpeech** – Provides offline speech synthesis.

To enable OpenAI calls, users must provide their API key in the app settings.

## Repository Layout

```
app/                 # Android application module
├── src/main/java/   # Application code (repositories, view models, UI)
├── src/main/res/    # Layouts, navigation graph, strings
arch/                # Design documents and SQL model
build.gradle         # Project build configuration
```

## Building

This project uses Gradle. Run `./gradlew assembleDebug` to build the debug APK. Running the full unit test suite requires network access for Gradle to download dependencies.

## Getting Started

1. Clone the repository and open it in Android Studio.
2. Set your OpenAI API key in the app's preferences screen.
3. Create a new project inside the app and select a text file to process.
4. Review and generate character lines, then synthesize speech and mix the final audiobook.

