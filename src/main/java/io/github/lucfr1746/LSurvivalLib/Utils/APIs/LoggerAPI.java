package io.github.lucfr1746.LSurvivalLib.Utils.APIs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class LoggerAPI {

    private JavaPlugin plugin;

    public LoggerAPI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void info(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + this.plugin.getName() + "] " + message);
    }

    public void warning(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[" + this.plugin.getName() + "] " + message);
    }

    public void warning(String message, Exception exception) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[" + this.plugin.getName() + "] " + message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[" + this.plugin.getName() + "] " + exception.getMessage());
    }

    public void error(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + this.plugin.getName() + "] " + message);
    }

    public void success(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[" + this.plugin.getName() + "] " + message);
    }

    public void debug(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[" + this.plugin.getName() + "] " + message);
    }
}
