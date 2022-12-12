package fireflasher.forgerplog.mixins;

import fireflasher.forgerplog.config.modmenu.Optionsscreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Logger;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {

    protected PauseScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

    @Inject(method = ("createPauseMenu"), at = @At("HEAD"))
    public void createPauseMenu(CallbackInfo callbackInfo){
        Button accessModOption = new Button(0, 0, 35, 20, Component.nullToEmpty("RPL"), button -> {
            Minecraft.getInstance().setScreen(new Optionsscreen(this));
        });
        addRenderableWidget(accessModOption);
        Logger.getLogger("Debug").info("ModButton");
    }
}