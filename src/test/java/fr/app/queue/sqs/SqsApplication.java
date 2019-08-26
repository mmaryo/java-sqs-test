package fr.app.queue.sqs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {"fr.app"})
public class SqsApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(SqsApplication.class, args);
    }
}
