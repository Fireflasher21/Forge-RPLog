package fireflasher.forgerplog.config.json;


import java.util.ArrayList;
import java.util.List;

public class JsonConfig {

    private List<String> defaultKeywords = new ArrayList<>();
    private List<ServerConfig> serverList = new ArrayList<>();

    public JsonConfig(){}

    public JsonConfig(List<String> defaultKeywords) {
        this.defaultKeywords = defaultKeywords;
    }

    public JsonConfig(List<String> defaultKeywords, List<ServerConfig> serverList){
        this.defaultKeywords = defaultKeywords;
        this.serverList = serverList;
    }

    public List<String> getDefaultKeywords() {
        return defaultKeywords;
    }

    public List<ServerConfig> getServerList() {
        return serverList;
    }
}

