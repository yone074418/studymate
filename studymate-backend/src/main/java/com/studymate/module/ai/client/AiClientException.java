package com.studymate.module.ai.client;

public class AiClientException extends RuntimeException {

    private final String responseContent;

    public AiClientException(String message) {
        this(message, null, null);
    }

    public AiClientException(String message, Throwable cause) {
        this(message, null, cause);
    }

    public AiClientException(String message, String responseContent) {
        this(message, responseContent, null);
    }

    public AiClientException(String message, String responseContent, Throwable cause) {
        super(message, cause);
        this.responseContent = responseContent;
    }

    public String getResponseContent() {
        return responseContent;
    }
}
