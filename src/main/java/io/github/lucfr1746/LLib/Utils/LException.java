package io.github.lucfr1746.LLib.Utils;

public class LException extends Exception {

    public static class PluginNotFoundException extends RuntimeException {
        public PluginNotFoundException(String pluginName) {
            super("Plugin not found: " + pluginName);
        }
    }

    public static class CannotCreateFolderException extends RuntimeException {
        public CannotCreateFolderException(String message) {
            super(message);
        }
    }

    public static class CannotCreateFileException extends RuntimeException {
        public CannotCreateFileException(String message) {
            super(message);
        }
    }

    public static class FileNotFoundException extends RuntimeException {
        public FileNotFoundException(String message) {
            super(message);
        }
    }

    public static class ConfigInitializationException extends RuntimeException {
        public ConfigInitializationException(String message) {
            super(message);
        }
    }
}
