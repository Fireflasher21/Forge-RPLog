package fireflasher.forgerplog.mixins;

import fireflasher.forgerplog.ChatLogger;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NewChatGui.class)
public abstract class ChatAccessMixin {

    @Inject(method="addMessage(Lnet/minecraft/util/text/ITextComponent;)V", at = @At("HEAD"))
    public void logChatMessage(ITextComponent chat, CallbackInfo ci){
        ChatLogger.chatFilter(chat.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }
}
