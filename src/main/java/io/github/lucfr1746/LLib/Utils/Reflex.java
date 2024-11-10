package io.github.lucfr1746.LLib.Utils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class Reflex {

    public static Field getField(Class<?> source, String name) {
        try {
            return source.getDeclaredField(name);
        }
        catch (NoSuchFieldException exception) {
            Class<?> superClass = source.getSuperclass();
            return superClass == null ? null : getField(superClass, name);
        }
    }

    public static Object getFieldValue(Object source, String name) {
        try {
            Class<?> clazz = source instanceof Class<?> ? (Class<?>) source : source.getClass();
            Field field = getField(clazz, name);
            if (field == null) return null;

            field.setAccessible(true);
            return field.get(source);
        }
        catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static boolean setFieldValue(Object source, String name, @Nullable Object value) {
        try {
            boolean isStatic = source instanceof Class;
            Class<?> clazz = isStatic ? (Class<?>) source : source.getClass();

            Field field = getField(clazz, name);
            if (field == null) return false;

            field.setAccessible(true);
            field.set(isStatic ? null : source, value);
            return true;
        }
        catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
        return false;
    }
}
