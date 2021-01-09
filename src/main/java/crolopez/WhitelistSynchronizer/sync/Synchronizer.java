package crolopez.WhitelistSynchronizer.sync;

import crolopez.WhitelistSynchronizer.config.ConfigMain;
import org.bukkit.scheduler.BukkitRunnable;
import static org.bukkit.Bukkit.getLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Synchronizer extends BukkitRunnable {
    private final String USER_AGENT = "WhitelistSynchronizer";

    @Override
    public void run() {
        try {
            requestWhitelist();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestWhitelist() throws IOException {
        final String address = ConfigMain.getServerAddress();

        getLogger().info("Connecting to " + address + ".");
        // URL obj = new URL("HTTP", address, port, "output");
        URL obj = new URL(address);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        getLogger().info("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            getLogger().info(response.toString());
        } else {
            getLogger().info("GET request not worked");
        }
    }
}
