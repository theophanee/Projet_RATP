package fr.uparis.backapp.model.section;

import fr.uparis.backapp.model.Coordonnee;
import fr.uparis.backapp.model.lieu.Lieu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testeur de la classe Section.
 */
public class TestSection {
    private Section section;
    final private Lieu lieu1 = new Lieu("Lieu 1", new Coordonnee(0, 0));
    final private Lieu lieu2 = new Lieu("Lieu 2", new Coordonnee(1, 1));
    final private Duration duree = Duration.of(5, ChronoUnit.SECONDS);
    final private double distance = 1.0;

    /**
     * Réinitialisation des attributs avant tous les tests.
     */
    @BeforeEach
    public void setUp() {
        section = new Section(lieu1, lieu2, duree, distance);
    }

    /**
     * Teste le constructeur de Section.
     */
    @Test
    public void testsConstructor() {
        assertNotNull(section);
        assertThrows(IllegalArgumentException.class, () -> new Section(lieu1, lieu1, duree, distance));
    }

    /**
     * Teste le Getter sur le lieu de départ.
     */
    @Test
    public void testGetDepart() {
        assertEquals(lieu1, section.getDepart());
    }

    /**
     * Teste le Getter sur le lieu d'arrivée.
     */
    @Test
    public void testGetArrivee() {
        assertEquals(lieu2, section.getArrivee());
    }

    /**
     * Teste le Getter sur la durée d'une Section.
     */
    @Test
    public void testGetDuree() {
        assertEquals(Duration.of(5, ChronoUnit.SECONDS), section.getDuree());
    }

    /**
     * Teste le Getter sur la distance d'une Section.
     */
    @Test
    public void testGetDistance() {
        assertEquals(1.0, section.getDistance());
    }

    /**
     * Teste la copie d'une section.
     */
    @Test
    public void testsCopy() {
        Section copie = section.copy();
        assertNotEquals(section, copie);
        assertNotEquals(section.depart, copie.depart);
        assertNotEquals(section.arrivee, copie.arrivee);
        assertEquals(section.duree, copie.duree);
        assertEquals(section.distance, copie.distance);
    }
}