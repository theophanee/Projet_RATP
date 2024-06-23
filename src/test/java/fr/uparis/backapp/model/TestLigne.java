package fr.uparis.backapp.model;

import fr.uparis.backapp.model.lieu.Station;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testeur de la classe Ligne.
 */
public class TestLigne {
    final private Ligne ligne = new Ligne("L1");

    @AfterEach
    public void clear() {
        ligne.getStations().clear();
        ligne.getHorairesDepart().clear();
    }

    /**
     * Teste la construction de Ligne avec des paramètres null.
     */
    @Test
    public void testsConstructorWithNullValues() {
        Ligne ligne = new Ligne(null, null, null);
        assertNotNull(ligne);
        assertNull(ligne.getNomLigne());
        assertNull(ligne.getStations());
        assertNull(ligne.getHorairesDepart());
    }

    /**
     * Teste le Getter du nom de la Ligne.
     */
    @Test
    public void testGetNomLigne() {
        assertEquals("L1", ligne.getNomLigne());
    }

    /**
     * Teste sur les stations de la Ligne : get, ajout et suppression.
     */
    @Test
    public void testsStations() {
        Station station1 = new Station("S1", new Coordonnee(1,2));
        Station station2 = new Station("S2", new Coordonnee(2,2));

        assertEquals(0, ligne.getStations().size());

        ligne.removeStation(station1);
        assertEquals(0, ligne.getStations().size());

        ligne.addStation(station1);
        ligne.addStation(station1);
        ligne.addStation(station2);
        assertEquals(2, ligne.getStations().size());

        ligne.removeStation(station1);
        assertEquals(1, ligne.getStations().size());
        assertFalse(ligne.getStations().contains(station1));
        assertTrue(ligne.getStations().contains(station2));
    }

    /**
     * Tests sur les horaires de départ : get, ajout et suppression.
     */
    @Test
    public void testsHoraires() {
        LocalTime horaire1 = LocalTime.now();
        LocalTime horaire2 = LocalTime.of(0, 5);

        assertEquals(0, ligne.getHorairesDepart().size());

        ligne.removeHoraireDepart(horaire1);
        assertEquals(0, ligne.getHorairesDepart().size());

        ligne.addHoraireDepart(horaire1);
        ligne.addHoraireDepart(horaire2);
        assertEquals(2, ligne.getHorairesDepart().size());

        ligne.removeHoraireDepart(horaire1);
        assertEquals(1, ligne.getHorairesDepart().size());
        assertFalse(ligne.getHorairesDepart().contains(horaire1));
        assertTrue(ligne.getHorairesDepart().contains(horaire2));
    }

    /**
     * Teste le renvoie de terminus d'une ligne.
     */
    @Test
    public void testsGetDirection() {
        Station station1 = new Station("station1", new Coordonnee("1,1"));
        Station station2 = new Station("station2", new Coordonnee("1,1"));
        Station station3 = new Station("station3", new Coordonnee("1,1"));
        ligne.addStation(station1);
        ligne.addStation(station2);
        ligne.addStation(station3);

        assertNotEquals(station1, ligne.getDirection());
        assertNotEquals(station2, ligne.getDirection());
        assertEquals(station3, ligne.getDirection());
    }

    /**
     * Tests d'égalité.
     */
    @Test
    public void testsEquals() {
        Ligne ligne1 = new Ligne("L1");
        assertEquals(ligne, ligne1);
        assertEquals(ligne.hashCode(), ligne1.hashCode());

        Ligne ligne2 = new Ligne("L2");
        assertNotEquals(ligne, ligne2);
        assertNotEquals(ligne.hashCode(), ligne2.hashCode());
    }

    /**
     * Teste l'affichage d'une Section.
     */
    @Test
    public void testsToString() {
        assertEquals("L1 : ", ligne.toString());

        Station station1 = new Station("S1", new Coordonnee(1,2));
        Station station2 = new Station("S2", new Coordonnee(1,2));
        ligne.addStation(station1);
        ligne.addStation(station2);

        LocalTime horaire = LocalTime.of(15, 0);
        ligne.addHoraireDepart(horaire);

        assertEquals("L1 : S1 S2 \n    15:00", ligne.toString());
    }
}