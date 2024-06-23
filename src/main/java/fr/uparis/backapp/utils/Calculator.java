package fr.uparis.backapp.utils;

import fr.uparis.backapp.model.*;
import fr.uparis.backapp.model.lieu.Lieu;
import fr.uparis.backapp.model.lieu.Station;
import fr.uparis.backapp.model.section.Section;
import fr.uparis.backapp.model.section.SectionTransport;
import fr.uparis.backapp.utils.constants.Constants;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

import static fr.uparis.backapp.utils.Utils.*;

/**
 * Classe static pour calculer des itinéraires en fonction de différents paramètres.
 */
public class Calculator {
    private static boolean isCalculating = false;

    private static boolean A_PIED = false;

    private static boolean MARCHER_AU_MOINS_DISTANCE = false;
    private static double volonte = 0.0;

    private static boolean MARCHER_AU_MOINS_TEMPS = false;
    private static double minDistance = 0.0;

    private static boolean MARCHER_AU_PLUS = false;

    private static Coordonnee depart = null;
    private static Coordonnee arrivee = null;
    private static LocalTime horaireDepart = null;

    private static HashMap<Station, List<Station>> coupleStations = null;

    public static boolean getIsCalculating() {
        return isCalculating;
    }

    /**
     * Change l'état du boolean pour avoir des trajets à pied.
     */
    public static void changeAPied() {
        A_PIED = !A_PIED;
    }

    /**
     * Change l'état du boolean pour avoir des trajets en mode sportif avec la distance en paramètre.
     *
     * @param distance la distance minimale à parcourir dans le trajet.
     */
    public static void changeMarcherAuMoinsDistance(double distance) {
        MARCHER_AU_MOINS_DISTANCE = !MARCHER_AU_MOINS_DISTANCE;
        minDistance = distance;
    }

    /**
     * Change l'état du boolean pour avoir des trajets en mode sportif avec la durée en paramètre.
     *
     * @param temps la durée minimale à parcourir dans le trajet.
     */
    public static void changeMarcherAuMoinsTemps(Duration temps) {
        MARCHER_AU_MOINS_TEMPS = !MARCHER_AU_MOINS_TEMPS;
        if(temps == null) minDistance = 0.0;
        else minDistance = distanceOfWalkingDuration(temps);
    }

    /**
     * Change l'état du boolean pour avoir des trajets en mode paresseux.
     *
     * @param distance la distance maximale à parcourir entre les stations, pour avoir des trajets plus courts.
     */
    public static void changeMarcherAuPlus(double distance) {
        MARCHER_AU_PLUS = !MARCHER_AU_PLUS;
        volonte = distance;
    }

    /**
     * Fabrique d'itinéraires qui va retourner les itinéraires avec les bons paramètres.
     *
     * @param depart point de départ du trajet.
     * @param arrivee point d'arrivée du trajet.
     * @param horaireDepart horaire de départ.
     * @return les 5 trajets les plus rapides, sous forme de liste de Sections.
     */
    public static List<Section[]> itineraireFactory(Coordonnee depart, Coordonnee arrivee, LocalTime horaireDepart) {
        if(!isCalculating) {
            isCalculating = true;
            Calculator.depart = depart;
            Calculator.arrivee = arrivee;
            Calculator.horaireDepart = horaireDepart;
            List<Section[]> res;

            if(A_PIED) {
                res = new LinkedList<>();
                res.add(new Section[]{walkingItineraire(depart, arrivee, horaireDepart)});
                changeAPied();
            }
            else {
                coupleStations = new HashMap<>();

                if(MARCHER_AU_MOINS_DISTANCE) {
                    res = sportifItineraire(minDistance);
                    changeMarcherAuMoinsDistance(0.0);
                }
                else if(MARCHER_AU_MOINS_TEMPS) {
                    res = sportifItineraire(minDistance);
                    changeMarcherAuMoinsTemps(null);
                }
                else if(MARCHER_AU_PLUS) {
                    res = lazyItineraire(volonte);
                    changeMarcherAuPlus(0.0);
                }
                else res = lazyItineraire(Constants.DEFAULT_MIN_DISTANCE);

                coupleStations = null;
            }

            isCalculating = false;
            setCorrectTime(res);
            return res;
        }
        return null;
    }

