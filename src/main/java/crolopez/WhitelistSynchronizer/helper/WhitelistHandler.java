package crolopez.WhitelistSynchronizer.helper;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import crolopez.WhitelistSynchronizer.config.ConfigMain;
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
            LogHandler.info("The fetched whitelist is identical to the existing one.");
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

        sendConsoleMessage(removedEntries, addedEntries);

        if ((removedEntries.size() > 0 && ConfigMain.getBroadcastRemovedEntries()) ||
            (addedEntries.size() > 0 && ConfigMain.getBroadcastAddedEntries())) {
            sendWorldMessage(removedEntries, addedEntries);
        }

        return true;
    }

    private static void sendWorldMessage(List<WhitelistEntry> removedEntries, List<WhitelistEntry> addedEntries) {
        //String worldMessageFormat = "/tellraw @a [\"\",{\"text\":\"The whitelist has been synchronized with the following changes:\",\"bold\":true,\"color\":\"yellow\"},{\"text\":\"\\n\"},{\"text\":\"+ Manuel\",\"color\":\"green\"},{\"text\":\"\\n\"},{\"text\":\"- Federico\",\"color\":\"red\"}]";
        String worldMessageHeader = "The whitelist has been synchronized with the following changes:";
        String worldMessageFormat = "tellraw @a [\"\"," +
                "{\"text\":\"%s\",\"bold\":true,\"color\":\"yellow\"}," +
                "{\"text\":\"%s\",\"color\":\"green\"}," +
                "{\"text\":\"%s\",\"color\":\"red\"}]";
        String removed = "";
        String added = "";

        if (removedEntries.size() > 0 && ConfigMain.getBroadcastRemovedEntries()) {
            removed += collectEntryChangesForWorldMessage(removedEntries, true).replaceAll("\n", "\\\\n");
        }

        if (addedEntries.size() > 0 && ConfigMain.getBroadcastAddedEntries()) {
            added += collectEntryChangesForWorldMessage(addedEntries, false).replaceAll("\n", "\\\\n");
        }

        String finalCommand = worldMessageFormat.formatted(worldMessageHeader, added, removed);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
    }

    private static void sendConsoleMessage(List<WhitelistEntry> removedEntries, List<WhitelistEntry> addedEntries) {
        String consoleMessage = "";

        if (removedEntries.size() > 0) {
            consoleMessage += collectEntryChangesForConsoleMessage(removedEntries, true);
        }

        if (addedEntries.size() > 0) {
            consoleMessage += collectEntryChangesForConsoleMessage(addedEntries, false);
        }

        LogHandler.info(consoleMessage);
    }

    private static String collectEntryChangesForConsoleMessage(List<WhitelistEntry> entries, boolean removedMessage) {
        String entryChanges = removedMessage ? "\nRemoved users:" : "\nAdded users:";
        String rowPrefix = removedMessage ? "-" : "+";

        for (WhitelistEntry entry: entries) {
            entryChanges += "\n%s %s (%s)".formatted(rowPrefix, entry.name, entry.uuid);
        }

        return entryChanges;
    }

    private static String collectEntryChangesForWorldMessage(List<WhitelistEntry> entries, boolean removedMessage) {
        String entryChanges = "";
        String rowPrefix = removedMessage ? "-" : "+";

        for (WhitelistEntry entry: entries) {
            entryChanges += "\n%s %s".formatted(rowPrefix, entry.name);
        }

        return entryChanges;
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
