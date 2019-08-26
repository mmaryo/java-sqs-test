package fr.app.queue.sqs;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import fr.app.queue.MessageQueue;
import fr.app.queue.MessageQueueRequest;
import fr.app.queue.MessageQueueResultStatus;
import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SendWithMultipleCallTest {

    private Logger log = LoggerFactory.getLogger(SendWithMultipleCallTest.class);

    @Value("#{'${env.name:DEV}'}")
    private String envName;

    @Autowired
    private MessageQueue queue;

    @Autowired
    private AmazonSQSAsync amazonSQSAsync;

    private String queueName;

    private Set<Integer> indexes = new HashSet<>();

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
        CompletableFuture<List<Integer>> test1 = test_send_async(0, 50);
        CompletableFuture<List<Integer>> test2 = test_send_async(50, 100);
        CompletableFuture<List<Integer>> test3 = test_send_async(100, 150);
        CompletableFuture<List<Integer>> test4 = test_send_async(150, 200);
        test1.thenAccept(indexes::addAll);
        test2.thenAccept(indexes::addAll);
        test3.thenAccept(indexes::addAll);
        test4.thenAccept(indexes::addAll);
        CompletableFuture.allOf(test1, test2, test3, test4).join();
        assertThat(indexes).containsAll(IntStream.range(0, 200).boxed().collect(Collectors.toList()));
    }

    private CompletableFuture<List<Integer>> test_send_async(int inf, int sup) {
        return CompletableFuture.supplyAsync(() -> IntStream.range(inf, sup)
                .mapToObj(i -> {
                    // when
                    log.info("Test send index " + i);
                    return new SendResult(queue.send(queueName, "abc" + i), i);
                })
                .map(res -> {
                    // then
                    Awaitility.await()
                            .atMost(3, TimeUnit.SECONDS)
                            .until(() -> res.call.isDone());
                    MessageQueueRequest result = res.call.join();
                    assertThat(result.getStatus()).isEqualTo(MessageQueueResultStatus.SUCCESS);
                    return res.index;
                })
                .collect(Collectors.toList())
        );
    }

}

class SendResult {
    CompletableFuture<MessageQueueRequest> call;
    Integer index;

    public SendResult(CompletableFuture call, Integer index) {
        this.call = call;
        this.index = index;
    }
}