    /**
     * Modifie les horaires de départ des lieux pour prendre en compte les correspondances.
     *
     * @param trajets les trajets concernés par la modification.
     */
    private static void setCorrectTime(List<Section[]> trajets) {
        for(Section[] sections: trajets)
            for(int i = 1; i < sections.length; i++)
                sections[i].getDepart().setHoraireDePassage(sections[i].getArrivee().getHoraireDePassage().minus(sections[i].getDuree()));
    }

    /**
     * Calcule un itinéraire totalement à pied, à partir de coordonnées.
     *
     * @param depart point de départ du trajet.
     * @param arrivee point d'arrivée du trajet.
     * @param horaireDepart horaire de départ.
     * @return une section contenant toutes les informations du trajet à pied.
     */
    private static Section walkingItineraire(Coordonnee depart, Coordonnee arrivee, LocalTime horaireDepart) {
        double distance = distanceBetween(depart, arrivee);
        Duration duree = walkingDurationOf(distance);
        return new Section(new Lieu(Constants.DEPART, depart, horaireDepart),
                           new Lieu(Constants.ARRIVEE, arrivee, horaireDepart.plus(duree)),
                           duree,
                           distance);
    }

    /**
     * Calcule un itinéraire, avec correspondances et horaires, en imposant une distance de marche minimale dans le trajet.
     *
     * @param minDistance distance de marche minimale durant le trajet, en km.
     * @return les 5 trajets les plus rapides, sous forme de liste de Sections.
     */
    private static List<Section[]> sportifItineraire(double minDistance) {
        //Les 5 trajets les plus optimaux à retourner, avec celui à pied en termes de comparatif/pire trajet
        List<Section[]> trajetsSaved = new ArrayList<>();
        trajetsSaved.add(new Section[]{walkingItineraire(depart, arrivee, horaireDepart)});
        LocalTime maxTime = trajetsSaved.get(0)[0].getArrivee().getHoraireDePassage();

        double maxDistance = minDistance + Constants.DEFAULT_ECART_DISTANCE;

        //Les stations les plus proches du départ et de l'arrivée, avec un minimum de marche au début
        List<Station> procheDepartDeb = getNearStations(depart, minDistance, maxDistance);
        List<Station> procheArriveeDeb = getNearStations(arrivee, Constants.DEFAULT_MIN_DISTANCE, maxDistance);

        //Les stations les plus proches du départ et de l'arrivée, avec un minimum de marche au début et à la fin
        List<Station> procheDepartMid = getNearStations(depart, minDistance / 2, maxDistance);
        List<Station> procheArriveeMid = getNearStations(arrivee, minDistance / 2, maxDistance);

        //Les stations les plus proches du départ et de l'arrivée, avec un minimum de marche à la fin
        List<Station> procheDepartFin = getNearStations(depart, Constants.DEFAULT_MIN_DISTANCE, maxDistance);
        List<Station> procheArriveeFin = getNearStations(arrivee, minDistance, maxDistance);

        //Initialise les couples de stations déjà traités par dijkstra
        addStationsAndListToMap(procheDepartDeb);
        addStationsAndListToMap(procheDepartMid);
        addStationsAndListToMap(procheDepartFin);

        //Trouve le plus court chemin pour chaque couple procheDepart-procheArrivee
        addTrajetsOptimaux(trajetsSaved, procheDepartDeb, procheArriveeDeb, maxTime, maxDistance);
        addTrajetsOptimaux(trajetsSaved, procheDepartMid, procheArriveeMid, maxTime, maxDistance);
        addTrajetsOptimaux(trajetsSaved, procheDepartFin, procheArriveeFin, maxTime, maxDistance);

        return trajetsSaved;
    }

