package org.gamesdonewr0ng.loot_locator.client.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.gamesdonewr0ng.loot_locator.client.LootLocatorClient;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Unique
    Logger logger = LootLocatorClient.INSTANCE.LOGGER;

    @Inject(method = "onGameMessage", at = @At("RETURN"))
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo info) {
        Text message = packet.content();
        if (message instanceof MutableText mutableText && mutableText.getContent() instanceof TranslatableTextContent text) {
            if ("commands.seed.success".equals(text.getKey()) && text.getArgs().length == 1)  {
                try {
                    MutableText m = (MutableText) text.getArgs()[0];
                    TranslatableTextContent t = (TranslatableTextContent) m.getContent();
                    PlainTextContent.Literal l = (PlainTextContent.Literal) ((MutableText) t.getArgs()[0]).getContent();
                    String str = l.string();

                    LootLocatorClient.INSTANCE.seed = Long.parseLong(str);
                    logger.info("Set the world seed to: {}", str);

                } catch (Exception e)
                {
                    logger.warn("Failed to read the world seed from '{}'", text.getArgs()[0]);
                }
            }
        }
    }
}
