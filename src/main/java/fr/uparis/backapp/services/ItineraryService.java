package fr.uparis.backapp.services;

import fr.uparis.backapp.exceptions.StationNotFoundException;
import fr.uparis.backapp.model.Coordonnee;
import fr.uparis.backapp.model.Reseau;
import fr.uparis.backapp.model.lieu.Station;
import fr.uparis.backapp.model.section.Section;
import fr.uparis.backapp.model.section.SectionTransport;
import fr.uparis.backapp.utils.Calculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.uparis.backapp.utils.Calculator.itineraireFactory;
import static fr.uparis.backapp.utils.Utils.*;
import static fr.uparis.backapp.utils.constants.Constants.DELIMITER;

@Service
public class ItineraryService {
    private final Reseau reseau;

    @Autowired
    public ItineraryService() {
        this.reseau = Reseau.getInstance();
    }


    /**
     * Recherche un itinéraire entre deux lieux spécifiés à un moment donné.
     *
     * @param origin      la station ou les coordonnées de départ.
     * @param destination la station ou les coordonnées d'arrivée.
     * @param time        l'heure de départ.
     * @return la liste des itinéraires possibles sous forme de tableau de sections.
     */
    public List<Section[]> searchItinerary(String origin, String destination, String time) {
        List<Section[]> trajects;
        LocalTime trajectTime = getTimeFromString(time);
        try {
            Coordonnee originCoordinates = fetchCoordinates(origin);
            Coordonnee destinationCoordinates = fetchCoordinates(destination);
            trajects = itineraireFactory(originCoordinates, destinationCoordinates, trajectTime);
        } catch (StationNotFoundException e) {
            trajects = new ArrayList<>();
        }
        return trajects;
    }

    /**
     * Recherche un itinéraire entre deux lieux spécifiés à un moment donné avec une distance de marche minimale sur l'ensemble du trajet.
     *
     * @param origin      la station ou les coordonnées de départ.
     * @param destination la station ou les coordonnées d'arrivée.
     * @param time        l'heure de départ.
     * @param distanceMax la distance de marche maximum.
     * @return la liste des itinéraires possibles sous forme de tableau de sections.
     */
    public List<Section[]> searchLazyItinerary(String origin, String destination, String time, double distanceMax) {
        Calculator.changeMarcherAuPlus(distanceMax);
        return searchItinerary(origin, destination, time);
    }

    /**
     * Recherche un itinéraire à pied entre deux lieux spécifiés à un moment donné.
     *
     * @param origin      la station ou les coordonnées de départ.
     * @param destination la station ou les coordonnées d'arrivée.
     * @param time        l'heure de départ.
     * @return la liste des itinéraires possibles sous forme de tableau de sections.
     */
    public List<Section[]> searchFullSportItinerary(String origin, String destination, String time) {
        Calculator.changeAPied();
        return searchItinerary(origin, destination, time);
    }

    /**
     * Recherche un itinéraire entre deux lieux spécifiés à un moment donné en marchant au moins une certaine distance.
     *
     * @param origin      la station ou les coordonnées de départ.
     * @param destination la station ou les coordonnées d'arrivée.
     * @param time        l'heure de départ.
     * @param distanceMin la distance de marche maximum.
     * @return la liste des itinéraires possibles sous forme de tableau de sections.
     */
    public List<Section[]> searchItineraryWithMinWalkingDistance(String origin, String destination, String time, double distanceMin) {
        Calculator.changeMarcherAuMoinsDistance(distanceMin);
        return searchItinerary(origin, destination, time);
    }

    /**
     * Recherche un itinéraire entre deux lieux spécifiés à un moment donné en marchant au moins pendant une certaine durée.
     *
     * @param origin      la station ou les coordonnées de départ.
     * @param destination la station ou les coordonnées d'arrivée.
     * @param time        l'heure de départ.
     * @param walkingTimeMin la distance de marche maximum.
     * @return la liste des itinéraires possibles sous forme de tableau de sections.
     */
    public List<Section[]> searchItineraryWithMinWalkingMinutes(String origin, String destination, String time, double walkingTimeMin) {
        Calculator.changeMarcherAuMoinsTemps(Duration.ofMinutes((long) walkingTimeMin));
        return searchItinerary(origin, destination, time);
    }

    /**
     * L'autocomplétion de la saisie dans la barre de recherche de stations.
     *
     * @param prefix le préfixe de la station recherchée.
     * @return la liste des stations qui ont le préfixe demandé.
     */
    public List<String> autocomplete(String prefix) {
        List<Station> stationSuggested = reseau.getStations().stream().filter(station -> station.getNomLieu().toLowerCase().startsWith(prefix)).toList();
        List<String> stationSuggestedNames = new ArrayList<>();
        stationSuggested.forEach(station -> stationSuggestedNames.add(station.getNomLieu() + DELIMITER + station.getLocalisation().getLongitude() + DELIMITER + station.getLocalisation().getLatitude()));
        return stationSuggestedNames;
    }

    /**
     * Retourne tous les horaires de passage des trains pour une station donnée.
     *
     * @param stationName la station pour laquelle on cherche les horaires de passage.
     * @return les horaires de passage des trains, avec la direction correspondante.
     */
    public Map<String, List<LocalTime>> getStationSchedules(String stationName) {
        Station station = reseau.getStation(stationName);

        if (station == null)
            return new HashMap<>();
        //Recupérer toutes les sections qui partent de cette station
        List<SectionTransport> sectionTransports = reseau.getSections().stream().filter(section -> section.isStationDepart(station)).toList();
        return getSchedulesByLine(sectionTransports);
    }
}