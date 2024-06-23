package fr.uparis.backapp.utils;

import fr.uparis.backapp.model.lieu.Station;
import fr.uparis.backapp.model.section.SectionTransport;
import fr.uparis.backapp.utils.constants.Constants;
import fr.uparis.backapp.config.Config;
import fr.uparis.backapp.model.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static fr.uparis.backapp.utils.constants.Constants.*;
import static fr.uparis.backapp.utils.Utils.*;

/**
 * Singleton pour parser les données du client et pour créer nos Objets.
 */
public class Parser {
    private static Parser instance = null;
    final private Map<String, Ligne> lignes;
    final private Map<String, Station> stations;
    final private Set<SectionTransport> sections;

    /**
     * Constructeur privé pour créer une instance de la classe Parser.
     * Initialise les structures de données carte et sectionsSet.
     */
    private Parser() {
        lignes = new LinkedHashMap<>();
        stations = new HashMap<>();
        sections = new LinkedHashSet<>();
    }

    /**
     * Renvoie l'instance unique de la classe Parser.
     * Si l'instance n'existe pas encore, elle est créée et la méthode parse() est appelée pour la remplir.
     *
     * @return l'instance unique de la classe Parser.
     */
    public static Parser getInstance() {
        if (instance == null) {
            instance = new Parser();
            instance.parseMap();
            instance.parseTime();
        }
        return instance;
    }

    /**
     * Renvoie les lignes du réseau.
     *
     * @return les lignes du réseau.
     */
    public Ligne[] getLignes() {
        Ligne[] lignes = new Ligne[this.lignes.size()];
        return this.lignes.values().toArray(lignes);
    }

    /**
     * Renvoie les sections du réseau.
     *
     * @return les sections du réseau.
     */
    public Set<SectionTransport> getSections() {
        return sections;
    }

    /**
     * Renvoie les stations du réseau.
     *
     * @return les stations du réseau.
     */
    public Station[] getStations() {
        List<Station> stations = this.stations.values().stream().toList();
        Station[] res = new Station[stations.size()];
        for (int i = 0; i < stations.size(); i++) res[i] = stations.get(i);
        return res;
    }

