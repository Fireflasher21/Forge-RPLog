package fireflasher.forgerplog.config.modmenu;

import com.mojang.blaze3d.matrix.MatrixStack;
import fireflasher.forgerplog.ChatLogger;
import fireflasher.forgerplog.Forgerplog;
import fireflasher.forgerplog.config.DefaultConfig;
import fireflasher.forgerplog.config.json.ServerConfig;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Optionsscreen extends Screen {

    private Screen previous;
    static final int BUTTON_HEIGHT = 20;
    private final ServerConfig dummy = new ServerConfig("dummy", List.of("dummy"), List.of("dummy"));



    public Optionsscreen() {
        super(new TranslationTextComponent( "rplog.config.optionscreen.title"));
    }

    public Optionsscreen(Screen previous) {
        super(new TranslationTextComponent( "rplog.config.optionscreen.title"));
        this.previous = previous;
    }

    public Optionsscreen(Screen previous, String title) { //For Serverscreen as super
        super(ITextComponent.nullToEmpty(title));
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
            Button serverbutton = new Button(this.width / 2 - this.width / 4 - 50, i, 100, BUTTON_HEIGHT, ITextComponent.nullToEmpty(ChatLogger.getServerNameShortener(server.getServerDetails().getServerNames())),
                    button -> {
                        Minecraft.getInstance().setScreen(new Serverscreen(this, server));
                    });


            Button delete = new Button(this.width / 2 + this.width / 4 - serverbutton.getWidth() / 2, i, serverbutton.getWidth(), BUTTON_HEIGHT, new TranslationTextComponent("rplog.config.screen.delete"),
                    button -> {
                        Minecraft.getInstance().setScreen(new Verification(Minecraft.getInstance().screen, defaultConfig, server));
                    });

            if (!serverConfigList.contains(dummy)) {
                this.addButton(serverbutton);
                this.addButton(delete);
            }
        }
        serverConfigList.remove(dummy);

        Button addServer = new Button(this.width / 2 - this.width / 4 - 50, 13, 100, BUTTON_HEIGHT, new TranslationTextComponent("rplog.config.optionscreen.add_Server"),
                button ->{
                    if (Minecraft.getInstance().getCurrentServer() != null && !Minecraft.getInstance().getCurrentServer().isLan()) {
                        String[] ip = new String[2];
                        ip[0] = Minecraft.getInstance().getCurrentServer().ip;
                        ip[1] = Minecraft.getInstance().getCurrentServer().name;

                        ip = ChatLogger.getIP(ip[0], ip[1]);

                        defaultConfig.addServerToList(ip[0], ip[1]);
                        defaultConfig.loadConfig();
                        Minecraft.getInstance().setScreen(new Optionsscreen(previous));
                }});

        Button defaultconfigbutton = new Button(this.width / 2 + this.width / 4 - 50, 13, 100, BUTTON_HEIGHT,  new TranslationTextComponent("rplog.config.screen.defaults"),
                button ->{
                    ServerConfig defaults = new ServerConfig("Defaults",List.of("Defaults"),Forgerplog.CONFIG.getKeywords());
                    Minecraft.getInstance().setScreen(new Serverscreen(Minecraft.getInstance().screen, defaults));
                });

        Button done = new Button(this.width / 2 + this.width / 4 - 50, this.height - 30, 100, BUTTON_HEIGHT, new TranslationTextComponent("rplog.config.screen.done"),
                button -> {
                    onClose();
                    defaultConfig.loadConfig();
                });

        Button openFolder = new Button(this.width / 2 - this.width / 4 - 50, this.height - 30, 100, BUTTON_HEIGHT, new TranslationTextComponent("rplog.config.optionscreen.open_LogFolder"),
                button -> {
                        Util.getPlatform().openFile(new File(Forgerplog.getFolder()));
                });

        this.addButton(addServer);
        this.addButton(done);
        this.addButton(defaultconfigbutton);
        this.addButton(openFolder);
    }


    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
        TranslationTextComponent serverlist = new TranslationTextComponent("rplog.config.optionscreen.configuration_Servers");
        TranslationTextComponent deleteServer = new TranslationTextComponent("rplog.config.optionscreen.delete_Servers");
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
            super(ITextComponent.nullToEmpty(""));
            this.previous = previous;
            this.defaultConfig = defaultConfig;
            this.serverConfig = serverConfig;
        }

        public void init(){
            Button delete = new Button(this.width / 2 - this.width / 4 - 50, this.height / 2, 100, BUTTON_HEIGHT, new TranslationTextComponent("rplog.config.optionscreen.verification.delete"),
                    button -> {
                        defaultConfig.removeServerFromList(serverConfig);
                        Minecraft.getInstance().setScreen(new Optionsscreen(previous));
                    });


            Button abort = new Button(this.width / 2 + this.width / 4 - 50, this.height / 2,100, BUTTON_HEIGHT, new TranslationTextComponent("rplog.config.optionscreen.verification.cancel"),
                    button -> {
                        onClose();

                    });

            this.addButton(delete);
            this.addButton(abort);

        }

        public void render(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
            this.renderBackground(poseStack);
            TranslationTextComponent server_delete_message = new TranslationTextComponent("rplog.config.optionscreen.verification.message");
            drawCenteredString(poseStack, this.font, server_delete_message, this.width / 2, this.height / 2 - this.height / 4, 0xffffff);
            super.render(poseStack, mouseX, mouseY, delta);
        }

        @Override
        public void onClose(){
            this.minecraft.setScreen(previous);
        }

    }
}
