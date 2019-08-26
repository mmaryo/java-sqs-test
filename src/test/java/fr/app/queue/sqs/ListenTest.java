package fr.app.queue.sqs;

import fr.app.queue.MessageQueue;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ListenTest {

    @Autowired
    private MessageQueue queue;

    private final String queueName = "test-queue-receive";

    private String result = null;

    @Test
    public void test_listen() {
        // given
        String data = "abc";

        // when
        queue.send(queueName, data).join();

        // then
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until(() -> Objects.nonNull(result));

        Assertions.assertThat(result).equals(data);
    }

    @SqsListener(value = queueName)
    public void receive(String data) {
        this.result = data;
    }
}