    /**
     * Récupère les lignes d'un fichier CSV et les retourne sous forme de liste de tableaux de chaînes de caractères.
     *
     * @param filePath le chemin d'accès complet du fichier CSV à lire.
     * @return une liste de tableaux de chaînes de caractères, chaque tableau représentant une ligne du fichier CSV.
     */
    private static List<String[]> getFileLines(String filePath) {
        List<String[]> lines = new ArrayList<>();

        try {
            InputStream ins = Parser.class.getClassLoader().getResourceAsStream(filePath);
            lines = IOUtils.readLines(ins, "UTF-8")
                    .stream()
                    .map(line -> line.split(Constants.DELIMITER))
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * Analyse le fichier de données de la carte et construit les objets Java correspondants.
     * La méthode lit le fichier de données de la carte, récupère chaque ligne et crée les objets Java suivants :
     * les stations de départ et d'arrivée, la ligne, la durée de la section, et la section elle-même.
     * Les objets Java créés sont ensuite stockés dans des ensembles et des cartes pour une récupération rapide ultérieure.
     * Les coordonnées géographiques de chaque station sont également extraites à partir du fichier de données et stockées
     * dans les objets Station correspondants.
     */
    private void parseMap() {
        Config config = Config.getInstance();
        List<String[]> lines = getFileLines(config.getProperty(Constants.MAP_DATA_FILE_PATH_PROPERTY));

        lines.forEach(l -> {
            Station stationDepart = stations.getOrDefault(l[Constants.STATION_DEPART_INDEX],
                    new Station(l[Constants.STATION_DEPART_INDEX], new Coordonnee(l[Constants.STATION_DEPART_COORDONEES_INDEX])));
            Station stationArrivee = stations.getOrDefault(l[Constants.STATION_ARRIVEE_INDEX],
                    new Station(l[Constants.STATION_ARRIVEE_INDEX], new Coordonnee(l[Constants.STATION_ARRIVEE_COORDONEES_INDEX])));
            stationDepart.addLocalisation(l[NOM_LIGNE_INDEX], new Coordonnee(l[STATION_DEPART_COORDONEES_INDEX]));
            stationArrivee.addLocalisation(l[NOM_LIGNE_INDEX], new Coordonnee(l[STATION_ARRIVEE_COORDONEES_INDEX]));
            stations.put(l[Constants.STATION_DEPART_INDEX], stationDepart);
            stations.put(l[Constants.STATION_ARRIVEE_INDEX], stationArrivee);

            Ligne ligne = lignes.getOrDefault(l[Constants.NOM_LIGNE_INDEX], new Ligne(l[Constants.NOM_LIGNE_INDEX]));
            ligne.addStation(stationDepart);
            ligne.addStation(stationArrivee);
            lignes.put(l[Constants.NOM_LIGNE_INDEX], ligne);

            Duration duree = correctDuration(l[Constants.DUREE_INDEX]);
            double distance = correctDistance(l[Constants.DISTANCE_INDEX]);

            SectionTransport section = new SectionTransport(stationDepart, stationArrivee, duree, distance, ligne);
            sections.add(section);

            stationDepart.addCorrespondance(section);
            //On ajoute le trajet inverse
            stationArrivee.addCorrespondance(new SectionTransport(stationArrivee, stationDepart, duree, distance, ligne));
        });
    }

    /**
     * Lecture des horaires.
     * Ajoute les horaires de passage d’un transport à une Station donnée.
     */
    private void parseTime() {
        Config config = Config.getInstance();
        List<String[]> fileLines = getFileLines(config.getProperty(SCHEDULES_FILE_PATH_PROPERTY));
        Map<String, Map<String, List<LocalTime>>> passagePerVariant = new HashMap<>();
        fileLines.forEach(l -> {
            String lineVariant = l[SCHEDULES_FILE_LINE_INDEX] + " variant " + l[SCHEDULES_FILE_VARIANTE_INDEX];
            Map<String, List<LocalTime>> lineDetail = passagePerVariant.getOrDefault(lineVariant, new HashMap<>());
            String terminal = l[SCHEDULES_FILE_TERMINUS_INDEX];
            List<LocalTime> departureTimeByTerminal = lineDetail.getOrDefault(terminal, new ArrayList<>());
            LocalTime time = LocalTime.parse(correctTime(l[SCHEDULES_FILE_TIME_INDEX]), DateTimeFormatter.ofPattern("HH:mm"));

            departureTimeByTerminal.add(time);
            lineDetail.put(terminal, departureTimeByTerminal);
            passagePerVariant.put(lineVariant, lineDetail);
        });

        addSchedulesToLines(passagePerVariant);//TODO discuss this one (elle peut etre enlever)
        calculate_schedules(passagePerVariant);
    }

    /**
     * Ajoute les horaires de départ de chaque ligne à la carte en fonction des horaires fournis sous forme de dictionnaire.
     *
     * @param map un dictionnaire contenant les horaires de départ pour chaque variant de ligne et chaque terminus.
     */
    private void addSchedulesToLines(Map<String, Map<String, List<LocalTime>>> map) {
        map.forEach((variant, timesByTerminal) ->
                timesByTerminal.values().forEach(times -> times.forEach(time -> lignes.get(variant).addHoraireDepart(time)))
        );
    }

    /**
     * Calcule les horaires de départ pour chaque section du réseau à partir d'une carte des horaires de passage pour chaque combinaison de variant et terminus.
     *
     * @param map une carte des horaires de passage pour chaque combinaison de variant et terminus.
     * @throws IllegalArgumentException si l'une des stations de départ n'est pas présente dans le réseau.
     */
    private void calculate_schedules(Map<String, Map<String, List<LocalTime>>> map) {
        map.forEach((variant, timesByTerminal) ->
                timesByTerminal.forEach((terminal, times) -> {
                    SectionTransport sectionDepart = findSectionDepart(terminal, variant);
                    if (sectionDepart == null)
                        throw new IllegalArgumentException("Station départ introuvable dans le réseau");
                    sectionDepart.addHorairesDepart(times);
                    propagateSchedules(sectionDepart);
                })
        );
    }

    /**
     * Récupère la section de départ correspondant à une station de départ et une variante de ligne donnée.
     *
     * @param nameStation le nom de la station de départ recherchée.
     * @param lineVariant le variant de ligne de la section de départ recherchée.
     * @return la section de départ ayant pour station de départ et variant de ligne ceux donnés, ou null si aucune section n'a été trouvée.
     */
    private SectionTransport findSectionDepart(String nameStation, String lineVariant) {
        Station stationDepart = new Station(nameStation, null);
        for (SectionTransport section : sections)
            if (section.isStationDepart(stationDepart) && section.getLigne().getNomLigne().equals(lineVariant))
                return section;
        return null;
    }

    /**
     * Calcule les horaires de départ de la section initiale et les propage à toutes les sections connectées à cette section,
     * en utilisant les durées des sections pour calculer les horaires de départ des sections suivantes,
     * et en rajoutant 40 secondes à chaque arrêt.
     *
     * @param sectionDepart la section de départ à partir de laquelle propager les horaires de départ.
     */
    private void propagateSchedules(SectionTransport sectionDepart) {
        SectionTransport currentSection = sectionDepart;
        SectionTransport nextSectionInTheSameLine = currentSection.moveToNextSectionInTheSameLine(sections);

        while (nextSectionInTheSameLine != null) {
            SectionTransport finalCurrentSection = currentSection;
            SectionTransport finalNextSectionInTheSameLine = nextSectionInTheSameLine;
            currentSection.getHorairesDepart().forEach(time ->
                    finalNextSectionInTheSameLine.addHoraireDepart(time.plus(finalCurrentSection.getDuree())
                            .plus(Duration.ofSeconds(40)))); //40 secondes d'arrêt

            currentSection = nextSectionInTheSameLine;
            nextSectionInTheSameLine = currentSection.moveToNextSectionInTheSameLine(sections);
        }
    }
}
