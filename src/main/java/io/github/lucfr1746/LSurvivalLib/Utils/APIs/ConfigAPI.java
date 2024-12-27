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
import java.nio.file.StandardCopyOption;

public class ConfigAPI {

    private final Plugin plugin;
    private final File pluginPath;

    public ConfigAPI(String exactlyPluginName) throws LException.PluginNotFoundException {
        this.plugin = Bukkit.getPluginManager().getPlugin(exactlyPluginName);
        if (this.plugin == null) {
            throw new LException.PluginNotFoundException(exactlyPluginName);
        }
        pluginPath = plugin.getDataFolder();
    }

    public ConfigAPI(Plugin plugin) throws LException.PluginNotFoundException {
        if (plugin == null) {
            throw new LException.PluginNotFoundException("Error while input Plugin");
        }
        this.plugin = plugin;
        pluginPath = plugin.getDataFolder();
    }

    public File createFolder(String parentFolder, String childFolder) throws LException.CannotCreateFolderException {
        File folder = new File(parentFolder, childFolder);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new LException.CannotCreateFolderException("Cannot create folder: " + folder.getAbsolutePath());
        }
        return folder;
    }

    public File createYamlFile(String path, String filename) throws LException.CannotCreateFileException {
        if (!filename.endsWith(".yml")) filename += ".yml";

        Path filePath = new File(path, filename).toPath();

        try {
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
        } catch (IOException e) {
            throw new LException.CannotCreateFileException("Failed to create file: " + filename);
        }

        return new File(path, filename);
    }

    public FileConfiguration createYamlFileConfiguration(String path, String filename) {
        return YamlConfiguration.loadConfiguration(createYamlFile(path, filename));
    }

    public File createDefaultYamlFile(String path, String extendPathInResources, String filename) throws LException.CannotCreateFileException {
        if (!filename.endsWith(".yml")) filename += ".yml";
        return createDefaultFile(path, extendPathInResources, filename);
    }

    public File createDefaultFile(String path, String extendPathInResources, String filename) throws LException.CannotCreateFileException {
        Path filePath = new File(path, filename).toPath();

        try {
            if (Files.notExists(filePath)) {
                try (InputStream inputStream = plugin.getResource(extendPathInResources + filename)) {
                    if (inputStream == null) {
                        throw new LException.CannotCreateFileException("Resource not found: " + filename);
                    }
                    Files.createDirectories(filePath.getParent());
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            throw new LException.CannotCreateFileException("Failed to create file: " + filename);
        }
        return new File(path, filename);
    }

    public FileConfiguration createDefaultYamlFileConfiguration(String path, String extendPathInResources, String filename) {
        return YamlConfiguration.loadConfiguration(createDefaultYamlFile(path, extendPathInResources, filename));
    }

    public FileConfiguration getYamlConfiguration(String path, String filename) throws LException.FileNotFoundException {
        if (!filename.endsWith(".yml")) filename += ".yml";

        File file = new File(path, filename);

        if (!file.exists()) {
            throw new LException.FileNotFoundException("File not found: " + file.getAbsolutePath());
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getOrCreateYamlConfiguration(String path, String filename) {
        if (!filename.endsWith(".yml")) filename += ".yml";
        return new File(path, filename).exists() ? getYamlConfiguration(path, filename) : createYamlFileConfiguration(path, filename);
    }

    public File getFile(String path, String filename) throws LException.FileNotFoundException {
        File file = new File(path, filename);

        if (!file.exists()) {
            throw new LException.FileNotFoundException("File not found: " + file.getAbsolutePath());
        }
        return file;
    }
}
