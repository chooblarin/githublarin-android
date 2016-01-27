package com.chooblarin.githublarin.model;

import android.util.Xml;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class FeedParser {

    public static List<Feed> parseString(String string) {
        List<Feed> entries = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new StringReader(string));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    // nothing to do
                } else if (eventType == XmlPullParser.START_TAG) {
                    String elementName = parser.getName();
                    if (elementName.equals("entry")) {
                        entries.add(parseEntry(parser));
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                } else if (eventType == XmlPullParser.TEXT) {
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public static Feed parseEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        Feed feed = new Feed();
        int eventType = parser.getEventType();
        String elementName = parser.getName();
        while (eventType != XmlPullParser.END_TAG || !elementName.equals("entry")) {
            if (eventType == XmlPullParser.START_TAG) {
                if ("id".equals(elementName)) {
                    feed.entryId = parser.nextText();

                }
                if ("published".equals(elementName)) {
                    String publishedString = parser.nextText();
                    feed.published = LocalDateTime.parse(publishedString,
                            DateTimeFormatter.ofPattern("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'"));
                }
                if ("updated".equals(elementName)) {
                    feed.updated = parser.nextText();
                }
                if ("link".equals(elementName)) {
                    for (int i = 0, count = parser.getAttributeCount(); i < count; i++) {
                        String attrName = parser.getAttributeName(i);
                        if ("href".equals(attrName)) {
                            feed.link = parser.getAttributeValue(i);
                        }
                    }
                }
                if ("title".equals(elementName)) {
                    feed.title = parser.nextText();
                }
                if ("author".equals(elementName)) {
                    eventType = parser.nextTag();
                    elementName = parser.getName();
                    if (XmlPullParser.START_TAG == eventType && "name".equals(elementName)) {
                        feed.authorName = parser.nextText();
                    }
                }
                if ("thumbnail".equals(elementName)) {
                    for (int i = 0, count = parser.getAttributeCount(); i < count; i++) {
                        String attributeName = parser.getAttributeName(i);
                        if ("url".equals(attributeName)) {
                            feed.thumbnail = parser.getAttributeValue(i);
                        }
                    }
                }
            }
            eventType = parser.next();
            elementName = parser.getName();
        }
        return feed;
    }
}
