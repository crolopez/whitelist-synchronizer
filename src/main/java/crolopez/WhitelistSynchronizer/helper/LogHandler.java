package crolopez.WhitelistSynchronizer.helper;

import org.bukkit.Bukkit;

import java.util.logging.Logger;

public class LogHandler {
    private static Logger log = Bukkit.getServer().getLogger();
    private static final String PluginTag = "[WhitelistSynchronizer] ";

    public static void info(String msg) {
        log.info(PluginTag + msg);
    }

    public static void warn(String msg) {
        log.warning(PluginTag + msg);
    }
}
