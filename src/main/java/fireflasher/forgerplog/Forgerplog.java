package fireflasher.forgerplog;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



@Mod("rplog")
public class Forgerplog {


    private static final Logger LOGGER = LogManager.getLogger();
    public static DefaultConfig CONFIG = new DefaultConfig();
    public static ChatLogger CHATLOGGER;

    public Forgerplog() {
        CONFIG.setup();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        CHATLOGGER = new ChatLogger();
        CHATLOGGER.setup();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(CHATLOGGER);

    }

    private void setup(final FMLCommonSetupEvent event) {}

    private void doClientStuff(final FMLClientSetupEvent event){}

    protected static String getFolder(){ return FMLPaths.GAMEDIR.get().toString(); }
    protected static String getConfigFolder(){ return FMLPaths.CONFIGDIR.get().toString() + "/RPLog/"; }
}