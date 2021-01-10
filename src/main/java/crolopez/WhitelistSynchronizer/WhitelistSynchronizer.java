package crolopez.WhitelistSynchronizer;

import crolopez.WhitelistSynchronizer.config.ConfigMain;
import crolopez.WhitelistSynchronizer.helper.LogHandler;
import crolopez.WhitelistSynchronizer.sync.Synchronizer;
import org.bukkit.plugin.java.JavaPlugin;

public class WhitelistSynchronizer extends JavaPlugin {
    @Override
    public void onEnable() {
        ConfigMain.instance().setup(this);
        runSynchronizer();
    }

    private void runSynchronizer() {
        new Synchronizer().runTaskTimer(this, 1, ConfigMain.getSyncPeriod() * 20);
    }
}
