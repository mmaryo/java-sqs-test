package fr.app.queue.sqs;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import fr.app.queue.MessageQueue;
import fr.app.queue.MessageQueueRequest;
import fr.app.queue.MessageQueueResultStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SendTest {

    @Value("#{'${env.name:DEV}'}")
    private String envName;

    @Autowired
    private MessageQueue queue;

    @Autowired
    private AmazonSQSAsync amazonSQSAsync;

    private String queueName;

    @Before
    public void init() {
        queueName = "test-queue-" + Helper.randomString();
    }

    @After
    public void after() {
        amazonSQSAsync.deleteQueue(EnvQueueNameResolver.generate(queueName, envName));
    }

    @Test
    public void test_send() {
        // when
        CompletableFuture<MessageQueueRequest> req = queue.send(queueName, "abc");

        // then
        MessageQueueRequest res = req.join();
        assertThat(res.getStatus()).isEqualTo(MessageQueueResultStatus.SUCCESS);
    }

}

