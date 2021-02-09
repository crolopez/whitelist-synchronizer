package crolopez.WhitelistSynchronizer.config;

import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigMain extends Config {
    private static final String ConfigFile = "config.yml";
    private static ConfigMain _instance = new ConfigMain();
    private static int syncPeriod;
    private static String serverAddress;
    private static boolean broadcastAddedEntries;
    private static boolean broadcastRemovedEntries;

    @Override
    public void load(boolean firstCreate) {
        syncPeriod = getConfig().getInt("sync-period", 5);
        serverAddress = getConfig().getString("server-address", "http://localhost");
        broadcastAddedEntries = getConfig().getBoolean("broadcast-added-entries", false);
        broadcastRemovedEntries = getConfig().getBoolean("broadcast-added-entries", false);
    }

    @Override
    public void update() {
    }

    public static FileConfiguration get() {
        return instance().config;
    }

    public static int getSyncPeriod() {
        return syncPeriod;
    }

    public static String getServerAddress() {
        return serverAddress;
    }

    public static boolean getBroadcastAddedEntries() {
        return broadcastAddedEntries;
    }

    public static boolean getBroadcastRemovedEntries() {
        return broadcastRemovedEntries;
    }

    public static ConfigMain instance()
    {
        return _instance;
    }

    private ConfigMain() {
        super(instance().ConfigFile);
    }
}
