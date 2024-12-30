package io.github.lucfr1746.LSurvivalLib.Utils.APIs;

import org.bukkit.Color;

public class ColorAPI {

    public enum BukkitColor {
        BLACK(0,0,0),
        DARK_BLUE(0,0,170),
        DARK_GREEN(0,170,0),
        DARK_AQUA(0,170,170),
        DARK_RED(170,0,0),
        DARK_PURPLE(170,0,170),
        GOLD(255,170,0),
        GRAY(170,170,170),
        DARK_GRAY(85,85,85),
        BLUE(85,85,255),
        GREEN(85,255,85),
        AQUA(85,255,255),
        RED(255,85,85),
        LIGHT_PURPLE(255,85,255),
        YELLOW(255,255,0),
        WHITE(255,255,255);

        private final int red;
        private final int green;
        private final int blue;

        BukkitColor(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public int getRed() {
            return red;
        }

        public int getGreen() {
            return green;
        }

        public int getBlue() {
            return blue;
        }
    }

    /**
     * Converts a {@link Color} object to a hexadecimal color string.
     *
     * @param color the {@link Color} object to be converted
     * @return a string representing the color in hexadecimal format, prefixed with '#'
     *         (e.g., "#FF5733" for an RGB color with red=255, green=87, blue=51)
     */
    public static String colorToHex(Color color) {
        if (color == null) return null;
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return String.format("#%02X%02X%02X", r, g, b);
    }

    /**
     * Converts a hexadecimal color string to a {@link Color} object.
     *
     * <p>This method supports 3-character and 6-character hexadecimal color strings.
     * For example, "#F00" will be converted to red (255, 0, 0).</p>
     *
     * @param hex the hexadecimal color string to be converted (with or without a leading '#')
     * @return a {@link Color} object representing the color corresponding to the given hexadecimal string
     * @throws IllegalArgumentException if the provided string is not a valid hex color
     */
    public static Color hexToColor(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        if (hex.length() == 3) {
            // Convert 3-character hex to 6-character hex
            hex = String.valueOf(hex.charAt(0)) + hex.charAt(0) +
                    hex.charAt(1) + hex.charAt(1) +
                    hex.charAt(2) + hex.charAt(2);
        } else if (hex.length() != 6) {
            throw new IllegalArgumentException("Invalid hex color format: " + hex);
        }

        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return Color.fromRGB(r, g, b);
    }
}
