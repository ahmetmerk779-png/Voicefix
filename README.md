# VoiceFix

VoiceFix is a client-side Fabric mod for **Minecraft 1.21.11**, targeting **Android ARM64 on PojavLauncher**. It supplies a Java Sound SPI bridge for Simple Voice Chat.

## Audio backend

- Microphone capture through Android AAudio (mono, signed 16-bit PCM).
- Speaker playback through Android OpenSL ES (mono/stereo, signed 16-bit PCM).
- Java integration through `javax.sound.sampled.spi.MixerProvider`.
- ARM64 `libaudioshim.so` bundled in the mod JAR and extracted automatically on first use.

The implementation follows the Android Audio Shim architecture. Upstream project: https://github.com/Hhhrty2/android-audio-shim (MIT).

## Requirements

- Android 8.0+ (AAudio API 26+).
- ARM64 / arm64-v8a.
- PojavLauncher, Minecraft 1.21.11, Fabric Loader and a compatible Simple Voice Chat Fabric build.
- Microphone permission granted to PojavLauncher in Android settings.

## Install

1. Download the VoiceFix JAR from the GitHub Actions artifact.
2. Put it in the instance `mods` folder.
3. Launch Minecraft.
4. Open Simple Voice Chat settings and select **Android AudioRecord** as the microphone.
5. Test microphone and speaker.

If Android blocks microphone access, grant the microphone permission to PojavLauncher in Android app settings. VoiceFix cannot grant Android permissions itself.

If Simple Voice Chat reports errors from its optional desktop native backend, set `use_natives=false` in `voicechat-client.properties` if that option exists in your installed version. VoiceFix provides its own Java Sound bridge.

## Build

Requirements: JDK 21, Gradle 9.2.1 and Android NDK 27.2.12479018. Set `ANDROID_NDK_HOME`, then run:

```bash
gradle clean build
```

The JAR is generated in `build/libs/`; the native library is built for `arm64-v8a` and packaged under `natives/arm64-v8a/`.

## Limitations

- Android ARM64 only; x86 Android and desktop operating systems are not targeted.
- Simple Voice Chat is the compatibility target; other voice mods are not guaranteed.
- Android must grant PojavLauncher microphone permission.
- A successful compile does not prove audio routing on every phone; real-device testing is required.

## License

MIT. The audio bridge is based on the MIT-licensed Android Audio Shim project by Hhhrty2. See the upstream repository for original attribution.
