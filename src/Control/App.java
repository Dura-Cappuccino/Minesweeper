package Control;

import java.io.FileReader;
import java.util.Arrays;
import java.util.Properties;

public class App {

    public static void main(String[] args) {
        //parse through settings and make a new game
        String[] config = readConfig();

        //define default level settings
        int height = 16;
        int width = 16;
        int mines = 39;
        try {
            height = Integer.parseInt(config[0]);
            width = Integer.parseInt(config[1]);
            mines = Integer.parseInt(config[2]);
        } catch (Exception e) {
            System.out.println("cannot parse configurations.\n" +
                    "Are ou sure your settings are saved correctly?\n");
        }
        Game playgame = new Game(height, width, mines);
        playgame.play();
    }

    private static String[] readConfig() {
        String[] config = new String[4];
        try (FileReader reader = new FileReader("src/Config/settings")) {
            Properties properties = new Properties();
            properties.load(reader);

            config[0] = properties.getProperty("height");
            config[1] = properties.getProperty("width");
            config[2] = properties.getProperty("mines");
            config[3] = properties.getProperty("theme");
        } catch (Exception e) {
            System.out.println("Error reading configuration file.");
        }
        return config;
    }
}
