package se.lexicon.subscriptionapi.exception;

public class InvalidPlanException extends RuntimeException {
    public InvalidPlanException(String message) {
        super(message);
    }
}
