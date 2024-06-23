package fr.uparis.backapp.model.lieu;

import fr.uparis.backapp.model.Coordonnee;

import java.time.LocalTime;

/**
 * Représente un Lieu.
 */
public class Lieu {
    final protected String nomLieu;
    final protected Coordonnee localisation;
    protected LocalTime horaireDePassage;

    /**
     * Constructeur de la classe Lieu, à partir de tous les attributs.
     *
     * @param nomLieu le nom du lieu.
     * @param localisation la localisation du lieu.
     * @param horaireDePassage l'horaire de passage au lieu.
     */
    public Lieu(String nomLieu, Coordonnee localisation, LocalTime horaireDePassage) {
        this.nomLieu = nomLieu;
        this.localisation = localisation;
        this.horaireDePassage = horaireDePassage;
    }

    /**
     * Constructeur de la classe Lieu, à partir d'un nom et d'une localisation.
     *
     * @param nomLieu le nom du lieu.
     * @param localisation la localisation du lieu.
     */
    public Lieu(String nomLieu, Coordonnee localisation) {
        this(nomLieu, localisation, null);
    }

    /**
     * Renvoie le nom du lieu.
     *
     * @return le nom du lieu.
     */
    public String getNomLieu() {
        return nomLieu;
    }

    /**
     * Renvoie la Coordonnee du lieu.
     *
     * @return la Coordonne du lieu.
     */
    public Coordonnee getLocalisation() {
        return localisation;
    }

    /**
     * Pour le dernier trajet calculé, renvoie l'horaire auquel il faut être au lieu courant.
     *
     * @return l'horaire auquel il faut être au lieu courant.
     */
    public LocalTime getHoraireDePassage() {
        return horaireDePassage;
    }

    /**
     * Configure l'horaire auquel il faut être au lieu courant.
     *
     * @param horaireDePassage l'horaire auquel il faut être au lieu courant.
     */
    public void setHoraireDePassage(LocalTime horaireDePassage) {
        this.horaireDePassage = horaireDePassage;
    }

    /**
     * Fournit une copie du Lieu courant.
     */
    public Lieu copy() {
        return new Lieu(nomLieu, localisation, horaireDePassage);
    }
}
