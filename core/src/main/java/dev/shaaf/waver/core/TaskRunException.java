package dev.shaaf.waver.core;

public class TaskRunException extends RuntimeException{

    public TaskRunException() {
        super();
    }

    public TaskRunException(String s) {
        super(s);
    }

    public TaskRunException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskRunException(Throwable cause) {
        super(cause);
    }

    public TaskRunException(String inputString, String invalidInputType) {
    }
}
