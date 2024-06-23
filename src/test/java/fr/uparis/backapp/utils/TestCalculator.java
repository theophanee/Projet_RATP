package fr.uparis.backapp.utils;

import fr.uparis.backapp.model.Coordonnee;
import fr.uparis.backapp.model.Reseau;
import fr.uparis.backapp.model.lieu.Station;
import fr.uparis.backapp.model.section.Section;
import fr.uparis.backapp.utils.constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testeur des calculs d'itinéraires.
 */
public class TestCalculator {
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
     * Teste le calcul d'itinéraire à pied.
     */
    @Test
    void testsWalkingItineraire() {
        LocalTime horaireDepart = LocalTime.of(23, 58, 59, 0);
        Coordonnee depart = reseau.getStation("Danube").getLocalisation();
        Coordonnee arrivee1 = reseau.getStation("Botzaris").getLocalisation();
        Coordonnee arrivee2 = reseau.getStation("Buttes Chaumont").getLocalisation();
        Coordonnee arrivee3 = reseau.getStation("Stalingrad").getLocalisation();

        Calculator.changeAPied();
        List<Section[]> trajetsTrouves1 = Calculator.itineraireFactory(depart, arrivee1, horaireDepart);
        assertNotNull(trajetsTrouves1);
        assertEquals(1, trajetsTrouves1.size());
        assertEquals(1, trajetsTrouves1.get(0).length);

        Calculator.changeAPied();
        List<Section[]> trajetsTrouves2 = Calculator.itineraireFactory(depart, arrivee2, horaireDepart);
        assertEquals(1, trajetsTrouves2.size());
        assertEquals(1, trajetsTrouves2.get(0).length);

        Calculator.changeAPied();
        List<Section[]> trajetsTrouves3 = Calculator.itineraireFactory(depart, arrivee3, horaireDepart);
        assertEquals(1, trajetsTrouves3.size());
        assertEquals(1, trajetsTrouves3.get(0).length);
    }

    /**
     * Teste l'égalité des trajets en mode sportif.
     */
    @Test
    public void testsSameSportifItineraire() {
        LocalTime horaireDepart = LocalTime.of(12, 28, 59, 0);
        Coordonnee depart = reseau.getStation("Nation").getLocalisation();
        Coordonnee arrivee = reseau.getStation("Boucicaut").getLocalisation();

        //Trajets avec au moins 10 minutes de marche
        Calculator.changeMarcherAuMoinsDistance(Constants.DEFAULT_ECART_DISTANCE);
        List<Section[]> trajetsTrouves0 = Calculator.itineraireFactory(depart, arrivee, horaireDepart);
        assertNotNull(trajetsTrouves0);
        assertTrue(trajetsTrouves0.size() <= Constants.MAX_TRAJETS_NUMBER);

        Calculator.changeMarcherAuMoinsTemps(Duration.ofMinutes(10));
        List<Section[]> trajetsTrouves1 = Calculator.itineraireFactory(depart, arrivee, horaireDepart);
        assertNotNull(trajetsTrouves1);
        assertTrue(sameTrajet(trajetsTrouves0, trajetsTrouves1));
    }

    /**
     * Teste le calcul d'itinéraire, qui donne des trajets en mode sportif, en vérifiant les minimums parcourus.
     */
    @Test
    public void testsItineraireModeSportif() {
        LocalTime horaireDepart = LocalTime.of(12, 28, 59, 0);
        Station depart = reseau.getStation("Lourmel");
        Station arrivee = reseau.getStation("Mairie d'Issy");

        Calculator.changeMarcherAuMoinsDistance(0.91);
        List<Section[]> trajetsTrouves1 = Calculator.itineraireFactory(depart.getLocalisation(), arrivee.getLocalisation(), horaireDepart);
        assertNotNull(trajetsTrouves1);
        assertTrue(trajetsTrouves1.size() <= Constants.MAX_TRAJETS_NUMBER);

        Calculator.changeMarcherAuMoinsDistance(1.4);
        List<Section[]> trajetsTrouves2 = Calculator.itineraireFactory(depart.getLocalisation(), arrivee.getLocalisation(), horaireDepart);
        assertNotNull(trajetsTrouves2);
        assertTrue(trajetsTrouves2.size() <= Constants.MAX_TRAJETS_NUMBER);

        Section[] sections1=trajetsTrouves1.get(0);
        Section[] sections2=trajetsTrouves2.get(0);
        assertTrue(sections1[sections1.length - 1].getArrivee().getHoraireDePassage()
                .isBefore(sections2[sections2.length - 1].getArrivee().getHoraireDePassage()));
    }