    /**
     * Calcule un itinéraire, avec correspondances et horaires, en fixant un périmètre maximal de recherche des stations.
     *
     * @param volonte La distance que l'utilisateur accepte de parcourir, si jamais le chemin est plus rapide à pied qu'en transport.
     * @return les 5 trajets les plus rapides, sous forme de liste de Sections.
     */
    private static List<Section[]> lazyItineraire(double volonte) {
        //Les 5 trajets les plus optimaux à retourner, avec celui à pied en termes de comparatif/pire trajet
        List<Section[]> trajetsSaved = new ArrayList<>();
        trajetsSaved.add(new Section[]{walkingItineraire(depart, arrivee, horaireDepart)});
        LocalTime maxTime = trajetsSaved.get(0)[0].getArrivee().getHoraireDePassage();

        //Trouve les stations les plus proches du départ et de l'arrivée
        double maxDistance = (volonte != Constants.DEFAULT_MIN_DISTANCE)? volonte : Constants.DEFAULT_ECART_DISTANCE;
        List<Station> procheDepart = getNearStations(depart, Constants.DEFAULT_MIN_DISTANCE, maxDistance);
        List<Station> procheArrivee = getNearStations(arrivee, Constants.DEFAULT_MIN_DISTANCE, maxDistance);

        //Initialise les couples de stations déjà traités par dijkstra
        addStationsAndListToMap(procheDepart);

        //Trouve le plus court chemin pour chaque couple procheDepart-procheArrivee
        addTrajetsOptimaux(trajetsSaved, procheDepart, procheArrivee, maxTime, volonte);

        return trajetsSaved;
    }

    /**
     * Cherche les stations proches d'une coordonnée.
     *
     * @param coordonnee coordonnée du point de départ.
     * @param minDistance distance minimale acceptable entre deux points, en km.
     * @param maxDistance distance maximale acceptable entre deux points, en km.
     * @return la liste des stations dont la distance est majorée par maxDistance et minorée par minDistance.
     */
    private static List<Station> getNearStations(Coordonnee coordonnee, double minDistance, double maxDistance) {
        List<Station> nearStations = new ArrayList<>();
        double distance;
        for(Station s: Reseau.getInstance().getStations()) {
            distance = distanceBetween(coordonnee, s.getLocalisation());
            if(distance >= minDistance && distance <= maxDistance) nearStations.add(s);
        }

        return nearStations;
    }

    /**
     * Ajoute les couples (Station, List<Station>) pour chaque station, avec la liste initialisée à la station en clef.
     * Le but étant d'y ajouter les couples de stations déjà traités par dijkstra.
     *
     * @param procheOrigine les stations à ajouter dans la HashMap dans le format désiré.
     */
    private static void addStationsAndListToMap(List<Station> procheOrigine) {
        for(Station station: procheOrigine) {
            List<Station> list = new LinkedList<>();
            list.add(station);
            coupleStations.put(station, list);
        }
    }

    /**
     * Trouve le plus court chemin pour chaque couple procheOrigine-procheDestination, et l'ajoute dans les trajets sauvegardés.
     *
     * @param trajetsSaved les trajets déjà sauvegardés par rapport à une précédente recherche.
     * @param procheDepart les stations proches du départ.
     * @param procheArrivee les stations proches de l'arrivée.
     * @param maxTime le temps à ne pas dépasser pour être ajouté aux trajets sauvegardés.
     * @param volonte La distance que l'utilisateur accepte de parcourir, si jamais le chemin est plus rapide à pied qu'en transport.
     */
    private static void addTrajetsOptimaux(List<Section[]> trajetsSaved, List<Station> procheDepart, List<Station> procheArrivee, LocalTime maxTime, double volonte) {
        for(Station departCandidat: procheDepart) {
            for(Station arriveeCandidat: procheArrivee) {

                //Si la combinaison de stations n'est pas encore traitée
                List<Station> listStationsTraitees = coupleStations.get(departCandidat);
                if(!listStationsTraitees.contains(arriveeCandidat)) {
                    //Calcul de la durée de marche avant d'arriver à la station candidate, puis récupère l'itinéraire avec dijkstra
                    double distanceDebut = distanceBetween(depart, departCandidat.getLocalisation());
                    Duration dureeDebut = walkingDurationOf(distanceDebut);
                    List<Section> trajet = djikstra(departCandidat, arriveeCandidat, horaireDepart.plus(dureeDebut), volonte);

                    if(trajet != null){
                        //Horaire auquel on finit le trajet
                        double distanceFin = distanceBetween(arrivee, arriveeCandidat.getLocalisation());
                        Duration dureeFin = walkingDurationOf(distanceFin);
                        LocalTime horaireArrivee = trajet.get(trajet.size()-1).getArrivee().getHoraireDePassage().plus(dureeFin);

                        //Vérifie si le trajet est actuellement parmi les 5 trajets les plus optimaux
                        if(horaireArrivee.isBefore(maxTime)) {
                            //Si c'est le cas, faire une copie du trajet à ajouter
                            Section sectionDebut = new Section(new Lieu(Constants.DEPART, depart, horaireDepart), departCandidat, dureeDebut, distanceDebut);
                            Section sectionFin = new Section(arriveeCandidat, new Lieu(Constants.ARRIVEE, arrivee, horaireArrivee), dureeFin, distanceFin);
                            Section[] sectionToSave = createNewTrajet(trajet, sectionDebut, sectionFin);

                            //Et l'ajouter dans les trajets, dans l'ordre chronologique d'horaire d'arrivée
                            trajetsSaved.add(getInsertIndex(trajetsSaved, horaireArrivee), sectionToSave);

                            //Gérer la liste des trajets, pour que le nombre de résultats ne dépasse pas Constants.MAX_TRAJETS_NUMBER
                            if(trajetsSaved.size() > Constants.MAX_TRAJETS_NUMBER) {
                                trajetsSaved.remove(trajetsSaved.size() - 1);
                                Section[] dernierTrajet = trajetsSaved.get(trajetsSaved.size() - 1);
                                maxTime = dernierTrajet[dernierTrajet.length - 1].getArrivee().getHoraireDePassage();
                            }
                        }
                    }

                    //Ajoute la station aux couples de stations déjà traités
                    listStationsTraitees.add(arriveeCandidat);
                }
            }
        }
    }

