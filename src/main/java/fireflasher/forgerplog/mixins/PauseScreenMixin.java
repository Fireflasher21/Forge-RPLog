package fireflasher.forgerplog.mixins;

import fireflasher.forgerplog.config.modmenu.Optionsscreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IngameMenuScreen.class)
public abstract class PauseScreenMixin extends Screen {


    protected PauseScreenMixin(ITextComponent p_i51108_1_) {super(p_i51108_1_);}

    @Inject(method = ("createPauseMenu"), at = @At("HEAD"))
    public void createPauseMenu(CallbackInfo callbackInfo){
        Button accessModOption = new Button(0, 0, 35, 20, ITextComponent.nullToEmpty("RPL"), button -> {
            Minecraft.getInstance().setScreen(new Optionsscreen(this));
        });
        addButton(accessModOption);
    }
}
