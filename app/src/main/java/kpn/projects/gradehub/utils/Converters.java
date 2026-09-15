package kpn.projects.gradehub.utils;

import androidx.room.TypeConverter;

import kpn.projects.gradehub.Colour;

public class Converters {
    @TypeConverter
    public static Colour toColour(String value) {
        return value == null ? null : Colour.valueOf(value);
    }

    @TypeConverter
    public static String fromColour(Colour colour) {
        return colour == null ? null : colour.name();
    }
}
