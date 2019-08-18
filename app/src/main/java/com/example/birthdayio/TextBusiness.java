package com.example.birthdayio;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.TextUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import model.Person;

public class TextBusiness {
    ContentResolver cr;
    public TextBusiness(ContentResolver cr) {
        this.cr = cr;
    }

    public void run() {
        try {
            ArrayList<String> birthdayNames = getBirthdayNames();
            ArrayList<Person> people = getPhoneNumbers(birthdayNames);
            sendTexts(people);
        } catch (Exception e) {
            // should make a notification here or something
        }
    }

    private void sendTexts(ArrayList<Person> people) {
        for (Person person: people) {
            String destination = getDestination(person);
//            destination = "+1 571 290 1731"; // used for testing
            String message = "Happy Birthday, " + person.getFirstName() + "!";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(destination, null, message, null, null);
        }
    }

    private String getDestination(Person person) {
        if (!person.getMobile().equals("")) {
            return person.getMobile();
        }
        else if(!person.getOther().equals("")) {
            return person.getOther();
        }
        else if(!person.getWork().equals("")) {
            return person.getWork();
        }
        else if(!person.getHome().equals("")) {
            return person.getHome();
        }

        return "";
    }

    private ArrayList<Person> getPhoneNumbers(ArrayList<String> names) {
        ArrayList<Person> people = new ArrayList<>();

        for (String name: names) {
            Person person = new Person(name);

            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                    "DISPLAY_NAME = '" + name + "'", null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String contactId =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                // Get all phone numbers associated with the person
                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                while (phones != null && phones.moveToNext()) {
                    String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    switch (type) {
                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                            person.setHome(number);
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                            person.setMobile(number);
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                            person.setWork(number);
                            break;
                        default:
                            person.setOther(number);
                            break;
                    }
                }
                people.add(person);
                if (phones != null) {
                    phones.close();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }

        return people;
    }
    /**
     * Trims the events down to just the name of the person
     * @return
     */
    private ArrayList<String> getBirthdayNames() {
        ArrayList<String> events = getBirthdayEvents();
        ArrayList<String> birthdayNames = new ArrayList<>();

        for (String event: events) {
            ArrayList<String> parts = new ArrayList<>(Arrays.asList(event.split(" ")));
            parts.remove(parts.size() - 1);

            String possessiveName = TextUtils.join(" ", parts);
            String name = possessiveName.substring(0, possessiveName.length() -2);
            birthdayNames.add(name);
        }

        return birthdayNames;
    }

    /**
     * Gets all events in device calendar for the day and returns list of birthday events
     * @return
     */
    private ArrayList<String> getBirthdayEvents() {
        ArrayList<String> birthdayEvents = new ArrayList<>();

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");

        String[] dateParts = formatter.format(date).split(" ");

        Calendar beginTime = Calendar.getInstance();
//        beginTime.set(2019, Calendar.MARCH, 30, 0, 0);
        beginTime.set(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[0]), 12, 0, 0);
        long startMills = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
//        endTime.set(2019, Calendar.MARCH, 30, 23, 59);
        endTime.set(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[0]), 12, 59, 59);
        long endMills = endTime.getTimeInMillis();
;
        ContentUris.appendId(builder, startMills);
        ContentUris.appendId(builder, endMills);
        Cursor eventCursor = cr.query(builder.build(), new String[]{CalendarContract.Instances.TITLE,
                        CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.DESCRIPTION},
                null, null, null);

        if (eventCursor != null) {
            while (eventCursor.moveToNext()) {
                final String title = eventCursor.getString(0);
                if (title.contains("birthday")) {
                    birthdayEvents.add(title);
                }
            }

            eventCursor.close();
        }

        return birthdayEvents;
    }
}
