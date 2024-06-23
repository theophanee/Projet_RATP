package fr.uparis.backapp.model.section;

import fr.uparis.backapp.model.Coordonnee;
import fr.uparis.backapp.model.Ligne;
import fr.uparis.backapp.model.lieu.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Testeur de la classe SectionTransport.
 */
public class TestSectionTransport {
    private SectionTransport section;
    final private Station station1 = new Station("Station 1", new Coordonnee(0, 0));
    final private Station station2 = new Station("Station 2", new Coordonnee(1, 1));
    final private Duration duree = Duration.of(5, ChronoUnit.SECONDS);
    final private double distance = 1.0;
    final private Ligne ligne = new Ligne("Ligne A");

    /**
     * Réinitialisation des attributs avant tous les tests.
     */
    @BeforeEach
    public void setUp() {
        section = new SectionTransport(station1, station2, duree, distance, ligne);
    }

    /**
     * Teste le constructeur de Section.
     */
    @Test
    public void testsConstructor() {
        assertNotNull(section);
        assertThrows(IllegalArgumentException.class, () -> new SectionTransport(station1, station1, duree, distance, ligne));
    }

    /**
     * Teste le Getter sur le lieu de départ.
     */
    @Test
    public void testGetDepart() {
        assertEquals(station1, section.getDepart());
    }

    /**
     * Teste le Getter sur le lieu d'arrivée.
     */
    @Test
    public void testGetArrivee() {
        assertEquals(station2, section.getArrivee());
    }

    /**
     * Teste le Getter de la ligne d'une Section.
     */
    @Test
    public void testGetLigne() {
        assertEquals(new Ligne("Ligne A"), section.getLigne());
    }

    /**
     * Tests sur les horaires : get, ajout et suppression.
     */
    @Test
    public void testsHoraires() {
        LocalTime horaire1 = LocalTime.of(22, 50);
        LocalTime horaire2 = LocalTime.of(0, 5);

        assertEquals(0, section.getHorairesDepart().size());

        section.removeHoraireDepart(horaire1);
        assertEquals(0, section.getHorairesDepart().size());

        section.addHoraireDepart(horaire1);
        section.addHoraireDepart(horaire1);
        section.addHoraireDepart(horaire2);
        assertEquals(2, section.getHorairesDepart().size());
        assertEquals(horaire2, section.getHoraireProchainDepart(LocalTime.of(0, 4)));
        assertNull(section.getHoraireProchainDepart(LocalTime.of(23, 59)));

        section.removeHoraireDepart(horaire1);
        assertEquals(1, section.getHorairesDepart().size());
        assertFalse(section.getHorairesDepart().contains(horaire1));
        assertTrue(section.getHorairesDepart().contains(horaire2));

        section.addHorairesDepart(List.of(horaire1, horaire1, horaire2));
        assertEquals(2, section.getHorairesDepart().size());
        assertTrue(section.getHorairesDepart().contains(horaire1));
        assertTrue(section.getHorairesDepart().contains(horaire2));
    }

    /**
     * Tests d'égalité.
     */
    @Test
    public void testsEquals() {
        SectionTransport section1 = new SectionTransport(station1, station2, duree, distance, ligne);
        assertEquals(section, section1);
        assertEquals(section.hashCode(), section1.hashCode());

        SectionTransport section2 = new SectionTransport(station2, station1, duree, distance, ligne);
        assertNotEquals(section, section2);
        assertNotEquals(section.hashCode(), section2.hashCode());
    }

    /**
     * Teste l'affichage d'une Section.
     */
    @Test
    public void testsToString() {
        assertEquals("Ligne A : Station 1 -> Station 2 (durée = PT5S, distance = 1.0 km)", section.toString());

        LocalTime horaire = LocalTime.of(15, 0);
        section.addHoraireDepart(horaire);
        assertEquals("Ligne A : Station 1 -> Station 2 (durée = PT5S, distance = 1.0 km, à 15:00)", section.toString());
    }

    /**
     * Teste si une Section va d'une station à une autre.
     */
    @Test
    public void testsAreStations() {
        assertTrue(section.areStations(station1, station2));

        Station station3 = new Station("Station 3", new Coordonnee(0, 0));
        assertFalse(section.areStations(station1, station3));
        assertFalse(section.areStations(station3, station2));
    }

    /**
     * Test le déplacement d'une section à une autre, sur la même ligne.
     */
    @Test
    public void testsMoveToNextSection() {
        Station station3 = new Station("Station3", new Coordonnee("4, 5"));
        SectionTransport section1 = new SectionTransport(station2, station3, duree, distance, ligne);
        SectionTransport section2 = new SectionTransport(station3, station2, duree, distance, ligne);

        Station station4 = new Station("Station4", new Coordonnee(4, 5));
        Ligne ligne2 = new Ligne("ligne2");
        SectionTransport section3 = new SectionTransport(station2, station4, duree, distance, ligne2);

        Set<SectionTransport> sections = new HashSet<>();
        sections.add(section);
        sections.add(section1); //même ligne
        sections.add(section2); //même ligne, mais pas la bonne station de départ
        sections.add(section3); //pas la même ligne

        SectionTransport nextSection = section.moveToNextSectionInTheSameLine(sections);
        assertEquals(section1, nextSection);
        assertNotEquals(section2, nextSection);
        assertNotEquals(section3, nextSection);

        assertEquals(section1, section2.moveToNextSectionInTheSameLine(sections));
        assertEquals(section2, section1.moveToNextSectionInTheSameLine(sections));
    }
}
