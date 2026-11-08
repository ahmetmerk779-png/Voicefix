package com.voicefix;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public final class VoiceFixClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(literal("voicefix")
                .then(literal("scan").executes(context -> {
                    context.getSource().sendFeedback(Text.literal("§6[VoiceFix] Mikrofon aygitlari taraniyor..."));
                    AudioDiagnostics.ScanReport report = AudioDiagnostics.scan();
                    for (String line : report.lines()) {
                        context.getSource().sendFeedback(Text.literal(line));
                    }
                    return 1;
                }))
                .then(literal("help").executes(context -> {
                    context.getSource().sendFeedback(Text.literal("§6[VoiceFix] §f/voicefix scan §7- mikrofon ve PCM formatlarini kontrol eder."));
                    context.getSource().sendFeedback(Text.literal("§7Bu mod sunucuya veya diger oyunculara mudahale etmez."));
                    return 1;
                }))
            )
        );
    }
}
