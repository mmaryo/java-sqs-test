package fr.app.queue.sqs;

import java.util.Map;

public class Headers {

    private static final String TOKEN_NAME = "token";
    private static final String TOKEN_VALUE = "token";

    private final Map<String, Object> headers;

    public Headers() {
        headers = Map.of(TOKEN_NAME, TOKEN_VALUE);
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }
}
