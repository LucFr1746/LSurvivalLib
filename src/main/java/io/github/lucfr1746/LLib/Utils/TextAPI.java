package io.github.lucfr1746.LLib.Utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TextAPI {

    private String text;
    private Player player;
    private OfflinePlayer offlinePlayer;

    public TextAPI(String text) {
        this.text = text;
    }

    public TextAPI(String text, Player player) {
        this.text = text;
        this.player = player;
    }

    public TextAPI(String text, OfflinePlayer offlinePlayer) {
        this.text = text;
        this.offlinePlayer = offlinePlayer;
    }

    public String build() {
        return this.text;
    }

    public TextAPI setColor(ChatColor color) {
        this.text = color + this.text;
        return this;
    }

    public TextAPI setBold() {
        this.text = ChatColor.BOLD + this.text;
        return this;
    }

    public TextAPI setItalic() {
        this.text = ChatColor.ITALIC + this.text;
        return this;
    }

    public TextAPI setUnderlined() {
        this.text = ChatColor.UNDERLINE + this.text;
        return this;
    }

    public TextAPI setStrikethrough() {
        this.text = ChatColor.STRIKETHROUGH + this.text;
        return this;
    }

    public TextAPI altColorRecognise() {
        this.text = ChatColor.translateAlternateColorCodes('&', text);
        return this;
    }

    public TextAPI stripColor() {
        this.text = ChatColor.stripColor(this.text);
        return this;
    }

    public TextAPI setHexColor(String hex) {
        this.text = ChatColor.of(hex) + this.text;
        return this;
    }

    public TextAPI convert() {
        if (this.player != null) setPlaceholder(this.player);
        else if (this.offlinePlayer != null) setPlaceholder(this.offlinePlayer);
        colorRecognise();
        return this;
    }

    public TextAPI colorRecognise() {
        altColorRecognise();
        gradientRecognise();
        hexRecognise();
        return this;
    }

    public TextAPI gradientRecognise() {
        String[] path1 = this.text.split("<gradient>");
        List<String> path2 = new ArrayList<>();

        // Collecting paths between gradients
        for (int i = 0; i < path1.length; i++) {
            if (i % 2 != 0)
                path2.add(path1[i]);
        }

        List<String> converted = new ArrayList<>();

        // Process each gradient
        for (String path : path2) {
            StringBuilder result = new StringBuilder();
            String[] path3 = path.split("-");

            // Check if the input is valid
            if (path3.length < 3) {
                continue; // Skip invalid gradient specifications
            }

            double R1 = Color.decode(path3[0]).getRed();
            double G1 = Color.decode(path3[0]).getGreen();
            double B1 = Color.decode(path3[0]).getBlue();
            double R2 = Color.decode(path3[1]).getRed();
            double G2 = Color.decode(path3[1]).getGreen();
            double B2 = Color.decode(path3[1]).getBlue();

            String text = path3[2];
            int length = text.length() - 1;
            double R = (R2 - R1) / length;
            double G = (G2 - G1) / length;
            double B = (B2 - B1) / length;

            // Append the first character with the first color
            result.append(ChatColor.of(path3[0])).append(text.charAt(0));
            for (int i = 1; i < text.length() - 1; i++) {
                result.append(ChatColor.of(new Color((int) Math.ceil(R1 + R), (int) Math.ceil(G1 + G), (int) Math.ceil(B1 + B))))
                        .append(text.charAt(i));
                R1 = (int) Math.round(R1 + R);
                G1 = (int) Math.round(G1 + G);
                B1 = (int) Math.round(B1 + B);
            }
            result.append(ChatColor.of(path3[1])).append(text.charAt(text.length() - 1));
            converted.add(result.toString());
        }

        // Replace the original gradient string with the converted one
        for (int i = 0; i < path2.size(); i++) {
            String convert = "<gradient>" + path2.get(i) + "<gradient>";
            this.text = this.text.replace(convert, converted.get(i));
        }
        return this;
    }

    public TextAPI hexRecognise() {
        StringBuilder result = new StringBuilder();
        String[] string = this.text.split("#");

        if (string.length == 0) {
            return this;
        }

        for (String path : string) {
            if (!path.isEmpty() && path.charAt(0) != '&' && !path.equals(string[0])) {
                path = "#" + path;
                String hexColor = path.substring(0, 7);
                path = path.replace(path.substring(0, 7), "");
                path = ChatColor.of(hexColor) + path;
            }
            result.append(path);
        }
        this.text = result.toString();
        return this;
    }

    private TextAPI setPlaceholder(Player p) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            this.text = PlaceholderAPI.setPlaceholders(p, this.text);
        return this;
    }

    private TextAPI setPlaceholder(OfflinePlayer p) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            this.text = PlaceholderAPI.setPlaceholders(p, this.text);
        return this;
    }

    public boolean isNumeric() {
        if (this.text == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(this.text);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public boolean isInteger() {
        if (this.text == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(this.text);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
