package net.hynse.scaleshifter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;

public class DataManagers {
    public File dataFile;

    public void datasetup() {
        dataFile = new File(Scaleshifter.instance.getDataFolder(), "player_interactions.json");
        if (!dataFile.exists()) {
            saveInteractions();
        } else {
            loadInteractions();
        }

    }
    public void saveInteractions() {
        JSONObject jsonObject = new JSONObject();
        for (UUID uuid : Scaleshifter.instance.playerInteractions.keySet()) {
            jsonObject.put(uuid.toString(), Scaleshifter.instance.playerInteractions.get(uuid));
        }
        try (FileWriter fileWriter = new FileWriter(dataFile)) {
            fileWriter.write(jsonObject.toJSONString());
            getLogger().info("Player interactions saved to file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadInteractions() {
        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(dataFile)) {
            Object obj = jsonParser.parse(fileReader);
            JSONObject jsonObject = (JSONObject) obj;
            for (Object key : jsonObject.keySet()) {
                UUID uuid = UUID.fromString((String) key);
                boolean interacted = (boolean) jsonObject.get(key);
                Scaleshifter.instance.playerInteractions.put(uuid, interacted);
            }
            getLogger().info("Player interactions loaded from file.");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}