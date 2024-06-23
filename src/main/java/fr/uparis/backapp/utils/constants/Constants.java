package fr.uparis.backapp.utils.constants;

/**
 * Classe qui rassemble les constantes du projet.
 */
public class Constants {
    /**
     * Constante pour obtenir les propriétés de l'application.
     */
    public static final String APPLICATION_PROPERTIES = "application.properties";

    /**
     * Chemin vers le fichier CSV qui contient les données du réseau.
     */
    public static final String MAP_DATA_FILE_PATH_PROPERTY = "map.data.file.path";

    /**
     * Chemin vers le fichier CSV qui contient les horaires des trains.
     */
    public static final String SCHEDULES_FILE_PATH_PROPERTY = "schedules.file.path";

    /**
     * Délimiteur utilisé par le fichier CSV.
     */
    public static final String DELIMITER = ";";

    /**
     * Délimiteur deux points.
     */
    public static final String COLON = ":";

    /**
     * Délimiteur espace.
     */
    public static final String SPACE = " ";


    /**
     * Expression régulière pour une coordonnée.
     */
    public static final String COORDINATE_REGEX = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?"
            + "(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$";


    /**
     * Index de la station de départ dans le fichier CSV des données du réseau.
     */
    public static final int STATION_DEPART_INDEX = 0;

    /**
     * Index de la station d'arrivée dans le fichier CSV des données du réseau.
     */
    public static final int STATION_ARRIVEE_INDEX = 2;

    /**
     * Index des coordonnées de la station de départ dans le fichier CSV des données du réseau.
     */
    public static final int STATION_DEPART_COORDONEES_INDEX = 1;

    /**
     * Index des coordonnées de la station d'arrivée dans le fichier CSV des données du réseau.
     */
    public static final int STATION_ARRIVEE_COORDONEES_INDEX = 3;

    /**
     * Index du nom de la ligne dans le fichier CSV des données du réseau.
     */
    public static final int NOM_LIGNE_INDEX = 4;

    /**
     * Index de la durée entre les deux stations dans le fichier CSV des données du réseau.
     */
    public static final int DUREE_INDEX = 5;

    /**
     * Index de la distance entre les deux stations dans le fichier CSV des données du réseau.
     */
    public static final int DISTANCE_INDEX = 6;


    /**
     * Index du numéro de la ligne dans le fichier CSV des horaires.
     */
    public static final int SCHEDULES_FILE_LINE_INDEX = 0;

    /**
     * Index du nom de la station (terminus) dans le fichier CSV des horaires.
     */
    public static final int SCHEDULES_FILE_TERMINUS_INDEX = 1;

    /**
     * Index de l'horaire de départ de la ligne dans le fichier CSV des horaires.
     */
    public static final int SCHEDULES_FILE_TIME_INDEX = 2;

    /**
     * Index du numéro de variant de la ligne dans le fichier CSV des horaires.
     */
    public static final int SCHEDULES_FILE_VARIANTE_INDEX = 3;


    /**
     * Nom du lieu de départ.
     */
    public static final String DEPART = "Départ";

    /**
     * Nom du lieu d'arrivée.
     */
    public static final String ARRIVEE = "Arrivée";

    /**
     * Valeur par défaut de la distance minimale pour trouver les stations proches d'une coordonnée, en km.
     */
    public static final double DEFAULT_MIN_DISTANCE = 0.0;

    /**
     * Valeur par défaut de l'écart de distance entre la distance minimale et la distance maximale pour trouver les stations proches d'une coordonnée, en km.
     */
    public static final double DEFAULT_ECART_DISTANCE = 5.0 / 6.0; //10 minutes de marche


    /**
     * Vitesse moyenne de marche en km/h.
     */
    public static final double AVERAGE_WALKING_SPEED = 5.0;


    /**
     * Nombre maximal de trajets à renvoyer à l'utilisateur.
     */
    public static final int MAX_TRAJETS_NUMBER = 5;

    /**
     * Pénalité appliquée pour les correspondances.
     */
    public static final int PENALTY = 2;
}
