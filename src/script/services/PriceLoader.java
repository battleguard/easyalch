package script.services;

import org.osbot.Constants;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.script.MethodProvider;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PriceLoader extends Event {

    private final Consumer<Map<Integer, ItemPriceConfig>> _onPricesLoaded;
    private final MethodProvider _ctx;
    public Map<Integer, ItemPriceConfig> PriceConfigs = new HashMap<>();

    public class ItemPriceConfig {
        public ItemPriceConfig(int itemId, int itemPrice, int storePrice) {
            this.itemId = itemId;
            this.itemPrice = itemPrice;
            this.storePrice = storePrice;
            highAlchPrice = (int) (storePrice * 0.6);
            lowAlchPrice = (int) (storePrice * 0.6);
        }

        public final int itemId;
        public final int itemPrice;
        public final int storePrice;
        public final int highAlchPrice;
        public final int lowAlchPrice;
    }

    public PriceLoader(MethodProvider ctx, Consumer<Map<Integer, ItemPriceConfig>> onPricesLoaded) {
        _onPricesLoaded = onPricesLoaded;
        _ctx = ctx;
    }

    @Override
    public final int execute() {
        Map<Integer, Integer> gePrices = loadOsBuddyPricesSummary();
        Map<Integer, Integer> storePrices = loadStorePricesFromCache();

        if (!gePrices.isEmpty() && !storePrices.isEmpty()) {
            PriceConfigs = gePrices.entrySet().stream().map(s -> new ItemPriceConfig(s.getKey(), s.getValue(), storePrices.getOrDefault(s.getKey(), 0)))
                    .collect(Collectors.toMap(i -> i.itemId, i -> i));
            _onPricesLoaded.accept(PriceConfigs);
            setFinished();
        } else {
            setFailed();
        }
        return 0;
    }

    private Map<Integer, Integer> loadOsBuddyPricesSummary() {
        Map<Integer, Integer> prices = new HashMap<>();
        log("Loading all prices from osbuddy");
        try {
            String json = downloadFile("https://rsbuddy.com/exchange/summary.json").get(0);
            String[] rows = json.split("},");

            final String idStart = "\"id\": ";
            final String priceStart = "\"overall_average\": ";
            for (int i = 1; i < rows.length; i++) {
                String row = rows[i];
                char[] data = row.toCharArray();
                int startId = row.indexOf(idStart) + idStart.length();
                int startPrice = row.indexOf(priceStart) + priceStart.length();

                int endId = startId;
                while (endId != data.length && Character.isDigit(data[endId]))
                    endId++;

                int endPrice = startPrice;
                while (endPrice != data.length && Character.isDigit(data[endPrice]))
                    endPrice++;

                String id = row.substring(startId, endId);
                String price = row.substring(startPrice, endPrice);
                prices.put(Integer.parseInt(id), Integer.parseInt(price));
            }
            log("Finished loading osbuddy prices found:" + prices.size() + " items.");
        } catch (Exception e) {
            log("Failed to download prices from osbuddy");
            e.printStackTrace();
        }
        return prices;
    }

    private Map<Integer, Integer> loadStorePricesFromCache() {
        Map<Integer, Integer> storePrices = new HashMap<>();

        try {
            File file = new File(Constants.DATA_DIR + "\\EasyAlch\\StorePrices.csv");
            log(file.getAbsolutePath());
            if (!file.exists()) {
                log("Downloading Store prices from web cache");
                List<String> lines = downloadFile("https://gist.githubusercontent.com/battleguard/b3f0a547eef33c880cf75f4cd181ebfb/raw/4285b8953b89030ed29f2efd1aac21cb63b840ef/StorePrices.csv");
                log("Writing lines: " + lines.size());
                if (file.getParentFile().mkdirs() && file.createNewFile()) {
                    log("Saving store cache to local file (" + file.getAbsolutePath() + ") (Store Prices loaded: " + lines.size() + ")");
                    try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
                        for (String line : lines) {
                            br.write(line + System.lineSeparator());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                log("Finished writing store prices to local file: " + file.getAbsolutePath());
            }
            log("loading store prices from local cache file (" + file.getAbsolutePath() + ")");
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                String line = "";
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    storePrices.put(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
                }
            }
            log("Finished reading store prices from local cache loaded up " + storePrices.size() + " store prices.");
        } catch (Exception e) {
            log("Failed to load Store Prices");
            e.printStackTrace();
        }
        return storePrices;
    }

    private List<String> downloadFile(String link) {
        List<String> lines = new ArrayList<>();
        try {
            URL url = new URL(link);
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
            con.setUseCaches(true);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null)
                    lines.add(line);
            }
        } catch (Exception ignored) {
        }
        return lines;
    }
}
