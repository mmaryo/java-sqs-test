package fr.app.queue;

import java.util.concurrent.CompletableFuture;

public interface MessageQueue {

    <T> CompletableFuture<MessageQueueRequest> send(String queueName, T payload);

}