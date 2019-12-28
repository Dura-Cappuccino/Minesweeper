package Config;

import java.io.FileReader;
import java.util.Properties;

public class Config {

    static int height;
    static int width;
    static int mines;
    static String theme;

    public static void readConfig() {
        try (FileReader reader = new FileReader("src/Config/settings")) {
            Properties properties = new Properties();
            properties.load(reader);

            height = Integer.parseInt(properties.getProperty("height"));
            width = Integer.parseInt(properties.getProperty("width"));
            mines = Integer.parseInt(properties.getProperty("mines"));
            theme = properties.getProperty("theme");
        } catch (Exception e) {
            System.out.println("Error reading configuration file.");
        }
    }

    private static void writeConfig(int h, int w, int m, String t) {
        height = h;
        width = w;
        mines = m;
        theme = t;

        try {
            //TODO: write entered values to config file

        } catch (Exception e) {
            System.out.println("cannot write configurations.\n" +
                    "Are you sure your settings are entered correctly?\n");
        }
    }

    public static int getHeight() {
        return height;
    }

    public static int getWidth() {
        return width;
    }

    public static int getMines() {
        return mines;
    }

    public static String getTheme() {
        return theme;
    }
}
