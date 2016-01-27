package com.chooblarin.githublarin.model;

import org.threeten.bp.LocalDateTime;

public class Feed {

    public String entryId;
    public LocalDateTime published;
    public String updated;
    public String title;
    public String link;
    public String authorName;
    public String authorUrl;
    public String thumbnail;

    public Action action;
}
