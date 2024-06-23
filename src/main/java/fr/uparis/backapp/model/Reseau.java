package fr.uparis.backapp.model;

import fr.uparis.backapp.model.lieu.Station;
import fr.uparis.backapp.model.section.SectionTransport;
import fr.uparis.backapp.utils.Parser;

import java.util.*;

/**
 * Représente le Reseau de transport.
 */
public class Reseau {
    private static Reseau instance = null;
    private static Set<Station> stations;
    private static Set<SectionTransport> sections;

    /**
     * Constructeur privé pour créer une instance de la classe Reseau.
     */
    private Reseau() {
        Parser parser = Parser.getInstance();
        sections = parser.getSections();
        stations = new HashSet<>();
        Collections.addAll(stations, parser.getStations());
    }

    /**
     * Renvoie l'instance de la classe Reseau.
     *
     * @return l'instance de la classe Reseau.
     */
    public static Reseau getInstance() {
        if (instance == null) instance = new Reseau();
        return instance;
    }

    /**
     * Renvoie la liste des Station du Reseau.
     *
     * @return la liste des Station du Reseau.
     */
    public Set<Station> getStations() {
        return stations;
    }

    /**
     * Retrouve une station dans le réseau avec le nom de la station.
     *
     * @param nameStation le nom de la station cherchée.
     * @return la station qui porte le nom qu'on cherche.
     */
    public Station getStation(String nameStation) {
        for (Station s : stations)
            if (s.getNomLieu().equals(nameStation))
                return s;
        return null;
    }

    /**
     * Retrouve une station dans le réseau avec les coordonnées de la station.
     *
     * @param coordonneeStation la coordonnée de la station cherchée.
     * @return la station qui se trouve à la coordonnée précisée.
     */
    public Station getStation(Coordonnee coordonneeStation) {
        for (Station s : stations)
            if (s.getLocalisation().equals(coordonneeStation))
                return s;
        return null;
    }

    /**
     * Ajout d'une station dans le Reseau, si elle n'y est pas déjà.
     *
     * @param station la station à ajouter dans le Reseau.
     */
    public void addStation(Station station) {
        stations.add(station);
    }

    /**
     * Suppression d'une station dans le Reseau, si elle existe, et des sections impactées
     *
     * @param station la station à supprimer du Reseau.
     */
    public void removeStation(Station station) {
        stations.remove(station);
        List<SectionTransport> toDelete = sections.stream()
                .filter(s -> s.isStationDepart(station) || s.isStationArrivee(station))
                .toList();
        for (SectionTransport section : toDelete) removeSection(section);
    }

    /**
     * Renvoie la liste des Section du Reseau.
     *
     * @return la liste des Section du Reseau.
     */
    public Set<SectionTransport> getSections() {
        return sections;
    }

    /**
     * Ajout d'une section dans le Reseau, si elle n'y est pas déjà,
     * et également ajout des stations qui la composent.
     *
     * @param section la section à ajouter dans le Reseau.
     */
    public void addSection(SectionTransport section) {
        sections.add(section);
        addStation(section.getDepart());
        addStation(section.getArrivee());
    }

    /**
     * Suppression d'une section dans le Reseau, si elle existe,
     * et également les stations qui la composent si elles ne sont plus utiles.
     *
     * @param section la section à supprimer du Reseau.
     */
    public void removeSection(SectionTransport section) {
        sections.remove(section);
        if (sections.stream().noneMatch(s -> s.isStationDepart(section.getDepart()) || s.isStationArrivee(section.getDepart())))
            removeStation(section.getDepart());
        if (sections.stream().noneMatch(s -> s.isStationDepart(section.getArrivee()) || s.isStationArrivee(section.getArrivee())))
            removeStation(section.getArrivee());
    }
}
