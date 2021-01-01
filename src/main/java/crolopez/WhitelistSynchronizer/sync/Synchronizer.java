package crolopez.WhitelistSynchronizer.sync;

import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Bukkit.getLogger;

public class Synchronizer extends BukkitRunnable {

    @Override
    public void run() {
        getLogger().info("On Synchronizer run!");
    }
}
