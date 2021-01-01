package crolopez.WhitelistSynchronizer.config;

import org.bukkit.configuration.file.FileConfiguration;

import static org.bukkit.Bukkit.getLogger;

public final class ConfigMain extends Config {
    private static final String ConfigFile = "config.yml";
    private static ConfigMain instance_ = new ConfigMain();
    private static int syncPeriod;
    private static String serverAddress;
    private static int serverPort;

    @Override
    public void load(boolean firstCreate) {
        getLogger().info("load is called!");
        syncPeriod = getConfig().getInt("sync-period", 5);
        serverAddress = getConfig().getString("server-address", "http://localhost");
        serverPort = getConfig().getInt("server-port", 8080);
        getLogger().info("load is called! 2: " + syncPeriod + serverAddress + serverPort);
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

    public static ConfigMain instance()
    {
        return instance_;
    }

    private ConfigMain() {
        super(instance().ConfigFile);
    }
}
