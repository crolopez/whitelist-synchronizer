package crolopez.WhitelistSynchronizer.sync;

import com.google.gson.JsonSyntaxException;
import crolopez.WhitelistSynchronizer.config.ConfigMain;
import crolopez.WhitelistSynchronizer.helper.LogHandler;
import crolopez.WhitelistSynchronizer.helper.WhitelistHandler;
import org.bukkit.scheduler.BukkitRunnable;

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
            String whitelist = requestWhitelist();
            if (whitelist == null) {
                return;
            }

            WhitelistHandler.setWhitelist(whitelist);
        } catch (IOException | JsonSyntaxException e) {
            LogHandler.warn("Could not synchronize the whitelist.");
            LogHandler.warn(e.getMessage());
        }
    }

    private String requestWhitelist() throws IOException {
        final String address = ConfigMain.getServerAddress();
        URL obj = new URL(address);

        LogHandler.info("Fetching the server whitelist from " + address + ".");

        HttpURLConnection httpConnection = (HttpURLConnection) obj.openConnection();
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = httpConnection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            LogHandler.warn("The server replied and invalid status code: " + responseCode);
            return null;
        }

        return getServerResponse(httpConnection);
    }

    private String getServerResponse(HttpURLConnection httpConnection) throws IOException {
        InputStreamReader inputReader = new InputStreamReader(httpConnection.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputReader);

        try {
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        } finally {
            bufferedReader.close();
        }
    }
}
