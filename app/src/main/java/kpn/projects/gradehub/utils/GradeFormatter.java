package kpn.projects.gradehub.utils;

import java.util.Locale;

public final class GradeFormatter {

    private GradeFormatter() {
    }

    public static String percent(double value, int decimalPlaces) {
        return String.format(Locale.getDefault(), "%." + decimalPlaces + "f%%", value);
    }
}
