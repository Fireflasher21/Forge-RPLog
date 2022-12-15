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

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {

    protected PauseScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

    @Inject(method = ("createPauseMenu"), at = @At("HEAD"))
    public void createPauseMenu(CallbackInfo callbackInfo){
        Component rpl = Component.literal("RPL");
        Button accessModOption = Button.builder(rpl, button -> {
            Minecraft.getInstance().setScreen(new Optionsscreen(this));
        }).pos(0, 0).width(35).build();
        addRenderableWidget(accessModOption);
    }
}
