package fr.uparis.backapp.exceptions;

/**
 * Exception levée lorsqu'une station n'est pas trouvée dans le réseau de transports.
 **/
public class StationNotFoundException extends Exception {

    /**
     * Crée une nouvelle instance de l'exception avec un message d'erreur.
     *
     * @param message le message d'erreur décrivant la raison de l'exception.
     */
    public StationNotFoundException(String message) {
        super(message);
    }
}
