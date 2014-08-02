package me.odj.cymorth;

import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by owain on 9/18/13.
 */
public class HTMLImporter {
    public static HTMLImporter _instance = null;

    public static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("EEE dd-MM-yyyy HH:mm");
    public static final String NBSP_IN_UTF8 = "\u00a0";

    private Document document = null;
    private Slot[] lectures = null;
    private Date[] times = null;

    public Document parse(String html) {
        if(this.document != null) return this.document;
        this.document = Jsoup.parse(html);
        return this.document;
    }

    public Document parse(InputStream html) throws IOException {
        if(this.document != null) return this.document;
        this.document = Jsoup.parse(html, "UTF-8", "https://studentrecord" +
                ".aber.ac.uk/en/");
        return this.document;
    }

    public Document parse() {
        return this.document;
    }

    public Date[] getDates() throws ParseException {
        if(this.times != null) return this.times;
        Date times[] = new Date[UniCalendar.SLOTS.length * 5];
        Elements dates = this.parse().select("td.bold_label[style=border:1px " +
                "black dotted]");
        String dateStr = null;
        int slotLen = UniCalendar.SLOTS.length;
        for(int day=0; day < slotLen * 5; day += slotLen) {
            dateStr = dates.get(day / slotLen).text().replaceAll("<br />",
                    "").replaceAll("(\n|\r)", "").replaceAll(NBSP_IN_UTF8,
                "").replaceAll("&nbsp;", "").trim();
            if(dateStr == "" || dateStr == null) continue;
            for(int slot=0; slot < slotLen; slot++) {
                Log.d("abersistant", dateStr + " " + UniCalendar.SLOTS[slot]);
                Date d = dateFormat.parse(dateStr + " " + UniCalendar
                        .SLOTS[slot]);
                times[day + slot] = d;
            }
        }
        this.times = times;
        return this.times;
    }

    public Slot[] getLectures() throws IOException, ParseException {
        if(this.lectures != null) return this.lectures;

        Elements lectures = this.parse().select("td[style=border:1px black " +
                "dotted]:not(.bold_label)");
        Slot[] slots = new Slot[UniCalendar.SLOTS.length * 5];
        for(int i=0; i<lectures.size(); i++) {
            try {
                String text = lectures.get(i).html().replaceAll
                        (NBSP_IN_UTF8, "").replaceAll("&nbsp;",
                        "").replaceAll("<br />", "\n").replaceAll("<b>",
                        "").replaceAll("</b>", "").trim();
                if(text == "" || text == null) continue;
                String[] lines = text.split("(\n|\r\n)");
                if(lines.length >= 3) {
                    Slot slot = new Slot();
                    slot.time = this.getDates()[i];
                    slot.type = lines[0];
                    slot.module = lines[1];
                    slot.room = lines[2];
                    slot.readableTime = String.format("%1$tH:%1$tM",
                            slot.time.getTime());
                    slots[i] = slot;
                    Log.d("abersistant", String.format("%d: {%s}", i, slot));
                }
            } catch(ArrayIndexOutOfBoundsException e) {

            }
        }
        this.lectures = slots;
        return this.lectures;
    }

    public int addToDatabase(Context c, boolean replace) throws IOException,
            ParseException {
        Slot[] slots = this.getLectures();
        DBHelper h = new DBHelper(c);
        return h.insertLecture(slots, replace);
    }

    public int addToDatabase(Context c) throws IOException, ParseException {
        return this.addToDatabase(c, false);
    }

    public static HTMLImporter fromFile(InputStream input) throws
            IOException,
            ParseException {
        HTMLImporter importer = new HTMLImporter();
        importer.parse(input);
        return importer;
    }

    public static HTMLImporter fromFile(Context c) throws IOException,
            ParseException {
        InputStream input = c.getResources().openRawResource(R.raw.timetable);
        return fromFile(input);
    }
}
