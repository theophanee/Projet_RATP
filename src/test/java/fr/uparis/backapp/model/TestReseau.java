package fr.uparis.backapp.model;

import fr.uparis.backapp.model.lieu.Station;
import fr.uparis.backapp.model.section.SectionTransport;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testeur de la classe Reseau.
 */
public class TestReseau {
    final private Reseau reseau = Reseau.getInstance();
    final private int NB_STATIONS = 308; //tri sur excel par nom de station (sans prise en compte des coordonnées)
                                         //et comparaison avec la formule =IF(A2=A1;C1;C1+1)
    final private int NB_SECTIONS = 1770; //nombre de lignes du fichier excel fourni

    /**
     * Teste le Getter de l'instance de Reseau.
     */
    @Test
    void testGetInstance() {
        Reseau reseau1 = Reseau.getInstance();
        assertSame(reseau, reseau1);
    }

    /**
     * Teste sur les Station Station de Reseau : get, ajout et suppression.
     */
    @Test
    void testsStations() {
        Set<Station> stations = reseau.getStations();
        assertEquals(NB_STATIONS, stations.size());


        //cas d'ajout et suppression simples
        Station station = new Station("station", new Coordonnee(1, 0));

        reseau.removeStation(station);
        assertEquals(NB_STATIONS, stations.size());
        assertNull(reseau.getStation("station"));
        assertNull(reseau.getStation(new Coordonnee(1, 0)));

        reseau.addStation(station);
        reseau.addStation(station); //doublon
        assertEquals(NB_STATIONS + 1, stations.size());
        assertEquals(station, reseau.getStation("station"));
        assertEquals(station, reseau.getStation(new Coordonnee(1, 0)));

        reseau.removeStation(station);
        assertEquals(NB_STATIONS, stations.size());


        //Cas où supprimer une station supprime aussi sections et stations en cascade
        Station station1 = new Station("station 1", new Coordonnee(1, 0));
        Station station2 = new Station("station 2", new Coordonnee(1, 0));
        SectionTransport section = new SectionTransport(station1, station2, Duration.of(5, ChronoUnit.SECONDS), 1.0, new Ligne("ligne"));
        reseau.addSection(section);
        assertEquals(NB_STATIONS + 2, stations.size());

        reseau.removeStation(station1);
        assertEquals(NB_STATIONS, stations.size());
    }

    /**
     * Teste sur les Section de Reseau : get, ajout et suppression.
     */
    @Test
    void testsSections() {
        Set<SectionTransport> sections = reseau.getSections();
        assertEquals(NB_SECTIONS, sections.size());

        Station station1 = new Station("station 1", new Coordonnee(1, 0));
        Station station2 = new Station("station 2", new Coordonnee(1, 0));
        SectionTransport section = new SectionTransport(station1, station2, Duration.of(5, ChronoUnit.SECONDS), 1.0, new Ligne("ligne"));

        reseau.removeSection(section);
        assertEquals(NB_SECTIONS, sections.size());

        reseau.addSection(section);
        reseau.addSection(section); //doublon
        assertEquals(NB_SECTIONS + 1, sections.size());
        assertEquals(NB_STATIONS + 2, reseau.getStations().size());

        reseau.removeSection(section);
        assertEquals(NB_SECTIONS, sections.size());
        assertEquals(NB_STATIONS, reseau.getStations().size());
    }
}
