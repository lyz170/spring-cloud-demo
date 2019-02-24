package com.mycloud.demo.usercontext;

public class UserContext {

    public static final String CORRELATION_ID = "tmx-correlation-id";
    public static final String USER_ID = "tmx-user-id";

    private String correlationId;
    private String userId;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
