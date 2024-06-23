package fr.uparis.backapp.model.lieu;

import fr.uparis.backapp.model.Coordonnee;
import fr.uparis.backapp.model.Ligne;
import fr.uparis.backapp.model.section.SectionTransport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testeur de la classe Station.
 */
public class TestStation {
    private Station station;

    /**
     * Réinitialisation des attributs avant chaque test.
     * Utilisation des deux constructeurs possibles de Station.
     */
    @BeforeEach
    public void setUp() {
        station = new Station("nomStation", new Coordonnee(1, 2));
    }

    /**
     * Tests sur les localisations de la station.
     */
    @Test
    public void testsLocalisation() {
        Coordonnee coordonnee11 = new Coordonnee(1, 1);
        Coordonnee coordonnee12 = new Coordonnee(1, 2);
        String nomLigne = "ligne 1";

        assertEquals(coordonnee12, station.getLocalisation());

        station.addLocalisation(nomLigne, coordonnee12); //pas ajouté, même localisation
        station.addLocalisation(nomLigne, coordonnee11); //ajouté
        assertEquals(coordonnee11, station.getLocalisation(nomLigne));

        station.removeLocalisation("rien");
        assertEquals(coordonnee11, station.getLocalisation(nomLigne));
        station.removeLocalisation(nomLigne);
        assertEquals(coordonnee12, station.getLocalisation(nomLigne));
    }

    /**
     * Tests sur les correspondances : get, ajout et suppression.
     */
    @Test
    public void testsCorrespondances() {
        Duration duree = Duration.of(5, ChronoUnit.SECONDS);
        double distance = 10.5;
        Ligne ligne = new Ligne("ligne");

        //sections qui vont effectivement être ajoutées
        Station station1 = new Station("station1", new Coordonnee(1.2, 1.2));
        SectionTransport sectionTransport1 = new SectionTransport(station, station1, duree, distance, ligne);

        //section non ajoutée, car ne concerne pas station (station de départ différent)
        Station station2 = new Station("station2", new Coordonnee(1.2, 2.2));
        SectionTransport sectionTransport2 = new SectionTransport(station1, station, duree, distance, ligne);
        SectionTransport sectionTransport3 = new SectionTransport(station1, station2, duree, distance, ligne);


        assertEquals(0, station.getCorrespondances().size());

        station.removeCorrespondance(sectionTransport1);
        assertEquals(0, station.getCorrespondances().size());

        station.addCorrespondance(sectionTransport1);
        station.addCorrespondance(sectionTransport1); //doublon qui ne va pas être ajouté
        station.addCorrespondance(sectionTransport2); //station de départ ne correspond pas
        station.addCorrespondance(sectionTransport3); //station de départ ne correspond pas
        assertEquals(1, station.getCorrespondances().size());
        assertTrue(station.getCorrespondances().contains(sectionTransport1));

        station.removeCorrespondance(sectionTransport1);
        assertEquals(0, station.getCorrespondances().size());
        assertFalse(station.getCorrespondances().contains(sectionTransport1));
    }

    /**
     * Tests d'égalité.
     */
    @Test
    public void testsEquals() {
        Station station1 = new Station("nomStation", new Coordonnee(1, 2));
        assertEquals(station, station1);
        assertEquals(station.hashCode(), station1.hashCode());

        Station station2 = new Station("nomStation", new Coordonnee(1, 4));
        assertEquals(station, station2);
        assertEquals(station.hashCode(), station2.hashCode());

        Station station3 = new Station("nomStation3", new Coordonnee(1, 2));
        assertNotEquals(station, station3);
        assertNotEquals(station.hashCode(), station3.hashCode());
    }

    /**
     * Teste l'affichage d'une Station.
     */
    @Test
    public void testsToString() {
        Duration duree = Duration.of(5, ChronoUnit.SECONDS);
        double distance = 10.5;

        Ligne ligne1 = new Ligne("ligne1");
        Station station1 = new Station("station1", new Coordonnee(1.2, 1.2));
        SectionTransport sectionTransport1 = new SectionTransport(station, station1, duree, distance, ligne1);
        station.addCorrespondance(sectionTransport1);
        assertEquals("nomStation -> ligne1 (latitude = 1.0 ; longitude = 2.0) ", station.toString());

        Ligne ligne2 = new Ligne("ligne2");
        Station station2 = new Station("station2", new Coordonnee(1.2, 2.2));
        SectionTransport sectionTransport2 = new SectionTransport(station, station2, duree, distance, ligne2);
        station.addCorrespondance(sectionTransport2);
        station.addLocalisation("ligne2", new Coordonnee(1.2, 2.5)); //ajouté
        assertEquals("nomStation -> ligne1 (latitude = 1.0 ; longitude = 2.0) ligne2 (latitude = 1.2 ; longitude = 2.5) ", station.toString());
    }
}