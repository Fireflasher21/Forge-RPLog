package fireflasher.forgerplog.config.modmenu;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import fireflasher.forgerplog.ChatLogger;
import fireflasher.forgerplog.Forgerplog;
import fireflasher.forgerplog.config.DefaultConfig;
import fireflasher.forgerplog.config.json.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.loading.targets.FMLClientDevLaunchHandler;
import net.minecraftforge.fml.loading.targets.FMLClientLaunchHandler;

import java.util.List;

public class Optionsscreen extends Screen {

    private Screen previous;
    static final int CLICKABLEWIDGETHEIGHT = 20;
    private final ServerConfig dummy = new ServerConfig("dummy", List.of("dummy"), List.of("dummy"));



    public Optionsscreen() {
        super(Component.nullToEmpty("RPlog Options"));
    }
    public Optionsscreen(Screen previous) {
        super(Component.nullToEmpty("RPlog Options"));
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
            Button serverbutton = new Button(this.width / 2 - this.width / 4 - 50, i, 100, CLICKABLEWIDGETHEIGHT, Component.nullToEmpty(ChatLogger.getServerNameShortener(server.getServerDetails().getServerNames())),
                    button -> {
                            Minecraft.getInstance().setScreen(new Serverscreen(Minecraft.getInstance().screen, server));
            });


            Button delete = new Button(this.width / 2 + this.width / 4 - serverbutton.getWidth() / 2, i, serverbutton.getWidth(), CLICKABLEWIDGETHEIGHT, Component.nullToEmpty("Löschen"),
                    button -> {
                            Minecraft.getInstance().setScreen(new Verification(Minecraft.getInstance().screen, defaultConfig, server));
                });

            if (!serverConfigList.contains(dummy)) {
            }
        }
        serverConfigList.remove(dummy);

        Button addServer = new Button(this.width / 2 - this.width / 4 - 50, 30, 100, CLICKABLEWIDGETHEIGHT, Component.nullToEmpty("Server Hinzufügen"),
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


        Button done = new Button(this.width / 2 + this.width / 4 - addServer.getWidth() / 2, 30, addServer.getWidth(), CLICKABLEWIDGETHEIGHT, Component.nullToEmpty("Done"),
                button -> {
                        onClose();
                        defaultConfig.loadConfig();
        });

        Button defaultconfigbutton = new Button(this.width / 2 + - 30 , 30, 60, CLICKABLEWIDGETHEIGHT, Component.nullToEmpty("Defaults"),
                button ->{
                ServerConfig defaults = new ServerConfig("Defaults",List.of("Defaults"),Forgerplog.CONFIG.getKeywords());
                Minecraft.getInstance().setScreen(new Serverscreen(Minecraft.getInstance().screen, defaults));
            });
    }

    public void render(int mouseX, int mouseY, float partialTicks) {
        String serverlist = "Konfigurierbare Server";
        String deleteServer = "Server löschen";
        this.renderBackground(null);
        drawCenteredString(null,this.font, this.title.toString(), this.width / 2, 5, 0xffffff);
        drawCenteredString(null, this.font, serverlist, this.width / 2 - this.width / 4, 60, 0xffffff);
        drawCenteredString(null, this.font, deleteServer, this.width / 2 + this.width / 4, 60, 0xffffff);
        super.render(mouseX,mouseY,partialTicks));
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
            ClickableWidget delete = new ClickableWidget(this.width / 2 - this.width / 4 - 50, this.height / 2, 100, CLICKABLEWIDGETHEIGHT, Text.of("Ja")) {
                @Override
                public void appendNarrations(NarrationMessageBuilder builder) {
                    return;
                }

                @Override
                public void onClick(double mouseX, double mouseY) {
                    defaultConfig.removeServerFromList(serverConfig);
                    MinecraftClient.getInstance().setScreenAndRender(new Optionsscreen(previous));
                }
            };


            ClickableWidget abort = new ClickableWidget(this.width / 2 + this.width / 4 - 50, this.height / 2,100, CLICKABLEWIDGETHEIGHT, Text.of("Nein")) {
                @Override
                public void appendNarrations(NarrationMessageBuilder builder) {
                    return;
                }

                @Override
                public void onClick(double mouseX, double mouseY) {
                    onClose();
                }
            };

            this.addDrawableChild(delete);
            this.addDrawableChild(abort);

        }

        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);
            drawCenteredText(matrices, this.textRenderer, Text.of("Bist du sicher, dass du den Server löschen willst?"), this.width / 2, this.height / 2 - this.height / 4, 0xffffff);
            super.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public void onClose(){
            this.client.setScreen(previous);
        }

    }
}
