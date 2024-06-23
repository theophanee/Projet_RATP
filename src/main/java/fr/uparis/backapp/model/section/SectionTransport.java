package fr.uparis.backapp.model.section;

import fr.uparis.backapp.model.Ligne;
import fr.uparis.backapp.model.lieu.Station;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Représente une Section de transport, avec deux stations, dans le Reseau.
 */
public class SectionTransport extends Section {
    final protected Ligne ligne;
    final private Set<LocalTime> horairesDepart;

    /**
     * Constructeur de la SectionTransport.
     *
     * @param stationDepart  Station de depart de la Section.
     * @param stationArrivee Station d'arrivée de la Section.
     * @param duree          durée estimée de la Section.
     * @param distance       distance de la Section.
     * @param ligne          Ligne empruntée par la Section.
     */
    public SectionTransport(Station stationDepart, Station stationArrivee, Duration duree, double distance, Ligne ligne) {
        super(stationDepart, stationArrivee, duree, distance);
        this.ligne = ligne;
        this.horairesDepart = new HashSet<>();
    }

    /**
     * Renvoie la station de départ de la Section.
     *
     * @return la station de départ de la Section.
     */
    @Override
    public Station getDepart() {
        return (Station) depart;
    }

    /**
     * Renvoie la station d'arrivée de la Section.
     *
     * @return la station d'arrivée de la Section.
     */
    @Override
    public Station getArrivee() {
        return (Station) arrivee;
    }

    /**
     * Renvoie la Ligne empruntée par la Section.
     *
     * @return la Ligne empruntée par la Section.
     */
    @Override
    public Ligne getLigne() {
        return ligne;
    }

    /**
     * Renvoie les heures de départ de la Section.
     *
     * @return les heures de départ de la Section.
     */
    public Set<LocalTime> getHorairesDepart() {
        return horairesDepart;
    }

    /**
     * Renvoie l'heure du prochain départ.
     *
     * @param depart l'heure actuelle.
     * @return l'heure du prochain départ.
     */
    @Override
    public LocalTime getHoraireProchainDepart(LocalTime depart) {
        LocalTime prochainDepart = LocalTime.MAX;
        boolean hasProchainDepart = false;
        for (LocalTime localTime : horairesDepart) {
            if (localTime.isAfter(depart)) {
                hasProchainDepart = true;
                if (localTime.isBefore(prochainDepart)) prochainDepart = localTime;
            }
        }
        return (hasProchainDepart) ? prochainDepart : null;
    }

    /**
     * Ajout d'un horaire de départ à la Section, si elle n'y est pas déjà.
     *
     * @param horaire l'horaire de départ à ajouter à la Section.
     */
    public void addHoraireDepart(LocalTime horaire) {
        this.horairesDepart.add(horaire);
    }

    /**
     * Ajout des horaires de départ à la Section, si elles n'y sont pas déjà.
     *
     * @param horaires les horaires de départ à ajouter à la Section.
     */
    public void addHorairesDepart(List<LocalTime> horaires) {
        horairesDepart.addAll(horaires);
    }

    /**
     * Suppression d'un horaire de départ de la Section, si elle existe.
     *
     * @param horaire l'horaire de départ à enlever de la Section.
     */
    public void removeHoraireDepart(LocalTime horaire) {
        this.horairesDepart.remove(horaire);
    }

    /**
     * Comparaison de deux Section.
     *
     * @param o objet avec lequel comparer.
     * @return true si o et this ont les mêmes stations de départ et d'arrivée.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectionTransport sectionTransport = (SectionTransport) o;
        return sectionTransport.getDepart().equals(this.getDepart()) && sectionTransport.getArrivee().equals(this.getArrivee());
    }

    /**
     * Retourne une valeur de code de hachage pour Section.
     *
     * @return la valeur de code de hachage pour Section.
     */
    @Override
    public int hashCode() {
        int result = depart != null ? depart.hashCode() : 0;
        result = 31 * result + (arrivee != null ? arrivee.hashCode() : 0);
        result = 31 * result + (ligne != null ? ligne.hashCode() : 0);
        return result;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères d'un objet Coordonnee.
     *
     * @return la représentation sous forme de chaîne de caractères d'un objet Coordonnee.
     */
    @Override
    public String toString() {
        String s = this.getLigne().getNomLigne() + " : ";
        s += this.depart.getNomLieu() + " -> " + this.arrivee.getNomLieu();
        s += " (durée = " + this.duree.toString() + ", distance = " + this.distance + " km";
        if (!this.horairesDepart.isEmpty()) s += ", à";
        for (LocalTime time : this.horairesDepart) s += " " + time;
        return s + ")";
    }

    /**
     * Fournit une copie de la Section courante.
     */
    @Override
    public Section copy() {
        return new SectionTransport(getDepart().copy(), getArrivee().copy(), duree, distance, ligne);
    }

    /**
     * Regarde si la Section démarre d'une certaine station.
     *
     * @param currentStation station de départ.
     * @return l'égalité entre la station donnée et celle de départ de la Section.
     */
    public boolean isStationDepart(Station currentStation) {
        return currentStation.equals(this.depart);
    }

    /**
     * Regarde si la Section va jusqu'à une certaine station.
     *
     * @param nextStation station d'arrivée.
     * @return l'égalité entre la station donnée et celle d'arrivée de la Section.
     */
    public boolean isStationArrivee(Station nextStation) {
        return nextStation.equals(this.arrivee);
    }

    /**
     * Regarde si la Section va d'une certaine station à une autre.
     *
     * @param currentStation station de départ.
     * @param nextStation    station d'arrivée.
     * @return l'égalité entre les stations données et celles de la Section.
     */
    public boolean areStations(Station currentStation, Station nextStation) {
        return isStationDepart(currentStation) && isStationArrivee(nextStation);
    }

    /**
     * Retourne la prochaine section dans la même ligne à partir de la station d'arrivée de la section actuelle.
     *
     * @param sections un ensemble de sections à parcourir.
     * @return la prochaine section dans la même ligne, ou null si aucune section n'est trouvée.
     */
    public SectionTransport moveToNextSectionInTheSameLine(Set<SectionTransport> sections) {
        for (SectionTransport section : sections)
            if (section.isStationDepart((Station) arrivee) && section.getLigne().equals(this.ligne))
                return section;
        return null;
    }
}
