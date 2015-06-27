package com.chooblarin.githublarin.model;

import android.text.format.Time;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class parses generic Atom feeds.
 *
 * <p>Given an InputStream representation of a feed, it returns a List of entries,
 * where each list element represents a single entry (post) in the XML feed.
 *
 * <p>An example of an Atom feed can be found at:
 * http://en.wikipedia.org/w/index.php?title=Atom_(standard)&oldid=560239173#Example_of_an_Atom_1.0_feed
 */
public class FeedParser {

    // Constants indicting XML element names that we're interested in
    private static final int TAG_ID = 1;
    private static final int TAG_TITLE = 2;
    private static final int TAG_PUBLISHED = 3;
    private static final int TAG_LINK = 4;

    // We don't use XML namespaces
    private static final String ns = null;

    public List<Entry> parse(InputStream in)
            throws XmlPullParserException, IOException, ParseException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<Entry> readFeed(XmlPullParser parser)
            throws XmlPullParserException, IOException, ParseException {
        List<Entry> entries = new ArrayList<Entry>();

        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private Entry readEntry(XmlPullParser parser)
            throws XmlPullParserException, IOException, ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String id = null;
        String title = null;
        String link = null;
        long publishedOn = 0;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("id")){
                id = readTag(parser, TAG_ID);
            } else if (name.equals("title")) {
                title = readTag(parser, TAG_TITLE);
            } else if (name.equals("link")) {
                String tempLink = readTag(parser, TAG_LINK);
                if (tempLink != null) {
                    link = tempLink;
                }
            } else if (name.equals("published")) {
                Time t = new Time();
                t.parse3339(readTag(parser, TAG_PUBLISHED));
                publishedOn = t.toMillis(false);
            } else {
                skip(parser);
            }
        }
        return new Entry(id, title, link, publishedOn);
    }

    private String readTag(XmlPullParser parser, int tagType)
            throws IOException, XmlPullParserException {
        String tag = null;
        String endTag = null;

        switch (tagType) {
            case TAG_ID:
                return readBasicTag(parser, "id");
            case TAG_TITLE:
                return readBasicTag(parser, "title");
            case TAG_PUBLISHED:
                return readBasicTag(parser, "published");
            case TAG_LINK:
                return readAlternateLink(parser);
            default:
                throw new IllegalArgumentException("Unknown tag type: " + tagType);
        }
    }

    private String readBasicTag(XmlPullParser parser, String tag)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return result;
    }

    private String readAlternateLink(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        String link = null;
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (relType.equals("alternate")) {
            link = parser.getAttributeValue(null, "href");
        }
        while (true) {
            if (parser.nextTag() == XmlPullParser.END_TAG) break;
            // Intentionally break; consumes any remaining sub-tags.
        }
        return link;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    public static class Entry {
        public final String id;
        public final String title;
        public final String link;
        public final long published;

        Entry(String id, String title, String link, long published) {
            this.id = id;
            this.title = title;
            this.link = link;
            this.published = published;
        }
    }
}
