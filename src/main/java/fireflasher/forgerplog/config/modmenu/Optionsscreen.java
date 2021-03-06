package fireflasher.forgerplog.config.modmenu;

import com.mojang.blaze3d.vertex.PoseStack;
import fireflasher.forgerplog.ChatLogger;
import fireflasher.forgerplog.Forgerplog;
import fireflasher.forgerplog.config.DefaultConfig;
import fireflasher.forgerplog.config.json.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

public class Optionsscreen extends Screen {

    private Screen previous;
    static final int BUTTON_HEIGHT = 20;
    private final ServerConfig dummy = new ServerConfig("dummy", List.of("dummy"), List.of("dummy"));



    public Optionsscreen() {
        super(new TranslatableComponent("rplog.config.optionscreen.title"));
    }

    public Optionsscreen(Screen previous) {
        super(new TranslatableComponent("rplog.config.optionscreen.title"));
        this.previous = previous;
    }



    protected void init() {
        int i = 50;
        DefaultConfig defaultConfig = Forgerplog.CONFIG;
        List<ServerConfig> serverConfigList = defaultConfig.getList();
        if (serverConfigList.isEmpty()) {
            serverConfigList.add(dummy);
        }
        for (ServerConfig server : serverConfigList) {
            i = i + 30;
            Button serverbutton = new Button(this.width / 2 - this.width / 4 - 50, i, 100, BUTTON_HEIGHT, Component.nullToEmpty(ChatLogger.getServerNameShortener(server.getServerDetails().getServerNames())),
                    button -> {
                        Minecraft.getInstance().setScreen(new Serverscreen(Minecraft.getInstance().screen, server));
                    });


            Button delete = new Button(this.width / 2 + this.width / 4 - serverbutton.getWidth() / 2, i, serverbutton.getWidth(), BUTTON_HEIGHT, new TranslatableComponent("rplog.config.delete"),
                    button -> {
                        Minecraft.getInstance().setScreen(new Verification(Minecraft.getInstance().screen, defaultConfig, server));
                    });

            if (!serverConfigList.contains(dummy)) {
                this.addRenderableWidget(serverbutton);
                this.addRenderableWidget(delete);
            }
        }
        serverConfigList.remove(dummy);

        Button addServer = new Button(this.width / 2 - this.width / 4 - 50, 30, 100, BUTTON_HEIGHT, new TranslatableComponent("rplog.config.optionscreen.add_Server"),
                button ->{
                    if (Minecraft.getInstance().getCurrentServer() == null || Minecraft.getInstance().getCurrentServer().isLan()) {
                    } else {
                        String ip1 = Minecraft.getInstance().getCurrentServer().ip;
                        String servername1 = Minecraft.getInstance().getCurrentServer().name;
                        String ip = ip1.split("/")[1];
                        String servername = servername1.toString().split("/")[0];
                        ip = ip.split(":")[0];
                        defaultConfig.addServerToList(ip, servername);
                        Minecraft.getInstance().setScreen(new Optionsscreen(previous));
                    }
                });


        Button done = new Button(this.width / 2 + this.width / 4 - addServer.getWidth() / 2, 30, addServer.getWidth(), BUTTON_HEIGHT, new TranslatableComponent("rplog.config.screen.done"),
                button -> {
                    onClose();
                    defaultConfig.loadConfig();
                });

        Button defaultconfigbutton = new Button(this.width / 2 + - 30 , 30, 60, BUTTON_HEIGHT,  new TranslatableComponent("rplog.config.screen.defaults"),
                button ->{
                    ServerConfig defaults = new ServerConfig("Defaults",List.of("Defaults"),Forgerplog.CONFIG.getKeywords());
                    Minecraft.getInstance().setScreen(new Serverscreen(Minecraft.getInstance().screen, defaults));
                });

        this.addRenderableWidget(addServer);
        this.addRenderableWidget(done);
        this.addRenderableWidget(defaultconfigbutton);
    }


    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        TranslatableComponent serverlist = new TranslatableComponent("rplog.config.optionscreen.configuration_Servers");
        TranslatableComponent deleteServer = new TranslatableComponent("rplog.config.optionscreen.delete_Servers");
        this.renderBackground(poseStack);
        drawCenteredString(poseStack,this.font, this.title, this.width / 2, 5, 0xffffff);
        drawCenteredString(poseStack, this.font, serverlist, this.width / 2 - this.width / 4, 60, 0xffffff);
        drawCenteredString(poseStack, this.font, deleteServer, this.width / 2 + this.width / 4, 60, 0xffffff);
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
            Button delete = new Button(this.width / 2 - this.width / 4 - 50, this.height / 2, 100, BUTTON_HEIGHT, new TranslatableComponent("rplog.config.optionscreen.verification.delete"),
                    button -> {
                        defaultConfig.removeServerFromList(serverConfig);
                        Minecraft.getInstance().setScreen(new Optionsscreen(previous));
                    });


            Button abort = new Button(this.width / 2 + this.width / 4 - 50, this.height / 2,100, BUTTON_HEIGHT, new TranslatableComponent("rplog.config.optionscreen.verification.cancel"),
                    button -> {
                        onClose();

                    });

            this.addRenderableWidget(delete);
            this.addRenderableWidget(abort);

        }

        public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
            this.renderBackground(poseStack);
            String server_delete_message = String.valueOf(new TranslatableComponent("rplog.config.optionscreen.verification.message"));
            drawCenteredString(poseStack, this.font, server_delete_message, this.width / 2, this.height / 2 - this.height / 4, 0xffffff);
            super.render(poseStack, mouseX, mouseY, delta);
        }

        @Override
        public void onClose(){
            this.minecraft.setScreen(previous);
        }

    }
}
