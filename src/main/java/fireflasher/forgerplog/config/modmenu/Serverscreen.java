package fireflasher.forgerplog.config.modmenu;

import com.mojang.blaze3d.vertex.PoseStack;
import fireflasher.forgerplog.ChatLogger;
import fireflasher.forgerplog.Forgerplog;
import fireflasher.forgerplog.config.DefaultConfig;
import fireflasher.forgerplog.config.json.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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

        Button reset = Button.builder(Component.translatable("rplog.config.serverscreen.reset_defaults"),
                button ->{
                    serverConfig.getServerDetails().getServerKeywords().clear();
                    serverConfig.getServerDetails().getServerKeywords().addAll(DefaultConfig.defaultKeywords);
                    Minecraft.getInstance().setScreen(new Serverscreen(previous, serverConfig));
                }).pos(this.width / 2 - this.width / 4 - 50, 13).width(100).build();

        Button done = Button.builder(Component.translatable("rplog.config.screen.done"),
                button -> {
                    Forgerplog.CONFIG.saveConfig();
                    onClose();
                }).pos(this.width / 2 + this.width / 4 - reset.getWidth() / 2 , 13).width(reset.getWidth()).build();

        this.addRenderableWidget(reset);
        this.addRenderableWidget(done);

        for (String keyword : keywords) {
            i = i + 20;
            Button delete = Button.builder(Component.translatable("rplog.config.screen.delete"),
                    button ->{
                        keywords.remove(keyword);
                        serverConfig.setServerDetails(serverDetails);
                        Minecraft.getInstance().setScreen(new Serverscreen(previous, serverConfig));
                    }).pos(this.width / 2 + this.width / 4 - reset.getWidth() / 2, i -5).width(reset.getWidth()).build();
            this.addRenderableWidget(delete);
        }

        i = i + 20;
        EditBox insert = new EditBox(this.font, this.width / 2 - this.width / 4 - 50, i, 100, CLICKABLEWIDGETHEIGHT, Component.nullToEmpty("")) {

        };

        Button add = Button.builder(Component.translatable("rplog.config.serverscreen.add_Keywords"),
                button ->{

                    if (!insert.getValue().isEmpty()) {
                        keywords.add(insert.getValue());
                        serverConfig.setServerDetails(serverDetails);
                        Minecraft.getInstance().setScreen(new Serverscreen(previous, serverConfig));
                    }

                }).pos(this.width / 2 + this.width / 4 - insert.getWidth() / 2, i).width(insert.getWidth()).build();

        this.addRenderableWidget(insert);
        this.addRenderableWidget(add);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 5, 0xffffff);
        List<String> keywords = serverConfig.getServerDetails().getServerKeywords();
        int i = 30;
        for(String keyword:keywords){
            i = i + 20;
            guiGraphics.drawCenteredString(this.font, Component.nullToEmpty(keyword), this.width / 2 - this.width / 4 , i ,0xffffff);
        }
        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose(){
        this.minecraft.setScreen(previous);
    }


}


