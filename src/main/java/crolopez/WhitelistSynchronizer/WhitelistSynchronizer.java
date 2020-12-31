package crolopez.WhitelistSynchronizer;
import org.bukkit.plugin.java.JavaPlugin;

public class WhitelistSynchronizer extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("onEnable is called!");
    }
    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
    }
}
