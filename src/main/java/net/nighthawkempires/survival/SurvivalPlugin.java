package net.nighthawkempires.survival;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.server.ServerType;
import net.nighthawkempires.survival.listener.PlayerListener;
import net.nighthawkempires.survival.scoreboard.SurvivalScoreboard;
import net.nighthawkempires.survival.user.registry.MUserRegistry;
import net.nighthawkempires.survival.user.registry.UserRegistry;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static net.nighthawkempires.core.CorePlugin.getConfigg;

public class SurvivalPlugin extends JavaPlugin {

    private static UserRegistry userRegistry;

    private static Plugin plugin;

    private static MongoDatabase mongoDatabase;

    public static NamespacedKey CREATURE_KEY;

    public void onEnable() {
        plugin = this;
        if (getConfigg().getServerType() != ServerType.SETUP) {
            String pluginName = getPlugin().getName();
            try {
                String hostname = getConfigg().getMongoHostname();
                String database = getConfigg().getMongoDatabase().replaceAll("%PLUGIN%", pluginName);
                String username = getConfigg().getMongoUsername().replaceAll("%PLUGIN%", pluginName);
                String password = getConfigg().getMongoPassword();

                ServerAddress serverAddress = new ServerAddress(hostname);
                MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
                mongoDatabase = new MongoClient(serverAddress, mongoCredential, new MongoClientOptions.Builder().build()).getDatabase(database);

                userRegistry = new MUserRegistry(mongoDatabase);


                getLogger().info("Successfully connected to MongoDB.");

                registerCommands();
                registerKeys();
                registerListeners();

                CorePlugin.getScoreboardManager().addScoreboard(new SurvivalScoreboard());
            } catch (Exception exception) {
                exception.printStackTrace();
                getLogger().warning("Could not connect to MongoDB, shutting plugin down...");
                getServer().getPluginManager().disablePlugin(this);
            }
        }
    }

    private void registerCommands() {}

    private void registerKeys() {
        Plugin plugin;
        CREATURE_KEY = new NamespacedKey(this, "creature");
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
    }

    public static UserRegistry getUserRegistry() {
        return userRegistry;
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}