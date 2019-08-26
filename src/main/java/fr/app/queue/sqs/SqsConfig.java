package fr.app.queue.sqs;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import fr.app.queue.MessageQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.core.env.ResourceIdResolver;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.support.destination.DynamicQueueUrlDestinationResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.core.DestinationResolver;
import org.springframework.messaging.handler.annotation.support.PayloadArgumentResolver;

import java.util.Collections;

@Configuration
@EnableSqs
public class SqsConfig {

    @Value("#{'${env.name:DEV}'}")
    private String envName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.access-key}")
    private String awsAccessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String awsSecretKey;


    @Bean
    public Headers headers() {
        return new Headers();
    }

    @Bean
    public MessageQueue queueMessagingSqs(Headers headers,
                                          QueueMessagingTemplate queueMessagingTemplate) {
        Sqs queue = new Sqs();
        queue.setQueueMessagingTemplate(queueMessagingTemplate);
        queue.setHeaders(headers);
        return queue;
    }

    private ResourceIdResolver getResourceIdResolver() {
        return queueName -> envName + "-" + queueName;
    }

    @Bean
    public DestinationResolver destinationResolver(AmazonSQSAsync amazonSQSAsync) {
        DynamicQueueUrlDestinationResolver destinationResolver = new DynamicQueueUrlDestinationResolver(
                amazonSQSAsync,
                getResourceIdResolver());
        destinationResolver.setAutoCreate(true);
        return destinationResolver;
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync,
                                                         DestinationResolver destinationResolver) {
        return new QueueMessagingTemplate(amazonSQSAsync, destinationResolver, null);
    }

    @Bean
    public QueueMessageHandlerFactory queueMessageHandlerFactory() {
        QueueMessageHandlerFactory factory = new QueueMessageHandlerFactory();
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setStrictContentTypeMatch(false);
        factory.setArgumentResolvers(Collections.singletonList(new PayloadArgumentResolver(messageConverter)));
        return factory;
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSqs) {
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAmazonSqs(amazonSqs);
        factory.setMaxNumberOfMessages(10);
        factory.setWaitTimeOut(2);
        return factory;
    }

}