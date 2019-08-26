package fr.app.queue;

public class MessageQueueException extends RuntimeException {

    public MessageQueueException(String message) {
        super(message);
    }

    public MessageQueueException(Throwable cause) {
        super(cause);
    }

    public MessageQueueException(String message, Throwable cause) {
        super(message, cause);
    }
}
