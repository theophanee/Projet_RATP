package fr.uparis.backapp.config;

import fr.uparis.backapp.utils.constants.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton de configuration.
 */
public class Config {
    private static Config instance = null;
    private static Properties prop;

    /**
     * Constructeur pour le singleton Config.
     */
    private Config() {
        prop = new Properties();
        try(InputStream input = Config.class.getClassLoader().getResourceAsStream(Constants.APPLICATION_PROPERTIES)) {
            prop.load(input);
        }
        catch(IOException e) {
            throw new RuntimeException("Erreur lors du chargement des propriétés", e);
        }
    }

    /**
     * Renvoie l'instance de la classe Config.
     *
     * @return l'instance de la classe Config.
     */
    public static Config getInstance() {
        if(instance == null) instance = new Config();
        return instance;
    }

    /**
     * Renvoie une propriété qui a pour clé key.
     *
     * @param key la clé de la propriété demandée.
     * @return La propriété correspondant à la clé.
     */
    public String getProperty(String key) {
        return prop.getProperty(key);
    }
}