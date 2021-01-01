package crolopez.WhitelistSynchronizer.config;

import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;

public abstract class Config {
    protected String fileName;
    protected FileConfiguration config;
    protected File pdfile;

    protected Config(String fileName) {
        this.fileName = fileName;
    }

    public void setup(Plugin plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        pdfile = new File(plugin.getDataFolder(), fileName);
        boolean firstCreate = false;

        if (!pdfile.exists()) {
            firstCreate = true;
            try {
                pdfile.createNewFile();
                try (InputStream is = plugin.getResource(fileName);
                     OutputStream os = new FileOutputStream(pdfile)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create the file: " + fileName, e);
            }
        }

        config = YamlConfiguration.loadConfiguration(pdfile);
        load(firstCreate);
        update();
    }

    public abstract void load(boolean firstCreate);

    public abstract void update();

    public void save() {
        try {
            config.save(pdfile);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe("Could not save " + fileName + "!");
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(pdfile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getFileName() {
        return fileName;
    }
}
