package io.github.lucfr1746.LLib.Utils.Metrics;

import io.github.lucfr1746.LLib.LLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private final LLib plugin;

    public UpdateChecker(LLib plugin) {
        this.plugin = plugin;
        check(-1);
    }

    public void getLatestVersion(Consumer<String> consumer, int resourceId) {
        try (InputStream inputStream = new URI("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).toURL().openStream();
             Scanner scanner = new Scanner(inputStream)) {

            if (scanner.hasNext()) {
                consumer.accept(scanner.next());
            }
        } catch (IOException | URISyntaxException exception) {
            plugin.getLogger().info("Update checker is broken, can't find an update! " + exception.getMessage());
        }
    }

    public void check(int resourceId) {
//        getLatestVersion(version -> {
//            PluginDescriptionFile pdf = plugin.getDescription();
//            // Check if the current version matches the latest version.
//            if (pdf.getVersion().equalsIgnoreCase(version)) {
//                Bukkit.getConsoleSender().sendMessage("[LLib] Version: " + ChatColor.GREEN + pdf.getVersion());
//                Bukkit.getConsoleSender().sendMessage("[LLib] Plugin is up to date!");
//            } else {
//                Bukkit.getConsoleSender().sendMessage("[LLib] Version: " + ChatColor.RED + pdf.getVersion());
//                Bukkit.getConsoleSender().sendMessage("[LLib] Plugin has an update! Download at: https://www.spigotmc.org/resources/" + resourceId);
//            }
//        }, resourceId);
    }
}
