package fr.uparis.backapp.model;


import static fr.uparis.backapp.utils.constants.Constants.COORDINATE_REGEX;

/**
 * Représente une coordonnée gps.
 */
public class Coordonnee {
    final private double latitude; //latitude en degré
    final private double longitude; //longitude en degré

    /**
     * Constructeur de la classe Coordonnee à partir de deux doubles.
     *
     * @param latitude  latitude de la coordonnée en degré.
     * @param longitude longitude de la coordonnée en degré.
     */
    public Coordonnee(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Constructeur de la classe Coordonne à partir d'une chaîne de caractères correctement formatée.
     *
     * @param coordonnee latitude et longitude séparées par une virgule.
     */
    public Coordonnee(String coordonnee) {
        Double[] splitCoordonnee = splitCoordonnee(coordonnee);
        this.latitude = splitCoordonnee[1];
        this.longitude = splitCoordonnee[0];
    }

    /**
     * Vérifie si une chaîne de caractères est bien dans le format désiré, afin d'instancier une Coordonnee avec.
     * Dans le cas contraire, une exception est levée.
     *
     * @param coordonnee hypothétiquement une chaîne de caractères représentant latitude et longitude séparées par une virgule.
     * @return un tableau de doubles, comportant respectivement latitude et longitude en son sein.
     */
    private Double[] splitCoordonnee(String coordonnee) {
        String[] splitCoordonnee = coordonnee.split(",");
        if (splitCoordonnee.length != 2)
            throw new IllegalArgumentException("Arguments invalides pour la construction de coordonnée" + coordonnee);
        return new Double[]{Double.parseDouble(splitCoordonnee[0]), Double.parseDouble(splitCoordonnee[1])};
    }

    /**
     * Renvoie la latitude en degré.
     *
     * @return la latitude en degré.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Renvoie la latitude de la coordonnée en radian, après conversion depuis des degrés.
     *
     * @return la latitude en radian.
     */
    public double getLatitudeRadian() {
        return Math.toRadians(latitude);
    }

    /**
     * Renvoie la longitude en degré.
     *
     * @return la longitude en degré.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Renvoie la longitude de la coordonnée en radian, après conversion depuis des degrés.
     *
     * @return la longitude en radian.
     */
    public double getLongitudeRadian() {
        return Math.toRadians(longitude);
    }

    /**
     * Vérifie si une chaîne de caractères donnée correspond à une coordonnée géographique valide,
     * au format décimal (latitude, longitude).
     *
     * @param input La chaîne de caractères à vérifier.
     * @return true si la chaîne de caractères est une coordonnée valide, false sinon.
     */
    public static boolean isCoordinate(String input) {
        return input.matches(COORDINATE_REGEX);
    }

    /**
     * Comparaison de deux coordonnées.
     *
     * @param o objet avec lequel comparer.
     * @return true si les objets comparés ont les mêmes latitude et longitude, false sinon.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordonnee coordonnee = (Coordonnee) o;
        return Double.compare(coordonnee.latitude, latitude) == 0
                && Double.compare(coordonnee.longitude, longitude) == 0;
    }

    /**
     * Retourne une valeur de code de hachage pour Coordonnee.
     *
     * @return la valeur de code de hachage pour Coordonnee.
     */
    @Override
    public int hashCode() {
        int result = 17;
        long temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères d'un objet Coordonnee.
     *
     * @return la représentation sous forme de chaîne de caractères d'un objet Coordonnee.
     */
    @Override
    public String toString() {
        return "latitude = " + this.latitude + " ; longitude = " + this.longitude;
    }
}
