package de.maxhenkel.shim;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Loads the bundled Android ARM64 audio bridge from the mod JAR. */
public final class NativeAudio {
    private static volatile boolean loaded;
    private static volatile String loadError;
    private static File extractedLibrary;

    static {
        loadBundledLibrary();
    }

    private NativeAudio() {}

    private static synchronized void loadBundledLibrary() {
        if (loaded) return;
        try (InputStream input = NativeAudio.class.getResourceAsStream("/natives/arm64-v8a/libaudioshim.so")) {
            if (input == null) {
                throw new IOException("Bundled ARM64 native library is missing from the VoiceFix JAR");
            }
            File directory = new File(System.getProperty("java.io.tmpdir", "."), "voicefix-native");
            if (!directory.isDirectory() && !directory.mkdirs()) {
                throw new IOException("Cannot create native extraction directory: " + directory);
            }
            File library = new File(directory, "libaudioshim.so");
            File temporary = File.createTempFile("audioshim-", ".so", directory);
            try (FileOutputStream output = new FileOutputStream(temporary)) {
                byte[] buffer = new byte[16384];
                int count;
                while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
                output.getFD().sync();
            }
            if (!temporary.setReadable(true, true) || !temporary.setExecutable(true, true)) {
                temporary.delete();
                throw new IOException("Cannot set permissions on extracted native library");
            }
            if (library.exists() && !library.delete()) {
                temporary.delete();
                throw new IOException("Cannot replace previous native library");
            }
            if (!temporary.renameTo(library)) {
                temporary.delete();
                throw new IOException("Cannot move native library into place");
            }
            System.load(library.getAbsolutePath());
            extractedLibrary = library;
            loaded = true;
        } catch (Throwable error) {
            loadError = error.getClass().getSimpleName() + ": " + String.valueOf(error.getMessage());
        }
    }

    public static boolean isAvailable() { return loaded; }
    public static String getLoadError() { return loadError; }

    public static native int open(int sampleRate, int framesPerBuffer);
    public static native int start();
    public static native int stop();
    public static native int close();
    public static native int read(byte[] buffer, int offset, int length);
}
