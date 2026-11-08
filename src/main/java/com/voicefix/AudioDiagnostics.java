package com.voicefix;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import java.util.ArrayList;
import java.util.List;

public final class AudioDiagnostics {
    private static final float[] SAMPLE_RATES = {48000F, 44100F, 32000F, 22050F, 16000F};

    private AudioDiagnostics() {}

    public record ScanReport(List<String> lines, int devicesFound, int compatibleDevices) {}

    public static ScanReport scan() {
        List<String> lines = new ArrayList<>();
        Mixer.Info[] devices;
        try {
            devices = AudioSystem.getMixerInfo();
        } catch (Throwable error) {
            lines.add("§c[VoiceFix] Java Sound aygit listesine erisemedi: " + clean(error));
            return new ScanReport(List.copyOf(lines), 0, 0);
        }

        int inputs = 0;
        int compatible = 0;
        lines.add("§6[VoiceFix] §fBulunan ses aygiti: " + devices.length);

        for (Mixer.Info info : devices) {
            Mixer mixer;
            try {
                mixer = AudioSystem.getMixer(info);
            } catch (Throwable error) {
                continue;
            }
            List<String> formats = supportedFormats(mixer);
            if (formats.isEmpty()) continue;

            inputs++;
            if (formats.stream().anyMatch(s -> s.startsWith("48000 Hz / 16-bit / mono"))) compatible++;
            lines.add("§e[VoiceFix] §f" + clean(info.getName()));
            for (String format : formats) lines.add("  §7- " + format);
        }

        if (inputs == 0) {
            lines.add("§c[VoiceFix] Mikrofon girisi sunan bir TargetDataLine bulunamadi.");
            lines.add("§7Isletim sistemi mikrofon iznini ve varsayilan giris aygitini kontrol et.");
        } else if (compatible == 0) {
            lines.add("§e[VoiceFix] 48 kHz / 16-bit / mono destekleyen giris bulunamadi.");
            lines.add("§7Simple Voice Chat ayarlarinda baska bir giris aygiti secmeyi dene.");
        } else {
            lines.add("§a[VoiceFix] En az bir aygit hatadaki 48 kHz mono formati destekliyor.");
            lines.add("§7Sorun aygit secimi, Java ses saglayicisi veya eski Voice Chat surumu olabilir.");
        }
        lines.add("§8Not: Tarama yalnizca aygitin formati ilan edip etmedigini kontrol eder; mikrofonu kayda acmaz.");
        return new ScanReport(List.copyOf(lines), inputs, compatible);
    }

    private static List<String> supportedFormats(Mixer mixer) {
        List<String> result = new ArrayList<>();
        try {
            for (Line.Info lineInfo : mixer.getTargetLineInfo()) {
                if (lineInfo instanceof DataLine.Info dataInfo
                    && TargetDataLine.class.isAssignableFrom(dataInfo.getLineClass())) {
                    for (AudioFormat format : dataInfo.getFormats()) addFormat(result, format);
                }
            }
            for (float rate : SAMPLE_RATES) {
                for (int channels : new int[]{1, 2}) {
                    AudioFormat format = new AudioFormat(rate, 16, channels, true, false);
                    if (mixer.isLineSupported(new DataLine.Info(TargetDataLine.class, format))) addFormat(result, format);
                }
            }
        } catch (Throwable ignored) {
            // A broken device must not prevent the rest of the scan.
        }
        return result.stream().distinct().limit(12).toList();
    }

    private static void addFormat(List<String> result, AudioFormat format) {
        if (format == null || !Float.isFinite(format.getSampleRate())) return;
        String rate = Math.round(format.getSampleRate()) + " Hz";
        String bits = format.getSampleSizeInBits() + "-bit";
        String channels = format.getChannels() == 1 ? "mono" : format.getChannels() + " kanal";
        String endian = format.isBigEndian() ? "big-endian" : "little-endian";
        String label = rate + " / " + bits + " / " + channels + " / " + endian;
        if (!result.contains(label)) result.add(label);
    }

    private static String clean(Throwable error) {
        String message = error.getMessage();
        return error.getClass().getSimpleName() + (message == null ? "" : ": " + message.replaceAll("[\\r\\n]", " "));
    }

    private static String clean(String value) {
        return value == null ? "Bilinmeyen aygit" : value.replaceAll("[\\r\\n]", " ");
    }
}
