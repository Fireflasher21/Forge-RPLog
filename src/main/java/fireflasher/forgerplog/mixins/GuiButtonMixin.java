package fireflasher.forgerplog.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import fireflasher.forgerplog.config.modmenu.Optionsscreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ChatOptionsScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class GuiButtonMixin{

    @Shadow protected abstract void lambda$createPauseMenu$0(Button par1);

    @Inject(method = "init", at = @At("HEAD"),cancellable = true)
    private void init(CallbackInfo ci){
        Button accessModOption = new Button(0, 0, 10, 10, Component.nullToEmpty("RPL"), button -> {
            Minecraft.getInstance().setScreen(new Optionsscreen());
        });
        this.lambda$createPauseMenu$0(accessModOption);


    }

}
