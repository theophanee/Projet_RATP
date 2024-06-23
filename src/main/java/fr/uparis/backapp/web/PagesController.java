package fr.uparis.backapp.web;

import fr.uparis.backapp.model.section.Section;
import fr.uparis.backapp.services.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;


@RestController
public class PagesController {
    private final ItineraryService itineraryService;

    /**
     * Constructeur de la classe PagesController, à partir d'un itineraryService.
     *
     * @param itineraryService le service qui fournit les fonctions utiles pour la recherche d'itinéraires.
     */
    @Autowired
    public PagesController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    /**
     * Recherche un itinéraire entre deux lieux spécifiés à un moment donné.
     *
     * @param origin      la station ou les coordonnées de départ.
     * @param destination la station ou les coordonnées d'arrivée.
     * @param time        l'heure de départ.
     * @return la liste des itinéraires possibles sous forme de tableau de sections.
     */
    @ResponseBody
    @GetMapping("itinerary/optimal")
    public List<Section[]> searchItinerary(@RequestParam("origin") String origin, @RequestParam("destination") String destination, @RequestParam("time") String time) {
        return itineraryService.searchItinerary(origin, destination, time);
    }

    /**
     * Recherche un itinéraire entre deux lieux spécifiés à un moment donné.
     *
     * @param origin      la station ou les coordonnées de départ.
     * @param destination la station ou les coordonnées d'arrivée.
     * @param time        l'heure de départ.
     * @param distanceMax la distance maximale à parcourir à pied.
     * @return la liste des itinéraires possibles sous forme de tableau de sections.
     */
    @ResponseBody
    @GetMapping("itinerary/lazy")
    public List<Section[]> searchLazyItinerary(@RequestParam("origin") String origin, @RequestParam("destination") String destination, @RequestParam("time") String time, @RequestParam("distanceMax") double distanceMax) {
        return itineraryService.searchLazyItinerary(origin, destination, time, distanceMax);
    }

    /**
     * Recherche un itinéraire à pied entre deux lieux spécifiés à un moment donné.
     *
     * @param origin      la station ou les coordonnées de départ.
     * @param destination la station ou les coordonnées d'arrivée.
     * @param time        l'heure de départ.
     * @return la liste des itinéraires possibles sous forme de tableau de sections.
     */
    @ResponseBody
    @GetMapping("itinerary/fullSport")
    public List<Section[]> searchFullSportItinerary(@RequestParam("origin") String origin, @RequestParam("destination") String destination, @RequestParam("time") String time) {
        return itineraryService.searchFullSportItinerary(origin, destination, time);
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
    @ResponseBody
    @GetMapping("itinerary/sport/distance")
    public List<Section[]> searchItineraryWithMinWalkingDistance(@RequestParam("origin") String origin, @RequestParam("destination") String destination, @RequestParam("time") String time, @RequestParam("distanceMin") double distanceMin) {
        return itineraryService.searchItineraryWithMinWalkingDistance(origin, destination, time, distanceMin);
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
    @ResponseBody
    @GetMapping("itinerary/sport/time")
    public List<Section[]> searchItineraryWithMinWalkingDuration(@RequestParam("origin") String origin, @RequestParam("destination") String destination, @RequestParam("time") String time, @RequestParam("walkingTimeMin") double walkingTimeMin) {
        return itineraryService.searchItineraryWithMinWalkingMinutes(origin, destination, time, walkingTimeMin);
    }

    /**
     * L'autocomplétion de la saisie dans la barre de recherche de stations.
     *
     * @param term le préfixe de la station recherchée.
     * @return la liste des stations qui ont le préfixe demandé.
     */
    @GetMapping("/autocomplete")
    @ResponseBody
    public List<String> getAutocompleteSuggestions(@RequestParam("term") String term) {
        return itineraryService.autocomplete(term);
    }

    /**
     * Retourne tous les horaires de passage des trains pour une station donnée.
     *
     * @param station la station pour laquelle on cherche les horaires de passage.
     * @return les horaires de passage des trains, avec la direction correspondante.
     */
    @ResponseBody
    @GetMapping("/schedules")
    public Map<String, List<LocalTime>> getStationSchedules(@RequestParam("station") String station) {
        return itineraryService.getStationSchedules(station);
    }
}