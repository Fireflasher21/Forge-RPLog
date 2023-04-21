package fireflasher.forgerplog.config;

import com.google.gson.Gson;
import fireflasher.forgerplog.Forgerplog;
import fireflasher.forgerplog.config.json.JsonConfig;
import fireflasher.forgerplog.config.json.ServerConfig;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultConfig {

    private File ConfigFile = null;
    private static final Gson GSON = new Gson();
    private String ModsDir = Forgerplog.getConfigFolder();
    private List<String> Keywords = new ArrayList<>();
    public static final List<String> defaultKeywords = Arrays.asList("[Flüstern]","[Leise]","[Reden]","[Rufen]","[PRufen]","[Schreien]");

    public List<ServerConfig> serverList = new ArrayList<>();

    private static final Logger LOGGER = LogManager.getLogger("ForgeRPLog Config");

    public void setup() {
        this.ConfigFile = new File(ModsDir + "rplog.json");
        if (ConfigFile.exists()) {
            LOGGER.info("Config loaded");
            loadConfig();
        } else {
            setConfigFile();
            loadConfig();
        }
    }

    private void setConfigFile(){
        if (this.ConfigFile == null || !this.ConfigFile.exists()) {
            this.ConfigFile = new File(ModsDir, "rplog.json");
            try {
                ConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JsonConfig channellistconfig = new JsonConfig(defaultKeywords);
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(ConfigFile, false));
            String json = GSON.toJson(channellistconfig);
            pw.write(json);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfig(){
        if (this.ConfigFile == null || !this.ConfigFile.exists())setConfigFile();

        try {
            BufferedReader br = new BufferedReader(new FileReader(ConfigFile));
            String collect = br.lines().collect(Collectors.joining(""));
            if(collect.isEmpty()){
                setConfigFile();
                collect = br.lines().collect(Collectors.joining(""));
            }
            JsonConfig jsonConfig = GSON.fromJson(collect, JsonConfig.class);


            Keywords = jsonConfig.getDefaultKeywords();
            serverList = jsonConfig.getServerList();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        if (this.ConfigFile == null) {
            return;
        }
        JsonConfig jsonConfig = new JsonConfig(Keywords, serverList);
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(ConfigFile, false));
            String json = GSON.toJson(jsonConfig);
            pw.write(json);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ServerConfig> getList() {
        if (serverList.isEmpty()) {
            loadConfig();
        }
        return this.serverList;
    }

    public List<String> getKeywords() {
        return Keywords;
    }

    public void addServerToList(String serverIp, String serverName) {
        boolean save = false;
        int i = 0;

        for (ServerConfig serverListe : serverList) {
            if (!serverListe.getServerIp().equals(serverIp)) {
                i++;
                continue;
            }
            //Falls Server als Ip bereits in der Liste, überprüf die Liste der Servernamen
            ServerConfig.ServerDetails temp_serverdetails = serverListe.getServerDetails();
            if (!temp_serverdetails.getServerNames().contains(serverName)) {
                this.serverList.remove(serverListe);
                temp_serverdetails.getServerNames().add(serverName);
                serverListe.setServerDetails(temp_serverdetails);
                this.serverList.add(serverListe);
                save = true;
            }
        }
        if(i == serverList.size()) {
            ServerConfig server = new ServerConfig(serverIp, List.of(serverName), defaultKeywords);
            serverList.add(server);
            saveConfig();
            return;
        }
        if(save){
            saveConfig();
        }
    }

    public void removeServerFromList(ServerConfig serverConfig){
        getList().remove(serverConfig);
        saveConfig();
    }

    public ServerConfig getServerObject(String serverIp) {
        for (ServerConfig server : serverList) {
            if (!server.getServerIp().equals(serverIp)) {
                continue;
            }
            return server;
        }
        return null;
    }



}

