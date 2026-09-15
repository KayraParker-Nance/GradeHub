package kpn.projects.gradehub;

public enum Colour {
    INDIGO("#4B5FE3"),
    TEAL("#12967F"),
    ORANGE("#E58A2A"),
    ROSE("#D5497B"),
    PURPLE("#8A4FD1"),
    BLUE("#2B87D8"),
    GREEN("#3F9B4F"),
    AMBER("#D6A419");

    private final String hex;

    Colour(String hex) {
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }
}
