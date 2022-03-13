package fireflasher.forgerplog.config.modmenu;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import fireflasher.forgerplog.ChatLogger;
import fireflasher.forgerplog.Forgerplog;
import fireflasher.forgerplog.config.DefaultConfig;
import fireflasher.forgerplog.config.json.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.List;

import static fireflasher.forgerplog.config.modmenu.Optionsscreen.CLICKABLEWIDGETHEIGHT;


class Serverscreen extends Screen {

    private Screen previous;
    private ServerConfig serverConfig;

    Serverscreen(Screen previous, ServerConfig serverConfig) {
        super(Component.nullToEmpty(ChatLogger.getServerNameShortener(serverConfig.getServerDetails().getServerNames())));
        this.previous = previous;
        this.serverConfig = serverConfig;
    }

    @Override
    protected void init() {

        ServerConfig.ServerDetails serverDetails = serverConfig.getServerDetails();
        List<String> keywords = serverDetails.getServerKeywords();
        int i = 30;

        Button reset = new Button(this.width / 2 - this.width / 4 - 50, i, 100, CLICKABLEWIDGETHEIGHT, Component.nullToEmpty("Reset Defaults"),
                button ->{
                serverConfig.getServerDetails().getServerKeywords().clear();
                serverConfig.getServerDetails().getServerKeywords().addAll(DefaultConfig.defaultKeywords);
                Minecraft.getInstance().setScreen(new Serverscreen(previous, serverConfig));
            });

        Button done = new Button(this.width / 2 + this.width / 4 - reset.getWidth() / 2 , i, reset.getWidth(), CLICKABLEWIDGETHEIGHT, Component.nullToEmpty("Done"),
                button -> {
                Forgerplog.CONFIG.saveConfig();
                onClose();
            });

        for (String keyword : keywords) {
            i = i + 30;
            Button delete = new Button(this.width / 2 + this.width / 4 - reset.getWidth() / 2, i, reset.getWidth(), CLICKABLEWIDGETHEIGHT, Component.nullToEmpty("Löschen"),
                    button ->{
                    keywords.remove(keyword);
                    serverConfig.setServerDetails(serverDetails);
                    Minecraft.getInstance().setScreen(new Serverscreen(previous, serverConfig));
                });

        }

        i = i + 30;
        TextFieldWidget insert = new TextFieldWidget(textRenderer, this.width / 2 - this.width / 4 - 50, i, 100, CLICKABLEWIDGETHEIGHT, Text.of("Keyword")) {

            @Override
            public void appendNarrations(NarrationMessageBuilder builder) {
                return;
            }

        };

        ClickableWidget add = new ClickableWidget(this.width / 2 + this.width / 4 - insert.getWidth() / 2, i, insert.getWidth(), CLICKABLEWIDGETHEIGHT, Text.of("Add")) {
            @Override
            public void appendNarrations(NarrationMessageBuilder builder) {
                return;
            }

            @Override
            public void onClick(double mouseX, double mouseY) {

                if (!insert.getText().isEmpty()) {
                    keywords.add(insert.getText());
                    serverConfig.setServerDetails(serverDetails);
                    MinecraftClient.getInstance().setScreenAndRender(new Serverscreen(previous, serverConfig));
                }

            }
        };

        this.addDrawableChild(done);
        this.addDrawableChild(reset);
        this.addDrawableChild(insert);
        this.addDrawableChild(add);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 5, 0xffffff);
        List<String> keywords = serverConfig.getServerDetails().getServerKeywords();
        int i = 30;
        for(String keyword:keywords){
            i = i + 30;
            drawCenteredText(matrices, this.textRenderer, Text.of(keyword), this.width / 2 - this.width / 4 , i ,0xffffff);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose(){
        this.client.setScreen(previous);
    }


}


