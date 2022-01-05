package fireflasher.forgerplog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DefaultConfig {

    String ModsDir = Forgerplog.getConfigFolder();
    private File ConfigFile = null;
    private List<String> Keywords = new ArrayList<>();

    private static final Logger LOGGER = LogManager.getLogger();

    public void DefaultConfig(){
        this.ConfigFile = new File(ModsDir, "config.yml");
        if(!Keywords.isEmpty()){
            this.Keywords = Keywords;
        }
    }

    public void reloadConfig() {
        if (this.ConfigFile == null || !this.ConfigFile.exists()) {
            this.ConfigFile = new File( ModsDir, "config.yml");
            try {
                ConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Scanner sc = new Scanner(ConfigFile);

            while(sc.hasNextLine()){
                String line = sc.nextLine();
                this.Keywords.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(Keywords.isEmpty()){
            Keywords.add("[Flüstern]");
            Keywords.add("[Leise]");
            Keywords.add("[Reden]");
            Keywords.add("[Rufen]");
            Keywords.add("[PRufen]");
            Keywords.add("[Schreien]");
        }
    }

    public List<String> getList() {
        if (Keywords.isEmpty()) {
            reloadConfig();
        }
        return Keywords;
    }

    public void saveConfig() {
        if (this.ConfigFile == null) {
            return;
        }
        try{
            for(String write: Keywords){
                BufferedWriter bw = new BufferedWriter(new FileWriter(ConfigFile, true));
                BufferedReader br = new BufferedReader(new FileReader(ConfigFile ));

                br.read();
                if(br.lines().toList().isEmpty()) bw.append(write);
                else bw.append("\n").append(write);
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void setup() {
        new File(ModsDir).mkdir();
        this.ConfigFile = new File(ModsDir + "config.yml");
        if (ConfigFile.exists()) {
            LOGGER.info("Config erfolgreich geladen");
            reloadConfig();
        } else {
            reloadConfig();
            saveConfig();
        }
    }


}