    /**
     * Teste l'égalité des trajets en mode paresseux, avec le mode sportif dans les cas extrêmes.
     */
    @Test
    public void testsSameLazyItineraire() {
        LocalTime horaireDepart = LocalTime.of(12, 28, 59, 0);
        Coordonnee depart = reseau.getStation("Nation").getLocalisation();
        Coordonnee arrivee = reseau.getStation("Boucicaut").getLocalisation();

        //Trajet par défaut
        List<Section[]> trajetsTrouves0 = Calculator.itineraireFactory(depart, arrivee, horaireDepart);
        assertNotNull(trajetsTrouves0);
        assertTrue(trajetsTrouves0.size() <= Constants.MAX_TRAJETS_NUMBER);

        //Trajet avec au plus 0 minute de marche entre les stations
        Calculator.changeMarcherAuPlus(Constants.DEFAULT_MIN_DISTANCE);
        List<Section[]> trajetsTrouves1 = Calculator.itineraireFactory(depart, arrivee, horaireDepart);
        assertNotNull(trajetsTrouves1);
        assertTrue(sameTrajet(trajetsTrouves0, trajetsTrouves1));
    }

    /**
     * Teste des trajets qui ne renvoient pas d'itinéraire avec dijkstra à cause de l'horaire des trains.
     */
    @Test
    public void testsNoItineraire() {
        LocalTime horaireDepart = LocalTime.of(23, 58, 59, 0);
        Coordonnee depart = reseau.getStation("Lourmel").getLocalisation();
        Coordonnee arrivee = reseau.getStation("Boucicaut").getLocalisation();

        //Trajet par défaut
        List<Section[]> trajetsTrouves0 = Calculator.itineraireFactory(depart, arrivee, horaireDepart);
        assertNotNull(trajetsTrouves0);
        assertEquals(1, trajetsTrouves0.size());

        //Trajet lazy
        Calculator.changeMarcherAuPlus(Constants.DEFAULT_MIN_DISTANCE);
        List<Section[]> trajetsTrouves1 = Calculator.itineraireFactory(depart, arrivee, horaireDepart);
        assertNotNull(trajetsTrouves1);
        assertEquals(1, trajetsTrouves1.size());

        //Trajet sportif
        Calculator.changeMarcherAuMoinsDistance(1.0);
        List<Section[]> trajetsTrouves2 = Calculator.itineraireFactory(depart, arrivee, horaireDepart);
        assertNotNull(trajetsTrouves2);
        assertEquals(1, trajetsTrouves2.size());
    }

    /**
     * Teste la concurrence de la recherche d'itinéraire.
     */
    @Test
    public void testsConsecutiveCall() {
        LocalTime horaireDepart = LocalTime.of(12, 28, 59, 0);
        Coordonnee depart = reseau.getStation("Nation").getLocalisation();
        Coordonnee arrivee = reseau.getStation("Boucicaut").getLocalisation();

        new Thread(() -> assertNotNull(Calculator.itineraireFactory(depart, arrivee, horaireDepart))).start();
        new Thread(() -> assertNull(Calculator.itineraireFactory(depart, arrivee, horaireDepart))).start();
    }
}