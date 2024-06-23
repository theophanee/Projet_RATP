package fr.uparis.backapp.web;

import fr.uparis.backapp.model.Coordonnee;
import fr.uparis.backapp.model.Reseau;
import fr.uparis.backapp.model.section.Section;
import fr.uparis.backapp.services.ItineraryService;
import fr.uparis.backapp.utils.Calculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPageController{
    PagesController controller = new PagesController(new ItineraryService());
    Reseau reseau = Reseau.getInstance();

    /**
     * S'assure qu'il n'y a pas de concurrence pour les différents tests.
     */
    @BeforeEach
    public void waitFinish() {
        while(Calculator.getIsCalculating()) {
            try {
                Thread.sleep(1000);
            }
            catch(InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Renvoie les coordonnées sous forme de String, dans le format de la requête js.
     *
     * @param coordonnee les coordonnées à convertir
     * @return la conversion vers String des coordonnées demandées.
     */
    private String coordonneeToString(Coordonnee coordonnee) {
        return coordonnee.getLongitude()+","+coordonnee.getLatitude();
    }

    /**
     * Renvoie l'horaire sous forme de String, dans le format de la requête js.
     *
     * @param horaire l'horaire à convertir
     * @return la conversion vers String de l'horaire demandé.
     */
    private String horaireToString(LocalTime horaire) {
        return horaire.getHour()+":"+horaire.getMinute();
    }

    /**
     * Comparaison de deux trajets.
     *
     * @param trajet1 un des trajets à comparer.
     * @param trajet2 un des trajets à comparer.
     * @return si les deux trajets sont identiques ou non.
     */
    private boolean sameTrajet(List<Section[]> trajet1, List<Section[]> trajet2) {
        if(trajet1.size() != trajet2.size()) return false;

        Section[] sections1, sections2;
        for(int i = 0; i < trajet1.size() ; i++) {
            sections1 = trajet1.get(i);
            sections2 = trajet2.get(i);
            if(sections1.length != sections2.length) return false;

            for(int j = 0; j < sections1.length; j++) {
                if(!sections1[j].getDepart().getNomLieu().equals(sections2[j].getDepart().getNomLieu())) return false;
                if(!sections1[j].getArrivee().getNomLieu().equals(sections2[j].getArrivee().getNomLieu())) return false;
                if(!sections1[j].getDuree().equals(sections2[j].getDuree())) return false;
                if(sections1[j].getDistance() != (sections2[j].getDistance())) return false;
            }
        }
        return true;
    }

    /**
     * Teste le calcul d'un trajet avec des stations inexistants.
     */
    @Test
    public void testNoItinerary() {
        String departString = "depart";

        Coordonnee arrivee = reseau.getStation("Boucicaut").getLocalisation();
        String arriveeString = coordonneeToString(arrivee);

        LocalTime horaire = LocalTime.of(13, 10);
        String horaireString = horaireToString(horaire);

        List<Section[]> trajetService = controller.searchItinerary(departString, arriveeString, horaireString);

        assertEquals(0, trajetService.size());
    }

    /**
     * Teste le calcul d'itinéraire par défaut.
     */
    @Test
    public void testDefaultItinerary() {
        Coordonnee depart = reseau.getStation("Nation").getLocalisation();
        String departString = coordonneeToString(depart);

        Coordonnee arrivee = reseau.getStation("Boucicaut").getLocalisation();
        String arriveeString = coordonneeToString(arrivee);

        LocalTime horaire = LocalTime.of(13, 10);
        String horaireString = horaireToString(horaire);

        List<Section[]> trajetService = controller.searchItinerary(departString, arriveeString, horaireString);
        List<Section[]> trajetCalculator = Calculator.itineraireFactory(depart, arrivee, horaire);

        assertTrue(sameTrajet(trajetCalculator, trajetService));
    }

    /**
     * Teste le calcul d'itinéraire paresseux.
     */
    @Test
    public void testLazyItinerary() {
        Coordonnee depart = reseau.getStation("Nation").getLocalisation();
        String departString = coordonneeToString(depart);

        Coordonnee arrivee = reseau.getStation("Boucicaut").getLocalisation();
        String arriveeString = coordonneeToString(arrivee);

        LocalTime horaire = LocalTime.of(13, 10);
        String horaireString = horaireToString(horaire);

        double distanceMax = 0.1;

        List<Section[]> trajetService = controller.searchLazyItinerary(departString, arriveeString, horaireString, distanceMax);
        Calculator.changeMarcherAuPlus(distanceMax);
        List<Section[]> trajetCalculator = Calculator.itineraireFactory(depart, arrivee, horaire);

        assertTrue(sameTrajet(trajetCalculator, trajetService));
    }

    /**
     * Teste le calcul d'itinéraire à pied.
     */
    @Test
    public void testWalkingItinerary() {
        Coordonnee depart = reseau.getStation("Nation").getLocalisation();
        String departString = coordonneeToString(depart);

        Coordonnee arrivee = reseau.getStation("Boucicaut").getLocalisation();
        String arriveeString = coordonneeToString(arrivee);

        LocalTime horaire = LocalTime.of(13, 10);
        String horaireString = horaireToString(horaire);

        List<Section[]> trajetService = controller.searchFullSportItinerary(departString, arriveeString, horaireString);
        Calculator.changeAPied();
        List<Section[]> trajetCalculator = Calculator.itineraireFactory(depart, arrivee, horaire);

        assertTrue(sameTrajet(trajetCalculator, trajetService));
    }

    /**
     * Teste le calcul d'itinéraire sportif avec la distance.
     */
    @Test
    public void testSportifItineraryDistance() {
        Coordonnee depart = reseau.getStation("Nation").getLocalisation();
        String departString = coordonneeToString(depart);

        Coordonnee arrivee = reseau.getStation("Boucicaut").getLocalisation();
        String arriveeString = coordonneeToString(arrivee);

        LocalTime horaire = LocalTime.of(13, 10);
        String horaireString = horaireToString(horaire);

        double distanceMin = 0.1;

        List<Section[]> trajetService = controller.searchItineraryWithMinWalkingDistance(departString, arriveeString, horaireString, distanceMin);
        Calculator.changeMarcherAuMoinsDistance(distanceMin);
        List<Section[]> trajetCalculator = Calculator.itineraireFactory(depart, arrivee, horaire);
        assertTrue(sameTrajet(trajetCalculator, trajetService));
    }

    /**
     * Teste le calcul d'itinéraire sportif avec la durée.
     */
    @Test
    public void testSportifItineraryTime() {
        Coordonnee depart = reseau.getStation("Nation").getLocalisation();
        String departString = coordonneeToString(depart);

        Coordonnee arrivee = reseau.getStation("Boucicaut").getLocalisation();
        String arriveeString = coordonneeToString(arrivee);

        LocalTime horaire = LocalTime.of(13, 10);
        String horaireString = horaireToString(horaire);

        int minutes = 5;
        Duration duree = Duration.ofMinutes(minutes);

        List<Section[]> trajetService = controller.searchItineraryWithMinWalkingDuration(departString, arriveeString, horaireString, minutes);
        Calculator.changeMarcherAuMoinsTemps(duree);
        List<Section[]> trajetCalculator = Calculator.itineraireFactory(depart, arrivee, horaire);
        assertTrue(sameTrajet(trajetCalculator, trajetService));
    }

    /**
     * Teste les résultats d'autocomplétion des stations dans la barre de saisie.
     */
    @Test
    public void testsAutoCompleteSuggestions() {
        List<String> suggestions1 = controller.getAutocompleteSuggestions("gare d");
        assertNotNull(suggestions1);
        assertEquals(4, suggestions1.size()); //Gare de l'Est, Gare d'Austerlitz, Gare de Lyon, Gare du Nord

        List<String> suggestions2 = controller.getAutocompleteSuggestions("Gare d");
        assertNotNull(suggestions2);
        assertEquals(0, suggestions2.size());

        List<String> suggestions3 = controller.getAutocompleteSuggestions("azerty");
        assertNotNull(suggestions3);
        assertEquals(0, suggestions3.size());
    }

    /**
     * Teste l'obtention des horaires de passages des trains.
     */
    @Test
    public void testsGetStationschedules() {
        Map<String, List<LocalTime>> schedules1 = controller.getStationSchedules("Nation");
        assertEquals(7, schedules1.keySet().size());

        Map<String, List<LocalTime>> schedules2 = controller.getStationSchedules("azerty");
        assertEquals(0, schedules2.keySet().size());
    }
}