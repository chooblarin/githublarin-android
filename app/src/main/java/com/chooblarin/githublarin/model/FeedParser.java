package com.chooblarin.githublarin.model;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class FeedParser {

    public static List<Entry> parseString(String string) {
        List<Entry> entries = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new StringReader(string));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    // nothing to do
                } else if (eventType == XmlPullParser.START_TAG) {
                    String elementName = parser.getName();
                    // Log.d("mimic", "start tag : " + elementName);
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

    public static Entry parseEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        Entry entry = new Entry();
        int eventType = parser.getEventType();
        String elementName = parser.getName();
        while (eventType != XmlPullParser.END_TAG || !elementName.equals("entry")) {
            if (eventType == XmlPullParser.START_TAG) {
                if ("published".equals(elementName)) {
                    entry.published = parser.nextText();
                }
                if ("updated".equals(elementName)) {
                    entry.updated = parser.nextText();
                }
                if ("link".equals(elementName)) {
                    entry.link = parser.nextText();
                }
                if ("title".equals(elementName)) {
                    entry.title = parser.nextText();
                }
            }
            eventType = parser.next();
            elementName = parser.getName();
        }
        return entry;
    }
}