    /**
     * Détermine le plus court chemin pour se rendre d'une Station à une autre en connaissant le Reseau.
     * Les horaires des trains et les temps de correspondance sont pris en compte.
     *
     * @param stationDepart la station de départ.
     * @param stationArrivee la station d'arrivée.
     * @param horaireDepart horaire de départ.
     * @param volonte La distance que l'utilisateur accepte de parcourir, si jamais le chemin est plus rapide à pied qu'en transport.
     * @return le plus court chemin sous forme d'une liste de section, ou null si aucun chemin n'a été trouvé.
     */
    private static List<Section> djikstra(Station stationDepart, Station stationArrivee, LocalTime horaireDepart, double volonte) {
        //Initialisation
        Map<Station, LocalTime> myMap = new HashMap<>();
        for(Station station: Reseau.getInstance().getStations())
            myMap.put(station, null); //null représente ici un temps infini
        myMap.put(stationDepart, horaireDepart);

        //Pour chaque Station, la meilleure liste de section pour y accéder
        Map<Station, List<Section>> trace = new Hashtable<>();
        trace.put(stationDepart, new LinkedList<>());

        //Prendre l'élément le plus petit
        Station currentStation;
        LocalTime currentHoraire;
        while(!myMap.isEmpty() && (currentStation = min(myMap)) != null) {
            currentHoraire = myMap.get(currentStation);
            currentStation.setHoraireDePassage(currentHoraire);

            //Si c'est la destination, c'est gagné!
            if(currentStation.equals(stationArrivee)) return trace.get(currentStation);
            //Sinon, on examine les voisins
            else {
                List<Station> nearStations = getNearStations(currentStation.getLocalisation(), Constants.DEFAULT_MIN_DISTANCE, volonte);
                for(Station nextStation: nearStations) {
                    if(!nextStation.equals(currentStation)) {
                        Section section = walkingItineraire(currentStation.getLocalisation(), nextStation.getLocalisation(), currentHoraire);
                        boucleDjikstra(myMap, currentStation, nextStation, section, currentHoraire, trace);
                    }
                }
                for(SectionTransport sectionTransport: currentStation.getCorrespondances()) {
                    Station nextStation = sectionTransport.getArrivee();
                    boucleDjikstra(myMap, currentStation, nextStation, sectionTransport, currentHoraire, trace);
                }
            }
            myMap.remove(currentStation);
        }
        return null; //pas de trajet possible à cause de l'heure des transports
    }

