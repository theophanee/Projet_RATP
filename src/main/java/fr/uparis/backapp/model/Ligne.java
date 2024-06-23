package fr.uparis.backapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.uparis.backapp.model.lieu.Station;

import java.time.LocalTime;
import java.util.*;

/**
 * Représente une Ligne de transport dans le Reseau.
 */
public class Ligne {
    final private String nomLigne; //nom de la ligne, unique
    @JsonIgnore
    final private Set<Station> stations; //stations de la ligne
    final private Set<LocalTime> horairesDepart;//TODO it will maybe deleted, to be discussed

    /**
     * Constructeur de la classe Ligne à partir de tous les attributs.
     *
     * @param nomLigne       nom de la Ligne.
     * @param stations       liste des Station desservies par la Ligne.
     * @param horairesDepart liste des horaires de departs de cette Ligne.
     */
    public Ligne(String nomLigne, Set<Station> stations, Set<LocalTime> horairesDepart) {
        this.nomLigne = nomLigne;
        this.stations = stations;
        this.horairesDepart = horairesDepart;
    }

    /**
     * Constructeur de la classe Ligne à partir du nom de la Ligne.
     *
     * @param nomLigne nom de la Ligne.
     */
    public Ligne(String nomLigne) {
        this(nomLigne, new LinkedHashSet<>(), new HashSet<>());
    }

    /**
     * Renvoie le nom de la Ligne.
     *
     * @return le nom de la Ligne.
     */
    public String getNomLigne() {
        return nomLigne;
    }

    /**
     * Renvoie la liste des Station de la Ligne.
     *
     * @return la liste des Station de la Ligne.
     */
    public Set<Station> getStations() {
        return stations;
    }

    /**
     * Ajout d'une station à la Ligne, si elle n'y est pas déjà.
     *
     * @param station la station à ajouter à la Ligne.
     */
    public void addStation(Station station) {
        this.stations.add(station);
    }

    /**
     * Suppression d'une station de la Ligne, si elle existe.
     *
     * @param station la station à enlever de la Ligne.
     */
    public void removeStation(Station station) {
        this.stations.remove(station);
    }

    /**
     * Renvoie la liste des horaires de départ de la Ligne.
     *
     * @return la liste des horaires de départ de la Ligne.
     */
    public Set<LocalTime> getHorairesDepart() {
        return horairesDepart;
    }

    /**
     * Ajout d'un horaire de départ à la Ligne, si elle n'y est pas déjà.
     *
     * @param horaire l'horaire de départ à ajouter à la Ligne.
     */
    public void addHoraireDepart(LocalTime horaire) {
        this.horairesDepart.add(horaire);
    }

    /**
     * Suppression d'un horaire de départ de la Ligne, si elle existe.
     *
     * @param horaire l'horaire de départ à enlever de la Ligne.
     */
    public void removeHoraireDepart(LocalTime horaire) {
        this.horairesDepart.remove(horaire);
    }

    /**
     * Renvoie la direction de la ligne.
     *
     * @return le terminus de la ligne.
     */
    public Station getDirection() {
        return (Station) stations.toArray()[stations.size() - 1];
    }

    /**
     * Comparaison de deux Ligne.
     *
     * @param o objet avec lequel comparer.
     * @return true si les objets comparés portent le même nom, false sinon.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ligne ligne = (Ligne) o;
        return Objects.equals(nomLigne, ligne.nomLigne);
    }

    /**
     * Retourne une valeur de code de hachage pour Ligne.
     *
     * @return la valeur de code de hachage pour Ligne.
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + nomLigne.hashCode();
        result = 31 * result + stations.hashCode();
        result = 31 * result + horairesDepart.hashCode();
        return result;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères d'un objet Ligne.
     *
     * @return la représentation sous forme de chaîne de caractères d'un objet Ligne.
     */
    @Override
    public String toString() {
        String s = nomLigne + " : ";
        for (Station station : stations) s += station.getNomLieu() + " ";
        for (LocalTime time : horairesDepart) s += "\n    " + time;
        return s;
    }
}
