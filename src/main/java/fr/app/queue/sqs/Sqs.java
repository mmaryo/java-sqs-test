package fr.app.queue.sqs;

import fr.app.queue.MessageQueue;
import fr.app.queue.MessageQueueException;
import fr.app.queue.MessageQueueRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.util.concurrent.CompletableFuture;

public class Sqs implements MessageQueue {

    private Logger log = LoggerFactory.getLogger(Sqs.class);

    private QueueMessagingTemplate queueMessagingTemplate;
    private Headers headers;

    protected Sqs() {
    }

    @Override
    public <T> CompletableFuture<MessageQueueRequest> send(String queueName, T payload) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Send message to queue " + queueName);
                queueMessagingTemplate.convertAndSend(queueName, payload, headers.getHeaders());
                return MessageQueueRequest.success();
            } catch (MessageQueueException e) {
                log.error("Error while sending message to queue " + queueName + " | " + e.getMessage(), e);
                return MessageQueueRequest.failure(e);
            }
        });
    }

    public void setQueueMessagingTemplate(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

}
