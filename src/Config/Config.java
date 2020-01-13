package Config;

import java.io.*;
import java.util.Properties;

public class Config {

    private static int height;
    private static int width;
    private static int mines;
    private static String level;
    private static boolean sound;
    private static boolean theme;

    public static void readConfig() {
        try (FileInputStream settingsReader = new FileInputStream("src/Config/settings")) {
            Properties properties = new Properties();
            properties.load(settingsReader);

            level = properties.getProperty("level");
            String[] temp;
            if(level.equalsIgnoreCase("custom"))
                temp = properties.getProperty("custom").split(",");
            else
                temp = getLevelPreset(level.toLowerCase());

            height = Integer.parseInt(temp[0]);
            width = Integer.parseInt(temp[1]);
            mines = Integer.parseInt(temp[2]);

            if(properties.getProperty("sound").equals("On"))
                sound = true;
            else
                sound = false;

            if(properties.getProperty("theme").equals("Dark"))
                theme = true;
            else
                theme = false;
        } catch (Exception e) {
            System.out.println("Error reading configuration file.");
        }
    }

    public static void saveConfig(String l, int h, int w, int m, boolean s, boolean t) {
        sound = s;
        theme = t;
        level = l;

        if(!level.equalsIgnoreCase("custom")) {
            try (FileInputStream levelsReader = new FileInputStream("src/Config/level_definitions")) {
                Properties properties = new Properties();
                properties.load(levelsReader);
                properties.getProperty(level.toLowerCase());
                String[] temp = getLevelPreset(level.toLowerCase());
                height = Integer.parseInt(temp[0]);
                width = Integer.parseInt(temp[1]);
                mines = Integer.parseInt(temp[2]);
            } catch(Exception e) {
                System.out.println("Unable to read level presets.");
            }
        } else {
            height = h;
            width = w;
            mines = m;
        }
    }

    public static void writeConfig() throws IOException {
        FileOutputStream settingsWriter = new FileOutputStream("src/Config/settings");
        Properties properties = new Properties();

        /* level=Medium
        custom=0
        sound=On
        theme=Light*/

        /*easy=9,9,10
        medium=16,16,40
        hard=16,30,99*/
        properties.setProperty("level", level);
        if(level.equalsIgnoreCase("custom")) {
            properties.setProperty("custom", height + "," + width + "," + mines);
        } else
            properties.setProperty("custom", "0");

        if(sound)
            properties.setProperty("sound", "On");
        else
            properties.setProperty("sound", "Off");

        if(theme)
            properties.setProperty("theme", "Dark");
        else
            properties.setProperty("theme", "Light");

        properties.store(settingsWriter, null);

    }

    private static String[] getLevelPreset(String levelStr) throws IOException {
        FileInputStream levelReader = new FileInputStream("src/Config/level_definitions");
        Properties properties = new Properties();
        properties.load(levelReader);
        return properties.getProperty(levelStr).split(",");
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

    public static String getLevel() {
        return level;
    }

    public static boolean getSound() {
        return sound;
    }

    public static boolean getTheme() {
        return theme;
    }
}
