package io.github.lucfr1746.LSurvivalLib.Utils.APIs;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextAPI {

    private String text;
    private Player player;
    private OfflinePlayer offlinePlayer;

    public TextAPI(String text) {
        this.text = text;
    }

    public TextAPI() {
        this.text = "";
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
        if (this.player != null) setPlaceholderOfOnlinePlayer();
        else if (this.offlinePlayer != null) setPlaceholderOfOfflinePlayer();
        colorRecognise();
        return this;
    }

    public TextAPI colorRecognise() {
        altColorRecognise();
        autoDetectARGBColor();
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
        String[] parts = this.text.split("<hex>");

        if (parts.length == 0) {
            return this;
        }

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            // Skip the first part if it's not wrapped in <hex> tags
            if (i == 0 || part.isEmpty()) {
                result.append(part);
                continue;
            }

            // Check if the part contains a valid hex code and ends with "</hex>"
            int endIndex = part.indexOf("</hex>");
            if (endIndex != -1) {
                String hexText = part.substring(0, endIndex); // Extract text inside the <hex>...<hex> tags
                if (hexText.startsWith("#") && hexText.length() > 7) {
                    String hexColor = hexText.substring(1, 7);    // Extract the hex color (e.g., #A06540)
                    String text = hexText.substring(8);          // Extract the text after the "-"
                    result.append(ChatColor.of("#" + hexColor)).append(text);
                } else {
                    // If the format is invalid, append the part as-is
                    result.append("<hex>").append(part);
                }
                // Append the rest of the string after </hex>
                result.append(part.substring(endIndex + 6));
            } else {
                // If no closing </hex> tag is found, append as-is
                result.append("<hex>").append(part);
            }
        }

        this.text = result.toString();
        return this;
    }

    public TextAPI autoDetectARGBColor() {
        if (this.text.contains("Color:[argb0x")) {
            Pattern pattern = Pattern.compile("Color:\\[argb0x([0-9a-fA-F]{8})\\](.*?)"); // Match ARGB and following text
            Matcher matcher = pattern.matcher(this.text);
            StringBuilder formattedText = new StringBuilder();

            while (matcher.find()) {
                String argb = matcher.group(1); // Extract ARGB code
                String followingText = matcher.group(2); // Extract text after the color code
                String hexColor = "#" + argb.substring(2); // Convert ARGB to hex color (remove alpha)

                try {
                    // Replace matched ARGB code with formatted text
                    matcher.appendReplacement(formattedText, ChatColor.of(hexColor) + Matcher.quoteReplacement(followingText));
                } catch (IllegalArgumentException e) {
                    // If the color conversion fails, keep the original text
                    matcher.appendReplacement(formattedText, Matcher.quoteReplacement("Color:[argb0x" + argb + "]" + followingText));
                }
            }
            matcher.appendTail(formattedText);
            this.text = formattedText.toString();
        }
        return this;
    }

    private void setPlaceholderOfOnlinePlayer() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            this.text = PlaceholderAPI.setPlaceholders(this.player, this.text);
    }

    private void setPlaceholderOfOfflinePlayer() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            this.text = PlaceholderAPI.setPlaceholders(this.offlinePlayer, this.text);
    }

    public TextAPI convertToEnumStringFormat() {
        this.text = this.text.toUpperCase()
                .replaceAll(" +", "_")      // Replace all spaces with single underscores
                .replaceAll("_+", "_")      // Replace all underscores with single underscores
                .replaceAll("^_+|_+$", ""); // Trim leading/trailing underscores
        return this;
    }
}
