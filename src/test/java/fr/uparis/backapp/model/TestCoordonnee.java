package fr.uparis.backapp.model;

import org.junit.jupiter.api.Test;

import static fr.uparis.backapp.model.Coordonnee.isCoordinate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testeur de la classe Coordonnee.
 */
public class TestCoordonnee {
    /**
     * Teste la construction de la classe à partir de deux doubles.
     * Teste les getters de latitude et longitude, en degrés et en radians.
     */
    @Test
    public void testsConstructorDoubleAndGetters() {
        Coordonnee coordonnee = new Coordonnee(48.8566, 2.3522);
        assertEquals(48.8566, coordonnee.getLatitude(), 0.0);
        assertEquals(2.3522, coordonnee.getLongitude(), 0.0);

        double coeff = Math.PI / 180;
        assertEquals(coeff * 48.8566, coordonnee.getLatitudeRadian(), 0.0);
        assertEquals(coeff * 2.3522, coordonnee.getLongitudeRadian(), 0.0);
    }

    /**
     * Teste la construction de la classe à partir d'une chaîne de caractères.
     */
    @Test
    public void testsConstructorString() {
        Coordonnee coordonnee = new Coordonnee("2.3522,48.8566");
        assertEquals(48.8566, coordonnee.getLatitude(), 0.0);
        assertEquals(2.3522, coordonnee.getLongitude(), 0.0);

        assertThrows(IllegalArgumentException.class, () -> new Coordonnee("48.8566;2.3522"));
        assertThrows(NumberFormatException.class, () -> new Coordonnee("48.8566,e2.3522"));
    }

    /**
     * Teste si une chaîne de caractères est une coordonnée valide.
     */
    @Test
    public void testsIsCoordinate() {
        assertTrue(isCoordinate("2.3522,48.8566"));
        assertFalse(isCoordinate("48.8566;2.3522"));
        assertFalse(isCoordinate("48.8566,e2.3522"));
    }

    /**
     * Teste l'égalité entre deux Coordonnee.
     */
    @Test
    public void testsEquals() {
        Coordonnee coordonnee1 = new Coordonnee(48.8566, 2.3522);
        assertNotEquals(null, coordonnee1);
        assertNotEquals("48.8566, 2.3522", coordonnee1);

        Coordonnee coordonnee2 = new Coordonnee(48.8566, 2.3522);
        assertEquals(coordonnee1, coordonnee2);
        assertEquals(coordonnee1.hashCode(), coordonnee2.hashCode());

        Coordonnee coordonnee3 = new Coordonnee(45.75, 4.85);
        assertNotEquals(coordonnee1, coordonnee3);
        assertNotEquals(coordonnee1.hashCode(), coordonnee3.hashCode());
    }

    /**
     * Teste l'affichage de Coordonnee.
     */
    @Test
    public void testToString() {
        Coordonnee coordonnee = new Coordonnee(48.8566, 2.3522);
        assertEquals("latitude = 48.8566 ; longitude = 2.3522", coordonnee.toString());
    }
}