    /**
     * Fonction annexe utilisée dans dijkstra, permettant d'examiner le trajet pour aller chez les voisins.
     *
     * @param myMap la map qui contient toutes les stations avec leur poids.
     * @param currentStation la station départ pour cette boucle de dijkstra.
     * @param nextStation la station suivant pour cette boucle de dijkstra.
     * @param section la section de la station courante, sur laquelle on se trouve.
     * @param currentHoraire l'horaire actuelle.
     * @param trace les bouts de trajets déjà tracés jusque-là.
     */
    private static void boucleDjikstra(Map<Station, LocalTime> myMap, Station currentStation, Station nextStation,
                                       Section section, LocalTime currentHoraire, Map<Station, List<Section>> trace) {
        List<Section> currentSectionTrace = trace.getOrDefault(currentStation, new LinkedList<>());

        //On ne veut que ceux qui sont encore dans myMap, et que le prochain train
        if(myMap.containsKey(nextStation)) {
            LocalTime prochainDepart = section.getHoraireProchainDepart(currentHoraire);
            if(prochainDepart != null) {
                prochainDepart = prochainDepart.plus(section.getDuree());

                //Ajout du temps de correspondance
                if(!currentSectionTrace.isEmpty()) {
                    Ligne previousLigne = currentSectionTrace.get(currentSectionTrace.size() - 1).getLigne();
                    Ligne currentLigne = section.getLigne();
                    if(previousLigne != null && currentLigne != null && previousLigne != section.getLigne()) {
                        Coordonnee c1 = currentStation.getLocalisation(previousLigne.getNomLigne());
                        Coordonnee c2 = currentStation.getLocalisation(section.getLigne().getNomLigne());
                        prochainDepart = prochainDepart.plus(walkingDurationOf(distanceBetween(c1, c2)))
                                                       .plus(Duration.ofMinutes(Constants.PENALTY));
                    }
                }

                //Mise à jour de l'horaire de départ et du trajet à suivre pour arriver à nextStation
                LocalTime horaire = myMap.get(nextStation);
                if(horaire == null || horaire.isAfter(prochainDepart)) {
                    myMap.put(nextStation, prochainDepart);

                    List<Section> sections = new LinkedList<>(currentSectionTrace);
                    sections.add(section);
                    trace.put(nextStation, sections);
                }
            }
        }
    }

    /**
     * Trouve l'élément de poids minimal du dictionnaire.
     *
     * @param myMap une map telle que celle utilisée dans l'algorithme de djikstra.
     * @return la station avec le plus petit LocalTime.
     */
    private static Station min(Map<Station, LocalTime> myMap) {
        Station stationMin = null;
        LocalTime tempsMin = null;

        Station stationTmp;
        LocalTime tempsTmp;
        for(Map.Entry<Station, LocalTime> entry : myMap.entrySet()) {
            stationTmp = entry.getKey();
            tempsTmp = entry.getValue();
            if(stationMin == null) {
                stationMin = stationTmp;
                tempsMin = tempsTmp;
            }
            else if(tempsMin == null || (tempsTmp != null && tempsMin.isAfter(tempsTmp))) {
                stationMin = stationTmp;
                tempsMin = entry.getValue();
            }
        }
        return (tempsMin == null) ? null : stationMin;
    }

    /**
     * Crée un nouveau trajet avec les copies des sections fournies,
     * et en ajoutant les sections de début et de fin, qui sont à pied.
     *
     * @param trajet le trajet à copier.
     * @param sectionDebut la section de début, à pied.
     * @param sectionFin la section de fin, à pied.
     * @return une copie du trajet demandé, avec ajout du début et de la fin à pied.
     */
    private static Section[] createNewTrajet(List<Section> trajet, Section sectionDebut, Section sectionFin) {
        Section[] copied = new Section[trajet.size() + 2];
        int i = 0;

        copied[i++] = sectionDebut;
        for(Section section: trajet) copied[i++] = section.copy();
        copied[i] = sectionFin;

        return copied;
    }

    /**
     * Trouve l'index auquel insérer le nouvel horaire,
     * en maintenant la liste des trajets dans l'ordre croissant des horaires d'arrivée.
     *
     * @param trajetsSaved la liste des trajets, trié dans l'ordre croissant des horaires d'arrivée.
     * @param horaireArrivee l'horaire d'arrivée du trajet à insérer dans la liste des trajets.
     * @return l'index d'insertion d'un trajet qui a pour horaire d'arrivée celui demandé.
     */
    private static int getInsertIndex(List<Section[]> trajetsSaved, LocalTime horaireArrivee) {
        int index = 0;
        Section[] trajet = trajetsSaved.get(index);
        while(index < trajetsSaved.size() - 1 && horaireArrivee.isAfter(trajet[trajet.length - 1].getArrivee().getHoraireDePassage()))
            trajet = trajetsSaved.get(++index);
        return index;
    }
}