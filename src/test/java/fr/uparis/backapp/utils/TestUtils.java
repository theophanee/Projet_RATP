package fr.uparis.backapp.utils;

import fr.uparis.backapp.exceptions.StationNotFoundException;
import fr.uparis.backapp.model.Coordonnee;
import fr.uparis.backapp.model.Ligne;
import fr.uparis.backapp.model.Reseau;
import fr.uparis.backapp.model.lieu.Station;
import fr.uparis.backapp.model.section.SectionTransport;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.uparis.backapp.utils.Utils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testeur de la classe Utils.
 */
public class TestUtils {
    /**
     * Teste l'arrondi à une précision de 3 chiffres après la virgule.
     */
    @Test
    public void tests3Precision() {
        assertEquals(3.333, truncateDoubleTo3Precision(3.333));
        assertEquals(3.333, truncateDoubleTo3Precision(3.33333));
        assertEquals(3.333, truncateDoubleTo3Precision(3.3334));
        assertEquals(3.334, truncateDoubleTo3Precision(3.33351));
    }

    /**
     * Teste la conversion d'une distance en dizaine de km vers km.
     */
    @Test
    public void testsCorrectDistance() {
        String distance1 = "9.552567634725916";
        assertEquals(Double.parseDouble("0.955"), correctDistance(distance1));

        String distance2 = "16.242977461710105";
        assertEquals(Double.parseDouble("1.624"), correctDistance(distance2));

        String distance3 = "27.99616623787354";
        assertEquals(Double.parseDouble("2.8"), correctDistance(distance3));
    }

    /**
     * Teste la conversion de durée en dizaine de secondes vers Duration.
     */
    @Test
    public void testsCorrectDuration() {
        String duration1 = "10:02";
        assertEquals("PT1M41S", correctDuration(duration1).toString());

        String duration2 = "02:35";
        assertEquals("PT24S", correctDuration(duration2).toString());

        String duration3 = "12:45";
        assertEquals("PT2M5S", correctDuration(duration3).toString());
    }

    /**
     * Teste la conversion d'un horaire vers le format "hh:mm".
     */
    @Test
    public void testsCorrectTime() {
        String time1 = "5:8";
        String expected1 = "05:08";
        assertEquals(expected1, correctTime(time1));

        String time2 = "12:45";
        String expected2 = "12:45";
        assertEquals(expected2, correctTime(time2));

        String time3 = "6:7";
        String expected3 = "06:07";
        assertEquals(expected3, correctTime(time3));
    }

    /**
     * Teste le calcul de distance entre deux coordonnées.
     */
    @Test
    void testsDistanceBetween() {
        Coordonnee origine1 = new Coordonnee(52.2296756, 21.0122287);
        Coordonnee destination1 = new Coordonnee(52.2296756, 21.0122287);
        assertEquals(0.000, distanceBetween(origine1, destination1));

        Coordonnee origine2 = new Coordonnee(48.948559, 2.063739);
        Coordonnee destination2 = new Coordonnee(48.94796491807184, 2.0651253924818516);
        assertEquals(0.121, distanceBetween(origine2, destination2)); //résultat depuis Google Maps

        Coordonnee origine3 = new Coordonnee(52.2296756, 21.0122287);
        Coordonnee destination3 = new Coordonnee(52.406374, 16.9251681);
        assertEquals(278.537, distanceBetween(origine3, destination3)); //résultat depuis un site de calcul
    }

    /**
     * Teste la durée de marche d'une certaine distance en km.
     */
    @Test
    void testsWalkingDuration() {
        assertEquals(Duration.ofSeconds(0), walkingDurationOf(0));
        assertEquals(Duration.ofHours(1), walkingDurationOf(5.0));
        assertEquals(Duration.ofMinutes(30), walkingDurationOf(2.5));
        assertEquals(Duration.ofMinutes(6), walkingDurationOf(0.5));
        assertEquals(Duration.ofMinutes(3), walkingDurationOf(0.25));
        assertEquals(Duration.ofMinutes(1).plusSeconds(12), walkingDurationOf(0.1));
    }

    /**
     * Teste la distance de marche d'une certaine durée.
     */
    @Test
    void testsDistanceOfWalkingDuration() {
        assertEquals(0, distanceOfWalkingDuration(Duration.ofSeconds(-5)));
        assertEquals(0.91, distanceOfWalkingDuration(Duration.ofSeconds(655)));
        assertEquals(0.919, distanceOfWalkingDuration(Duration.ofSeconds(662)));
    }

    /**
     * Teste l'obtention des horaires de passage des trains.
     */
    @Test
    public void testGetSchedulesByLine() {
        Station station = new Station("station", new Coordonnee("1,1"));
        Station station1 = new Station("station1", new Coordonnee("1,1"));
        Station station2 = new Station("station2", new Coordonnee("1,1"));


        Set<Station> stations1 = new HashSet<>();
        stations1.add(station);
        stations1.add(station1);
        Ligne ligne1 = new Ligne("ligne1", stations1, new HashSet<>());

        Set<Station> stations2 = new HashSet<>();
        stations2.add(station);
        stations2.add(station2);
        Ligne ligne2 = new Ligne("ligne2", stations2, new HashSet<>());


        SectionTransport section1 = new SectionTransport(station, station1, Duration.ofSeconds(50), 1.5, ligne1);
        section1.addHoraireDepart(LocalTime.of(12, 10));
        section1.addHoraireDepart(LocalTime.of(12, 15));
        station.addCorrespondance(section1);

        SectionTransport section2 = new SectionTransport(station, station2, Duration.ofSeconds(50), 1.5, ligne2);
        section2.addHoraireDepart(LocalTime.of(13, 27));
        section2.addHoraireDepart(LocalTime.of(13, 15));
        section2.addHoraireDepart(LocalTime.of(13, 39));
        station.addCorrespondance(section2);


        Map<String, List<LocalTime>> horaires = getSchedulesByLine(station.getCorrespondances().stream().toList());

        assertEquals(2, horaires.values().size());
        assertEquals(2, horaires.get("ligne1;station1").size());
        assertEquals(3, horaires.get("ligne2;station2").size());
        assertEquals(LocalTime.of(13, 27), horaires.get("ligne2;station2").get(1)); //trié
    }

    /**
     * Test la transformation d'une chaîne de caractères en LocalTime.
     */
    @Test
    public void testsGetTimeFromString() {
        assertEquals(LocalTime.of(5, 10), getTimeFromString("5:10"));
        assertEquals(LocalTime.of(5, 10), getTimeFromString("05:10"));
        assertEquals(LocalTime.of(0, 0), getTimeFromString("0:0"));
        assertEquals(LocalTime.of(0, 0), getTimeFromString("0:00"));
        assertEquals(LocalTime.of(0, 0), getTimeFromString("00:0"));
        assertEquals(LocalTime.of(0, 0), getTimeFromString("00:00"));
    }

    /**
     * Teste la récupération de coordonnées depuis un nom de station ou une coordonnée.
     */
    @Test
    public void testsFetchCoordinates() throws StationNotFoundException{
        assertEquals(new Coordonnee("1,1"), fetchCoordinates("1, 1"));
        assertEquals(new Coordonnee(1, 1), fetchCoordinates("1, 1"));
        assertEquals(Reseau.getInstance().getStation("Gare de Lyon").getLocalisation(), fetchCoordinates("Gare de Lyon"));
        assertThrows(StationNotFoundException.class, () -> fetchCoordinates("stationTest"));
    }
}
