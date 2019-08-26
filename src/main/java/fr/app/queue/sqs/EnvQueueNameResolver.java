package fr.app.queue.sqs;

public class EnvQueueNameResolver {

    public static String generate(String queueName, String envName) {
        return envName + "-" + queueName;
    }

}
