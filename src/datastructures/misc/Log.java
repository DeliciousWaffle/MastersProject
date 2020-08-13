package datastructures.misc;

public class Log {

    public enum Type {
        SIMPLE, DEVELOPER
    }

    private final Type type;
    private final String message;

    public Log(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return type + ": " + message;
    }
}