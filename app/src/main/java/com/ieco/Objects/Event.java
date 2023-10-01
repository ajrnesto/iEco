package com.ieco.Objects;

public class Event {
    String id;
    String eventName;
    String description;
    String bannerFileName;
    long schedule;
    String queryKey;

    public Event() {
    }

    public Event(String id, String eventName, String description, String bannerFileName, long schedule, String queryKey) {
        this.id = id;
        this.eventName = eventName;
        this.description = description;
        this.bannerFileName = bannerFileName;
        this.schedule = schedule;
        this.queryKey = queryKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBannerFileName() {
        return bannerFileName;
    }

    public void setBannerFileName(String bannerFileName) {
        this.bannerFileName = bannerFileName;
    }

    public long getSchedule() {
        return schedule;
    }

    public void setSchedule(long schedule) {
        this.schedule = schedule;
    }

    public String getQueryKey() {
        return queryKey;
    }

    public void setQueryKey(String queryKey) {
        this.queryKey = queryKey;
    }
}
