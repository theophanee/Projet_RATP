package fr.uparis.backapp.model.lieu;

import fr.uparis.backapp.model.Coordonnee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testeur de la classe Lieu.
 */
public class TestLieu {
    private Lieu lieu;

    /**
     * Réinitialisation des attributs avant chaque test.
     * Utilisation des deux constructeurs possibles de Station.
     */
    @BeforeEach
    public void setUp() {
        lieu = new Lieu("nomLieu", new Coordonnee(1, 2));
    }

    /**
     * Test sur le nom de la station.
     */
    @Test
    public void testGetNomStation() {
        assertEquals("nomLieu", lieu.getNomLieu());
    }

    /**
     * Tests sur les localisations de la station.
     */
    @Test
    public void testsLocalisation() {
        assertEquals(new Coordonnee(1, 2), lieu.getLocalisation());
        assertNotEquals(new Coordonnee(1, 1), lieu.getLocalisation());
    }

    /**
     * Tests sur les horaires de passage.
     */
    @Test
    public void testsHoraireDePassage() {
        LocalTime time = LocalTime.of(4, 5);

        assertNull(lieu.getHoraireDePassage());

        lieu.setHoraireDePassage(time);
        assertEquals(time, lieu.getHoraireDePassage());
    }

    /**
     * Teste la copie d'un lieu.
     */
    @Test
    public void testsCopy() {
        Lieu copie = lieu.copy();
        assertNotEquals(lieu, copie);
        assertEquals(lieu.nomLieu, copie.nomLieu);
        assertEquals(lieu.localisation, copie.localisation);
        assertEquals(lieu.horaireDePassage, copie.horaireDePassage);
    }
}