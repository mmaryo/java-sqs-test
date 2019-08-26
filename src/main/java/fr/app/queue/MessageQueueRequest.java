package fr.app.queue;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageQueueRequest {

    private MessageQueueResultStatus status;
    private MessageQueueException exception;

    public static MessageQueueRequest success() {
        return MessageQueueRequest.builder().status(MessageQueueResultStatus.SUCCESS).build();
    }

    public static MessageQueueRequest failure(MessageQueueException e) {
        return MessageQueueRequest.builder().status(MessageQueueResultStatus.FAILURE).exception(e).build();
    }
}
