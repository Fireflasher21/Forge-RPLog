package fireflasher.forgerplog.config.modmenu;

import com.mojang.blaze3d.matrix.MatrixStack;
import fireflasher.forgerplog.ChatLogger;
import fireflasher.forgerplog.Forgerplog;
import fireflasher.forgerplog.config.DefaultConfig;
import fireflasher.forgerplog.config.json.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.List;


class Serverscreen extends Optionsscreen {

    private Screen previous;
    private ServerConfig serverConfig;
    static final int CLICKABLEWIDGETHEIGHT = 20;

    Serverscreen(Screen previous, ServerConfig serverConfig) {
        super(previous, ChatLogger.getServerNameShortener(serverConfig.getServerDetails().getServerNames()));
        this.previous = previous;
        this.serverConfig = serverConfig;
    }

    @Override
    protected void init() {

        ServerConfig.ServerDetails serverDetails = serverConfig.getServerDetails();
        List<String> keywords = serverDetails.getServerKeywords();
        int i = 30;

        Button reset = new Button(this.width / 2 - this.width / 4 - 50, 13, 100, CLICKABLEWIDGETHEIGHT, new TranslationTextComponent("rplog.config.serverscreen.reset_defaults"),
                button ->{
                    serverConfig.getServerDetails().getServerKeywords().clear();
                    serverConfig.getServerDetails().getServerKeywords().addAll(DefaultConfig.defaultKeywords);
                    Minecraft.getInstance().setScreen(new Serverscreen(previous, serverConfig));
                });

        Button done = new Button(this.width / 2 + this.width / 4 - reset.getWidth() / 2 , 13, reset.getWidth(), CLICKABLEWIDGETHEIGHT, new TranslationTextComponent("rplog.config.screen.done"),
                button -> {
                    Forgerplog.CONFIG.saveConfig();
                    onClose();
                });

        this.addButton(reset);
        this.addButton(done);

        for (String keyword : keywords) {
            i = i + 20;
            Button delete = new Button(this.width / 2 + this.width / 4 - reset.getWidth() / 2, i -5 , reset.getWidth(), CLICKABLEWIDGETHEIGHT, new TranslationTextComponent("rplog.config.screen.delete"),
                    button ->{
                        keywords.remove(keyword);
                        Minecraft.getInstance().setScreen(new Serverscreen(previous, serverConfig));
                    });
            TextFieldWidget text = new TextFieldWidget(this.font, this.width / 2 - this.width / 4, i, reset.getWidth(), CLICKABLEWIDGETHEIGHT, new StringTextComponent(keyword));
            this.addButton(delete);
            this.addWidget(text);
        }

        i = i + 20;
        TextFieldWidget insert = new TextFieldWidget(this.font, this.width / 2 - this.width / 4 - 50, i, 100, CLICKABLEWIDGETHEIGHT, ITextComponent.nullToEmpty("")) {

        };

        Button add = new Button(this.width / 2 + this.width / 4 - insert.getWidth() / 2, i, insert.getWidth(), CLICKABLEWIDGETHEIGHT, new TranslationTextComponent("rplog.config.serverscreen.add_Keywords"),
                button ->{

                    if (!insert.getValue().isEmpty()) {
                        keywords.add(insert.getValue());
                        serverConfig.setServerDetails(serverDetails);
                        Minecraft.getInstance().setScreen(new Serverscreen(previous, serverConfig));
                    }

                });

        this.addButton(insert);
        this.addButton(add);
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 5, 0xffffff);
        List<String> keywords = serverConfig.getServerDetails().getServerKeywords();
        int i = 30;

        for(String keyword:keywords){
            i = i + 20;
            drawCenteredString(poseStack, this.font, ITextComponent.nullToEmpty(keyword), this.width / 2 - this.width / 4 , i ,0xffffff);
        }
        super.render(poseStack, mouseX, mouseY, delta);
    }

    @Override
    public void onClose(){
        this.minecraft.setScreen(previous);
    }


}


