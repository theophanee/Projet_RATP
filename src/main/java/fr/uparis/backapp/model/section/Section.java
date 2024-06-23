package fr.uparis.backapp.model.section;

import fr.uparis.backapp.model.Ligne;
import fr.uparis.backapp.model.lieu.Lieu;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Représente une Section de deux lieux.
 */
public class Section {
    final protected Lieu depart;
    final protected Lieu arrivee;
    final protected Duration duree;
    final protected double distance;

    /**
     * Constructeur de la Section avec tous les attributs.
     *
     * @param depart lieu de départ.
     * @param arrivee lieu d'arrivée.
     * @param duree durée estimée de la Section.
     * @param distance distance de la Section.
     */
    public Section(Lieu depart, Lieu arrivee, Duration duree, double distance) {
        if(depart.equals(arrivee)) throw new IllegalArgumentException();
        this.depart = depart;
        this.arrivee = arrivee;
        this.duree = duree;
        this.distance = distance;
    }

    /**
     * Renvoie le lieu de départ de la Section.
     *
     * @return le lieu de départ de la Section.
     */
    public Lieu getDepart() {
        return depart;
    }

    /**
     * Renvoie le lieu d'arrivée de la Section.
     *
     * @return le lieu d'arrivée de la Section.
     */
    public Lieu getArrivee() {
        return arrivee;
    }

    /**
     * Renvoie l'heure du prochain départ.
     *
     * @param depart l'heure actuelle.
     * @return l'heure du prochain départ.
     */
    public LocalTime getHoraireProchainDepart(LocalTime depart) {
        return depart;
    }

    /**
     * Renvoie la Ligne empruntée par la Section.
     *
     * @return la Ligne empruntée par la Section.
     */
    public Ligne getLigne() {
        return null;
    }

    /**
     * Renvoie la durée de la Section.
     *
     * @return la durée de la Section.
     */
    public Duration getDuree() {
        return duree;
    }

    /**
     * Renvoie la distance de la Section.
     *
     * @return la distance de la Section.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Fournit une copie de la Section courante.
     */
    public Section copy() {
        return new Section(depart.copy(), arrivee.copy(), duree, distance);
    }
}
