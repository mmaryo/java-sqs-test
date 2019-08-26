package fr.app.queue.sqs;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Helper {

    public static String randomString() {
        String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return IntStream.range(0, 5)
                .mapToObj(n -> String.valueOf(candidateChars.charAt(new Random().nextInt(candidateChars.length()))))
                .collect(Collectors.joining(""));
    }
}
