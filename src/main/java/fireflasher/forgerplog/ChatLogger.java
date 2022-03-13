package fireflasher.forgerplog;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ChatLogger {

    private static final Logger LOGGER = Forgerplog.LOGGER;
    private static File log;
    public static final DateTimeFormatter DATE  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME  = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static List<String> Channellist = Forgerplog.CONFIG.getList();
    private static String timedmessage = "";

    @SubscribeEvent
    public void ChatEvent(ClientChatReceivedEvent event){
        String chat =  event.getMessage().getString();

        for(String Channel:Channellist){
            if(chat.contains(Channel)){
                addMessage(chat);
            }
        }

    }

    protected void setup() {

        LocalDateTime today = LocalDateTime.now();
        String date = today.format(DATE);
        String Path = Forgerplog.getFolder() + "/RPLogs";
        String Filename = date + ".txt";
        log = new File(Path, Filename);
        if(!log.exists()){
            try{
                File path = new File(Path);
                path.mkdir();
                log.createNewFile();
            } catch (IOException e) {
                LOGGER.warn("RPLOG Datei " + log.toString() + " konnte nicht erstellt werden");
            }
            File[] files = new File(Path).listFiles();
            if(files == null){}
            else {
                for (File textfile : files) {
                    if (textfile.toString().endsWith(".txt") && textfile.compareTo(log) != 0 ) {
                        try {
                            String filename  = textfile.toString().replaceFirst(".txt", ".zip");

                            FileOutputStream fos = new FileOutputStream(filename);
                            ZipOutputStream zipOut = new ZipOutputStream(fos);

                            File fileToZip = new File(textfile.toString());
                            FileInputStream fis = new FileInputStream(fileToZip);

                            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                            zipOut.putNextEntry(zipEntry);

                            byte[] bytes = new byte[1024];
                            int length;
                            while ((length = fis.read(bytes)) >= 0) {
                                zipOut.write(bytes, 0, length);
                            }
                            zipOut.close();
                            fis.close();
                            fos.close();

                            if(new File(filename).exists()) fileToZip.delete();
                        }
                        catch (IOException e){
                            LOGGER.warn("RPLOG Datei konnte nicht verpackt werden");
                        }
                    }
                }
            }
        }
    }

    private void addMessage(String chat){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(log, true));
            BufferedReader br = new BufferedReader(new FileReader(log));
            LocalDateTime date = LocalDateTime.now();
            if ( !log.toString().contains(date.format(DATE))){setup();}

            String time = "[" + date.format(TIME) + "] ";
            String message = time + chat;

            br.read();
            if(br.lines().toList().isEmpty()) bw.append(message);
            else {
                if (timedmessage.equalsIgnoreCase(chat)) ;
                else bw.append("\n" + message);
            }
            bw.close();

            timedmessage = chat;

        } catch (IOException e) {
            LOGGER.warn("RPLog konnte nicht in " + log.toString() + " schreiben");
        }
    }

    public static String getServerNameShortener(List<String> namelist){
        int[] lenght = new int[2];
        lenght[0] = namelist.get(0).length();
        if(namelist.size() != 1){
            for(String name:namelist){
                if(lenght[0] > name.length()){
                    lenght[0] = name.length();
                    lenght[1] = namelist.indexOf(name);
                }
            }
        }
        String name = namelist.get(lenght[1]);
        Pattern pattern = Pattern.compile("\\.");
        Matcher match = pattern.matcher(name);
        int count = 0;
        while( match.find()){
            count++;
        }
        if(count > 1) name = name.split("\\.",2)[1];
        name = name.split("\\.")[0];
        return name;
    }
}
