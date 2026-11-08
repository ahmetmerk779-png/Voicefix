# VoiceFix

VoiceFix is a client-side Fabric diagnostic mod for Minecraft **1.21.11**. It investigates Java Sound microphone capture compatibility, including:

```
No line matching interface TargetDataLine supporting format
PCM_SIGNED 48000.0 Hz, 16 bit, mono, 2 bytes/frame,
little-endian is supported.
```

## Features

- Lists Java Sound devices that expose microphone capture lines.
- Checks advertised PCM formats at 48 kHz, 44.1 kHz, 32 kHz, 22.05 kHz and 16 kHz.
- Reports whether a device advertises the exact 48 kHz / 16-bit / mono format from the error.
- Does not open the microphone or record audio.
- Client-side only; it does not modify servers or other players.

## Commands

- `/voicefix scan` — scan microphone devices and supported capture formats.
- `/voicefix help` — show command help.

## Important limitation

VoiceFix is a diagnostic and compatibility helper, not a replacement audio driver. Java Sound providers and operating-system permissions determine which capture formats are available. This initial version does not patch Simple Voice Chat's internal capture implementation or force an unsupported format.

For Minecraft 1.21.11, install a current Simple Voice Chat Fabric release for that exact Minecraft version. The 2.6.8 release notes mention audio-device error-handling improvements; newer 1.21.11 releases are available. Never install a build for a different Minecraft version.

If the scan finds no microphone input, check operating-system microphone privacy permission, the selected input device, and whether another application has exclusive control of the microphone.

## Build

Requirements: JDK 21 and Gradle 9.2.1.

```bash
gradle build
```

The mod JAR is generated in `build/libs/`.

## Install

1. Install Fabric Loader for Minecraft 1.21.11.
2. Install Fabric API for Minecraft 1.21.11.
3. Put `voicefix-1.0.0.jar` in the client `mods` folder.
4. Launch the game and run `/voicefix scan`.

## License

MIT
