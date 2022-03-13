package fireflasher.forgerplog.config.modmenu;

import com.mojang.blaze3d.vertex.PoseStack;
import fireflasher.forgerplog.ChatLogger;
import fireflasher.forgerplog.Forgerplog;
import fireflasher.forgerplog.config.DefaultConfig;
import fireflasher.forgerplog.config.json.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;


class Serverscreen extends Screen {

    private Screen previous;
    private ServerConfig serverConfig;
    static final int CLICKABLEWIDGETHEIGHT = 20;

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
        EditBox insert = new EditBox(this.font, this.width / 2 - this.width / 4 - 50, i, 100, CLICKABLEWIDGETHEIGHT, Component.nullToEmpty("Keyword")) {

        };

        Button add = new Button(this.width / 2 + this.width / 4 - insert.getWidth() / 2, i, insert.getWidth(), CLICKABLEWIDGETHEIGHT, Component.nullToEmpty("Add"),
                button ->{

                if (!insert.getValue().isEmpty()) {
                    keywords.add(insert.getValue());
                    serverConfig.setServerDetails(serverDetails);
                    Minecraft.getInstance().setScreen(new Serverscreen(previous, serverConfig));
                }

            });
        }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 5, 0xffffff);
        List<String> keywords = serverConfig.getServerDetails().getServerKeywords();
        int i = 30;
        for(String keyword:keywords){
            i = i + 30;
            drawCenteredString(poseStack, this.font, Component.nullToEmpty(keyword), this.width / 2 - this.width / 4 , i ,0xffffff);
        }
        super.render(poseStack, mouseX, mouseY, delta);
    }

    @Override
    public void onClose(){
        this.minecraft.setScreen(previous);
    }


}


