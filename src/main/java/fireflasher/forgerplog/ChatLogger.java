package fireflasher.forgerplog;


import fireflasher.forgerplog.config.json.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static fireflasher.forgerplog.Forgerplog.CONFIG;

public class ChatLogger {

    public static Logger LOGGER = Forgerplog.LOGGER;
    private static String serverIP = "";
    private static String serverName = "Local";
    public static final DateTimeFormatter DATE  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME  = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static File log;
    private static List<String> channellist = new ArrayList<>();
    private static String timedmessage = "";
    private static boolean error;


    public static void chatFilter(String chat){
        /*
        @SubscribeEvent
        public static void ChatEvent(ChatEvent event)
        String chat =  event.getMessage();
        */
        LOGGER.warn("ChatEvent: " + chat);

        if( Minecraft.getInstance().getCurrentServer() != null && !Minecraft.getInstance().getCurrentServer().isLan()) servercheck();
        else{
            serverName = "Local";
            channellist = CONFIG.getKeywords();
        }
        for(String Channel:channellist){
            if(chat.contains(Channel)){
                addMessage(chat);
            }
        }

    }

    public static void servercheck(){
        String[] ipArray = new String[2];
        String ip = Minecraft.getInstance().getCurrentServer().ip;
        String serverNameTMP = Minecraft.getInstance().getCurrentServer().name;
        ipArray = getIP(ip ,serverNameTMP);

        ServerConfig serverConfig = CONFIG.getServerObject(ipArray[0]);

        if( serverConfig != null){
            channellist = serverConfig.getServerDetails().getServerKeywords();
            if(!ipArray[1].contains(serverName) || serverName.equals("")) {
                serverName = getServerNameShortener(serverConfig.getServerDetails().getServerNames());
            }
        }
        else channellist = CONFIG.getKeywords();
        serverIP = ipArray[0];
    }

    public void setup() {
        String path = Forgerplog.getFolder();
        if(!new File(path).exists())new File(path).mkdir();
        log = new File(path + serverName, LocalDateTime.now().format(DATE) + ".txt");
        for (ServerConfig serverList : CONFIG.getList()) {
            organizeFolders(serverList);

            String server_name = getServerNameShortener(serverList.getServerDetails().getServerNames());
            String Path = Forgerplog.getFolder() + server_name;
            log = new File(Path, LocalDateTime.now().format(DATE) + ".txt");
            File[] files = new File(Path).listFiles();
            if (files == null) {
            } else {
                for (File textfile : files) {
                    if (textfile.toString().endsWith(".txt") && textfile.compareTo(log) != 0) {
                        try {
                            String filename = textfile.toString().replaceFirst(".txt", ".zip");

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

                            if (new File(filename).exists()) fileToZip.delete();
                        } catch (IOException e) {
                            LOGGER.warn(Component.translatable("rplog.logger.chatlogger.zip_warning"));
                        }
                    }
                }
            }
        }
    }

    private static void addMessage(String chat){
        String Path = Forgerplog.getFolder() + serverName;
        if(!log.toString().contains(LocalDateTime.now().format(DATE)) || !log.getPath().equalsIgnoreCase(Path)) {
            LocalDateTime today = LocalDateTime.now();
            String date = today.format(DATE);
            String Filename = date + ".txt";
            log = new File(Path, Filename);
            if(error)log = new File(Forgerplog.getFolder(), date + "-error.txt");
            if (!log.exists()) {
                try {
                    File path = new File(Path);
                    path.mkdir();
                    log.createNewFile();
                } catch (IOException e) {
                    LOGGER.warn(Component.translatable("rplog.logger.chatlogger.creation_warning") + log.toString());
                    error = true;
                }
            }
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(log, true));
            BufferedReader br = new BufferedReader(new FileReader(log));
            LocalDateTime date = LocalDateTime.now();

            String time = "[" + date.format(TIME) + "] ";
            String message = time + chat;

            String collect = br.lines().collect(Collectors.joining(""));
            if(collect.isEmpty()) bw.append(message);
            else if (!timedmessage.equalsIgnoreCase(chat))bw.append("\n" + message);
            bw.close();

            timedmessage = chat;

        } catch (IOException e) {
            LOGGER.warn(Component.translatable("rplog.logger.chatlogger.write_warning") + log.toString());
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


    public static String[] getIP(String ip, String serverName){
        String ip1 = ip;
        int[] dotscount = new int[]{0,0};
        while(ip.length() > dotscount[0]){  //Count dots for IP Check
            if(ip.charAt(dotscount[0]) == '.') dotscount[1] ++;
            dotscount[0] ++;
        }
        if(dotscount[1] < 3){   //Check if IP is IP or Name per dots in String
            try {
                InetAddress adress = InetAddress.getByName(ip);
                ip = adress.getHostAddress();   //Get real ip per domain in String
            } catch (UnknownHostException e) { throw new RuntimeException(e);}

            if(dotscount[1] == 2) ip1 = ip1.split("\\.")[1];
            else ip1 = ip1.split("\\.")[0];
            serverName = ip1;   //Split Domain in Name
        }
        String[] serverIP = new String[]{ip, serverName};
        return serverIP;
    }

    private boolean organizeFolders(ServerConfig serverConfig){
        List<String> serverNameList = serverConfig.getServerDetails().getServerNames();
        Pattern serverAddress = Pattern.compile("[A-z]{1,}");
        for(String serverName: serverNameList){
            if(serverAddress.matcher(serverName).find()) continue;

            String path = Forgerplog.getFolder() + serverName;
            File ipFolder = new File(path);

            if(!ipFolder.exists())continue;

            File[] ipFolderFiles = ipFolder.listFiles();
            if(ipFolderFiles == null){
                ipFolder.delete();
                continue;
            }

            File newFolder = new File(Forgerplog.getFolder() + ChatLogger.getServerNameShortener(serverConfig.getServerDetails().getServerNames()));

            if(newFolder.exists()) {
                if(newFolder.listFiles().length == 0) {
                    newFolder.delete();
                    ipFolder.renameTo(newFolder);
                }else {
                    File[] newFolderFiles = newFolder.listFiles();
                    if (newFolderFiles.length < ipFolderFiles.length){
                        moveFiles(newFolder, ipFolder);
                        ipFolder.renameTo(newFolder);
                    }
                    else moveFiles(ipFolder, newFolder);
                }
            }
            else ipFolder.renameTo(newFolder);


        }
        return true;
    }

    private boolean moveFiles(File sourceFolder, File newFolder) {
        List<Path> folderstodelete = new ArrayList<>();
        try (Stream<Path> pathStream = Files.walk(sourceFolder.toPath())) {
            pathStream.forEach(path1 -> {
                String filename = path1.getFileName().toString();
                if(filename.equals(sourceFolder.getName()));
                else{
                    try {
                        if (Files.isRegularFile(path1))
                            Files.move(path1, Path.of(newFolder + path1.toString().replace(sourceFolder.toString(),"")));
                        if (Files.isDirectory(path1)) {
                            Files.createDirectory(Path.of(newFolder + path1.toString().replace(sourceFolder.toString(),"")));
                            folderstodelete.add(path1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = folderstodelete.size() - 1; i > -1; i--  ) {
            folderstodelete.get(i).toFile().delete();
        }
        sourceFolder.delete();
        return true;
    }
}
