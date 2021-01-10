package crolopez.WhitelistSynchronizer.helper;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.Bukkit;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class WhitelistEntry {
    String name;
    String uuid;

    @Override
    public String toString(){
        return  "{\n" +
                "    \"uuid\": \"" + uuid + "\",\n" +
                "    \"name\": \"" + name + "\"\n" +
                "}";
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof WhitelistEntry){
            WhitelistEntry toCompare = (WhitelistEntry) o;
            return this.name.equals(toCompare.name) && this.uuid.equals(toCompare.uuid);
        }
        return false;
    }
}

public class WhitelistHandler {
    private static final String FILENAME = "whitelist.json";

    public static void setWhitelist(String whitelistAsString) throws IOException {
        List<WhitelistEntry> whitelist = whitelistEntriesToList(whitelistAsString);

        if (!isWhitelistFormat(whitelist)) {
            LogHandler.warn("The server sent a whitelist with an invalid format.");
            return;
        }

        List<WhitelistEntry> existingWhitelist = getExistingWhitelist();
        if (!isADifferentWhitelist(whitelist, existingWhitelist)) {
            LogHandler.info("The pushed whitelist is identical to the existing one.");
            return;
        }

        replaceWhitelist(whitelist);
    }

    private static boolean isWhitelistFormat(List<WhitelistEntry> whitelist) {
        for (WhitelistEntry entry: whitelist) {
            if (entry.name == null || entry.uuid == null) {
                return false;
            }
        }
        
        return true;
    }

    private static List<WhitelistEntry> getExistingWhitelist() throws IOException {
        FileReader reader = new FileReader(FILENAME);
        return whitelistEntriesToList(reader);
    }

    private static boolean isADifferentWhitelist(List<WhitelistEntry> newWhitelist, List<WhitelistEntry> existingWhitelist) {
        List<WhitelistEntry> removedEntries = existingWhitelist.stream()
                .filter(entry -> !newWhitelist.contains(entry))
                .collect(Collectors.toList());
        List<WhitelistEntry> addedEntries = newWhitelist.stream()
                .filter(entry -> !existingWhitelist.contains(entry))
                .collect(Collectors.toList());

        if (removedEntries.size() == 0 && addedEntries.size() == 0) {
            return false;
        }

        if (removedEntries.size() > 0) {
            String removedMsg = "Removed users:";
            for (WhitelistEntry entry: removedEntries) {
                removedMsg += "\n - " + entry.name + " (" + entry.uuid + ")";
            }
            LogHandler.info(removedMsg);
        }

        if (addedEntries.size() > 0) {
            String addedMsg = "Added users:";
            for (WhitelistEntry entry: addedEntries) {
                addedMsg += "\n - " + entry.name + " (" + entry.uuid + ")";
            }
            LogHandler.info(addedMsg);
        }

        return true;
    }

    private static void replaceWhitelist(List<WhitelistEntry> whitelist) throws IOException {
        FileWriter writer = new FileWriter(FILENAME);
        try {
            writer.write(whitelist.toString());
        } finally {
            writer.close();
        }

        LogHandler.info("Reloading the whitelist.");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist reload");
    }

    private static List<WhitelistEntry> whitelistEntriesToList(String whitelistAsString) {
        Type whitelistType = new TypeToken<ArrayList<WhitelistEntry>>(){}.getType();
        Gson gson = new Gson();

        return gson.fromJson(whitelistAsString, whitelistType);
    }

    private static List<WhitelistEntry> whitelistEntriesToList(FileReader reader) {
        Type whitelistType = new TypeToken<ArrayList<WhitelistEntry>>(){}.getType();
        Gson gson = new Gson();

        return gson.fromJson(reader, whitelistType);
    }
}
