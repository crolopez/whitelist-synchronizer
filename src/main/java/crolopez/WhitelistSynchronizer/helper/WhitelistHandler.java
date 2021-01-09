package crolopez.WhitelistSynchronizer.helper;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class WhitelistEntry {
    String name;
    String uuid;

    @Override
    public String toString(){
        return  "\n" +
                "{\n" +
                "    \"uuid\": \"" + uuid + "\",\n" +
                "    \"name\": \"" + name + "\"\n" +
                "}";
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
                removedMsg += "\n - " + entry.name;
            }
            LogHandler.info(removedMsg);
        }

        if (addedEntries.size() > 0) {
            String addedMsg = "Added users:";
            for (WhitelistEntry entry: addedEntries) {
                addedMsg += "\n - " + entry.name;
            }
            LogHandler.info(addedMsg);
        }

        return true;
    }

    // TODO
    private static void replaceWhitelist(List<WhitelistEntry> whitelist) {
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
