package net.hynse.scaleshifter;

import net.hynse.scaleshifter.Scaleshifter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class DataManagers {
    private Connection connection;

    public void setupDatabase() {
        try {
            // Ensure that the directory for the data file exists
            File dataFolder = Scaleshifter.instance.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            // Connect to the SQLite database (if it doesn't exist, it will be created)
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder.getAbsolutePath() + "/data.db");
            // Create a table to store player interactions
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_interactions (uuid VARCHAR(36) PRIMARY KEY, interacted BOOLEAN)");
            statement.close();
            Bukkit.getLogger().info("SQLite database connected successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveInteractions() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO player_interactions (uuid, interacted) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET interacted = ?");
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setBoolean(2, Scaleshifter.instance.playerInteractions.getOrDefault(player.getUniqueId(), false));
                preparedStatement.setBoolean(3, Scaleshifter.instance.playerInteractions.getOrDefault(player.getUniqueId(), false));
                preparedStatement.executeUpdate();
            }
            preparedStatement.close();
            Bukkit.getLogger().info("Player interactions saved to SQLite database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadInteractions() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM player_interactions");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                boolean interacted = resultSet.getBoolean("interacted");
                Scaleshifter.instance.playerInteractions.put(UUID.fromString(uuid), interacted);
            }
            resultSet.close();
            preparedStatement.close();
            Bukkit.getLogger().info("Player interactions loaded from SQLite database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveDefaultInteractions() {
        saveInteractions();
    }
}
