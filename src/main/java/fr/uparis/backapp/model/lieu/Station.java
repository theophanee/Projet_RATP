package fr.uparis.backapp.model.lieu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.uparis.backapp.model.Coordonnee;
import fr.uparis.backapp.model.section.SectionTransport;

import java.time.LocalTime;
import java.util.*;

/**
 * Représente une Station du Reseau de transport.
 */

public class Station extends Lieu {
    final private Map<String, Coordonnee> autresLocalisations;
    @JsonIgnore
    final private Set<SectionTransport> correspondances;

    /**
     * Constructeur de la classe Station à partir de tous les attributs.
     *
     * @param nomStation             nom de la Station.
     * @param localisationPrincipale Coordonnee principale de la Station.
     * @param autresLocalisations    Coordonne différentes de localisation de la Station.
     * @param correspondances        liste des correspondances avec d'autres Ligne du Reseau.
     */
    public Station(String nomStation, Coordonnee localisationPrincipale, Map<String, Coordonnee> autresLocalisations, Set<SectionTransport> correspondances) {
        super(nomStation, localisationPrincipale);
        this.autresLocalisations = autresLocalisations;
        this.correspondances = correspondances;
    }

    /**
     * Constructeur de la classe Station à partir du nom de la station et de sa localisation principale.
     *
     * @param nomStation             nom de la Station.
     * @param localisationPrincipale Coordonnee principale de la Station.
     */
    public Station(String nomStation, Coordonnee localisationPrincipale) {
        this(nomStation, localisationPrincipale, new HashMap<>(), new HashSet<>());
    }

    /**
     * Constructeur utilisé pour faire une copie de la Station, utilisé pour retourner un trajet sauvegardé.
     *
     * @param nomStation nom de la Station.
     * @param localisation Coordonnee de la Station.
     * @param horaireDePassage horaire de passage à la station.
     */
    private Station(String nomStation, Coordonnee localisation, LocalTime horaireDePassage) {
        super(nomStation, localisation, horaireDePassage);
        autresLocalisations = null;
        correspondances = null;
    }

    /**
     * Renvoie la Coordonnee de la ligne demandée, ou bien la coordonnée principale si cette ligne n'est pas dans la map.
     *
     * @param nomLigne le nom de la ligne pou laquelle on cherche la localisation de la Station.
     * @return la Coordonnee de la ligne demandée.
     */
    public Coordonnee getLocalisation(String nomLigne) {
        return autresLocalisations.getOrDefault(nomLigne, getLocalisation());
    }

    /**
     * Ajoute une localisation de la station, si elle est différente de la localisation principale.
     *
     * @param nomLigne     le nom de la ligne concernée par cette localisation.
     * @param localisation la localisation de la Station pour la ligne donnée.
     */
    public void addLocalisation(String nomLigne, Coordonnee localisation) {
        if (!this.localisation.equals(localisation))
            autresLocalisations.put(nomLigne, localisation);
    }

    /**
     * Suppression d'une localisation de la station, si elle est différente de la localisation principale.
     *
     * @param nomLigne le nom de la ligne concernée par cette localisation.
     */
    public void removeLocalisation(String nomLigne) {
        autresLocalisations.remove(nomLigne);
    }

    /**
     * Renvoie les correspondances avec les Ligne du Reseau.
     *
     * @return les correspondances avec les Ligne du Reseau.
     */
    public Set<SectionTransport> getCorrespondances() {
        return correspondances;
    }

    /**
     * Ajout d'une correspondance à la Station, si elle n'y est pas déjà.
     *
     * @param sectionTransport une nouvelle correspondance possible à la station courante.
     */
    public void addCorrespondance(SectionTransport sectionTransport) {
        if (sectionTransport.getDepart().equals(this))
            this.correspondances.add(sectionTransport);
    }

    /**
     * Suppression d'une correspondance de la Station, si elle existe.
     *
     * @param sectionTransport une ancienne correspondance possible depuis la station courante.
     */
    public void removeCorrespondance(SectionTransport sectionTransport) {
        this.correspondances.remove(sectionTransport);
    }

    /**
     * Comparaison de deux stations.
     *
     * @param o objet avec lequel comparer.
     * @return si o et this représentent la même station, donc ont le même nom de station (unicité des noms de station).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return station.getNomLieu().equals(this.nomLieu); //unicité des noms de station
    }

    /**
     * Retourne une valeur de code de hachage pour la station.
     *
     * @return la valeur de code de hachage pour la station.
     */
    @Override
    public int hashCode() {
        return nomLieu.hashCode();
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères d'un objet Station.
     *
     * @return la représentation sous forme de chaîne de caractères d'un objet Station.
     */
    @Override
    public String toString() {
        String s = nomLieu + " -> ";
        for (SectionTransport sectionTransport : correspondances) {
            String nomLigne = sectionTransport.getLigne().getNomLigne();
            s += nomLigne + " (" + autresLocalisations.getOrDefault(nomLigne, localisation) + ") ";
        }
        return s;
    }

    /**
     * Fournit une copie de la station courante.
     */
    public Station copy() {
        return new Station(nomLieu, localisation, horaireDePassage);
    }
}
