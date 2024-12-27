package io.github.lucfr1746.LSurvivalLib.Utils.APIs;

import io.github.lucfr1746.LSurvivalLib.Utils.Utils.LException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileAPI {

    private final Plugin plugin;
    private final File pluginPath;

    public FileAPI(String exactlyPluginName) {
        try {
            this.plugin = Bukkit.getPluginManager().getPlugin(exactlyPluginName);
            if (this.plugin == null) {
                throw new LException.PluginNotFoundException(exactlyPluginName);
            }
        } catch (LException.PluginNotFoundException e) {
            throw new RuntimeException(e);
        }
        pluginPath = plugin.getDataFolder();
    }

    public FileAPI(Plugin plugin) {
        try {
            this.plugin = plugin;
            pluginPath = plugin.getDataFolder();
        } catch (LException.PluginNotFoundException e) {
            throw new LException.PluginNotFoundException("Error while input Plugin");
        }
    }

    public File createFolder(String parentFolder, String childFolder) {
        Path folderPath = Paths.get(parentFolder, childFolder);
        try {
            if (Files.notExists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            return folderPath.toFile();
        } catch (IOException e) {
            throw new LException.CannotCreateFolderException("Failed to create folder at: " + folderPath.toAbsolutePath());
        }
    }

    public File createYamlFile(String directory, String filename) throws LException.CannotCreateFileException {
        String validFilename = ensureYamlExtension(filename);
        Path filePath = Paths.get(directory, validFilename);

        try {
            createParentDirectories(filePath);
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new LException.CannotCreateFileException("Failed to create YAML file: " + filePath);
        }

        return filePath.toFile();
    }

    public FileConfiguration createYamlFileConfiguration(String directory, String filename) {
        try {
            return YamlConfiguration.loadConfiguration(createYamlFile(directory, filename));
        } catch (LException.CannotCreateFileException e) {
            throw new RuntimeException("Unable to create YAML configuration for: " + filename, e);
        }
    }

    public File createDefaultYamlFile(String directory, String resourcePath, String filename) throws LException.CannotCreateFileException {
        return createDefaultFile(directory, resourcePath, ensureYamlExtension(filename));
    }

    public File createDefaultFile(String directory, String resourcePath, String filename) throws LException.CannotCreateFileException {
        Path filePath = Paths.get(directory, filename);

        try {
            if (Files.notExists(filePath)) {
                createParentDirectories(filePath);
                try (InputStream inputStream = plugin.getResource(resourcePath + filename)) {
                    if (inputStream == null) {
                        throw new LException.CannotCreateFileException("Resource not found: " + filename);
                    }
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            throw new LException.CannotCreateFileException("Failed to create default file: " + filePath);
        }

        return filePath.toFile();
    }

    public FileConfiguration createDefaultYamlFileConfiguration(String directory, String resourcePath, String filename) {
        try {
            return YamlConfiguration.loadConfiguration(createDefaultYamlFile(directory, resourcePath, filename));
        } catch (LException.CannotCreateFileException e) {
            throw new RuntimeException("Unable to create default YAML configuration for: " + filename, e);
        }
    }

    public FileConfiguration getYamlConfiguration(String directory, String filename) throws LException.FileNotFoundException {
        File file = validateFileExists(directory, ensureYamlExtension(filename));
        return YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getOrCreateYamlConfiguration(String directory, String filename) {
        String validFilename = ensureYamlExtension(filename);
        File file = Paths.get(directory, validFilename).toFile();
        return file.exists() ? getYamlConfiguration(directory, validFilename) : createYamlFileConfiguration(directory, validFilename);
    }

    public File getFile(String directory, String filename) throws LException.FileNotFoundException {
        return validateFileExists(directory, filename);
    }

    // --- Helper Methods ---

    private String ensureYamlExtension(String filename) {
        return filename.endsWith(".yml") ? filename : filename + ".yml";
    }

    private void createParentDirectories(Path filePath) throws IOException {
        Files.createDirectories(filePath.getParent());
    }

    private File validateFileExists(String directory, String filename) throws LException.FileNotFoundException {
        Path filePath = Paths.get(directory, filename);
        if (Files.notExists(filePath)) {
            throw new LException.FileNotFoundException("File not found: " + filePath.toAbsolutePath());
        }
        return filePath.toFile();
    }
}
