package fireflasher.forgerplog.mixins;

import fireflasher.forgerplog.ChatLogger;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public abstract class ChatAccessMixin {

    @Inject(method = ("logChatMessage"), at = @At("HEAD"))
    public void logChatMessage(Component chat, GuiMessageTag tag, CallbackInfo ci){
        ChatLogger.chatFilter(chat.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }
}
