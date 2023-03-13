package fireflasher.forgerplog.config.modmenu;

import com.mojang.blaze3d.vertex.PoseStack;
import fireflasher.forgerplog.ChatLogger;
import fireflasher.forgerplog.Forgerplog;
import fireflasher.forgerplog.config.DefaultConfig;
import fireflasher.forgerplog.config.json.ServerConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Optionsscreen extends Screen {

    private Screen previous;
    static final int BUTTON_HEIGHT = 20;
    private final ServerConfig dummy = new ServerConfig("dummy", List.of("dummy"), List.of("dummy"));



    public Optionsscreen() {
        super(Component.translatable("rplog.config.optionscreen.title"));
    }

    public Optionsscreen(Screen previous) {
        super(Component.translatable("rplog.config.optionscreen.title"));
        this.previous = previous;
    }



    protected void init() {
        int i = 30;
        DefaultConfig defaultConfig = Forgerplog.CONFIG;
        List<ServerConfig> serverConfigList = defaultConfig.getList();
        if (serverConfigList.isEmpty()) {
            serverConfigList.add(dummy);
        }
        for (ServerConfig server : serverConfigList) {
            i = i + 25;
            Button serverbutton = Button.builder(Component.nullToEmpty(ChatLogger.getServerNameShortener(server.getServerDetails().getServerNames())),
                    button -> {
                        Minecraft.getInstance().setScreen(new Serverscreen(Minecraft.getInstance().screen, server));
                    }).pos(this.width / 2 - this.width / 4 - 50, i).width(100).build();


            Button delete = Button.builder(Component.translatable("rplog.config.screen.delete"),
                    button -> {
                        Minecraft.getInstance().setScreen(new Verification(Minecraft.getInstance().screen, defaultConfig, server));
                    }).pos(this.width / 2 + this.width / 4 - serverbutton.getWidth() / 2, i).width(serverbutton.getWidth()).build();

            if (!serverConfigList.contains(dummy)) {
                this.addRenderableWidget(serverbutton);
                this.addRenderableWidget(delete);
            }
        }
        serverConfigList.remove(dummy);

        Button addServer = Button.builder(Component.translatable("rplog.config.optionscreen.add_Server"),
                button ->{
                    if (Minecraft.getInstance().getCurrentServer() != null && !Minecraft.getInstance().getCurrentServer().isLan()) {
                        String[] ip = new String[2];
                        ip[0] = Minecraft.getInstance().getCurrentServer().ip;
                        ip[1] = Minecraft.getInstance().getCurrentServer().name;

                        ip = ChatLogger.getIP(ip[0], ip[1]);

                        defaultConfig.addServerToList(ip[0], ip[1]);
                        defaultConfig.loadConfig();
                        Minecraft.getInstance().setScreen(new Optionsscreen(previous));
                    }
                }).pos(this.width / 2 - this.width / 4 - 50, 13).width(100).build();

        Button defaultconfigbutton = Button.builder(Component.translatable("rplog.config.screen.defaults"),
                button ->{
                    ServerConfig defaults = new ServerConfig("Defaults",List.of("Defaults"),Forgerplog.CONFIG.getKeywords());
                    Minecraft.getInstance().setScreen(new Serverscreen(Minecraft.getInstance().screen, defaults));
                }).pos(this.width / 2 + this.width / 4 - 50, 13).width(100).build();

        Button done = Button.builder(Component.translatable("rplog.config.screen.done"),
                button -> {
                    onClose();
                    defaultConfig.loadConfig();
                }).pos(this.width / 2 + this.width / 4 - 50, this.height - 30).width(100).build();

        Button openFolder = Button.builder(Component.translatable("rplog.config.optionscreen.open_LogFolder"),
                button -> {
                        Util.getPlatform().openFile(new File(Forgerplog.getFolder()));
                }).pos(this.width / 2 - this.width / 4 - 50, this.height - 30).width(100).build();

        this.addRenderableWidget(addServer);
        this.addRenderableWidget(done);
        this.addRenderableWidget(defaultconfigbutton);
        this.addRenderableWidget(openFolder);
    }


    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        Component serverlist = Component.translatable("rplog.config.optionscreen.configuration_Servers");
        Component deleteServer = Component.translatable("rplog.config.optionscreen.delete_Servers");
        this.renderBackground(poseStack);
        drawCenteredString(poseStack,this.font, this.title, this.width / 2, 18, 0xffffff);
        drawCenteredString(poseStack, this.font, serverlist, this.width / 2 - this.width / 4, 40, 0xffffff);
        drawCenteredString(poseStack, this.font, deleteServer, this.width / 2 + this.width / 4, 40, 0xffffff);
        super.render(poseStack, mouseX,mouseY,partialTicks);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }


    public class Verification extends Screen{

        private Screen previous;
        private DefaultConfig defaultConfig;
        private ServerConfig serverConfig;

        Verification(Screen previous, DefaultConfig defaultConfig, ServerConfig serverConfig){
            super(Component.nullToEmpty(""));
            this.previous = previous;
            this.defaultConfig = defaultConfig;
            this.serverConfig = serverConfig;
        }

        public void init(){
            Button delete = Button.builder(Component.translatable("rplog.config.optionscreen.verification.delete"),
                    button -> {
                        defaultConfig.removeServerFromList(serverConfig);
                        Minecraft.getInstance().setScreen(new Optionsscreen(previous));
                    }).pos(this.width / 2 - this.width / 4 - 50, this.height / 2).width(100).build();


            Button abort = Button.builder(Component.translatable("rplog.config.optionscreen.verification.cancel"),
                    button -> {
                        onClose();

                    }).pos(this.width / 2 + this.width / 4 - 50, this.height / 2).width(100).build();

            this.addRenderableWidget(delete);
            this.addRenderableWidget(abort);

        }

        public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
            this.renderBackground(poseStack);
             Component server_delete_message = Component.translatable("rplog.config.optionscreen.verification.message");
            drawCenteredString(poseStack, this.font, server_delete_message, this.width / 2, this.height / 2 - this.height / 4, 0xffffff);
            super.render(poseStack, mouseX, mouseY, delta);
        }

        @Override
        public void onClose(){
            this.minecraft.setScreen(previous);
        }

    }
}
