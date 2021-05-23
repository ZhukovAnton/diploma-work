package com.stanum.skrudzh.service.reminder;

public enum PushEvent {
    SALTEDGE_INTERACTIVE("SALTEDGE_NOTIFY_INTERACTIVE_NOTIFICATION_MESSAGE_KEY", "SALTEDGE"),
    SALTEDGE_FETCH_ACCOUNTS("SALTEDGE_NOTIFY_FETCH_ACCOUNTS_NOTIFICATION_MESSAGE_KEY", "SALTEDGE"),
    SALTEDGE_FETCH_RECENT("SALTEDGE_NOTIFY_FETCH_RECENT_NOTIFICATION_MESSAGE_KEY", "SALTEDGE"),
    SALTEDGE_FETCH_FULL("SALTEDGE_NOTIFY_FETCH_FULL_NOTIFICATION_MESSAGE_KEY", "SALTEDGE"),
    SALTEDGE_FINISH("SALTEDGE_NOTIFY_FINISH_NOTIFICATION_MESSAGE_KEY", "SALTEDGE"),
    SALTEDGE_FAILURE("SALTEDGE_FAILURE_NOTIFICATION_MESSAGE_KEY", "SALTEDGE");

    private String key;
    private String category;
    private String threadId;

    public String getKey() {
        return key;
    }

    public String getCategory() {
        return category;
    }

    public String getThreadId() {
        return threadId;
    }

    PushEvent(String key, String category, String threadId) {
        this.key = key;
        this.category = category;
        this.threadId = threadId;
    }

    PushEvent(String key, String category) {
        this.key = key;
        this.category = category;
    }
}
