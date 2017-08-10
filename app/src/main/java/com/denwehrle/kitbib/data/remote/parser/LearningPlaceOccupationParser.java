package com.denwehrle.kitbib.data.remote.parser;

import android.support.annotation.NonNull;

import com.denwehrle.kitbib.data.model.LearningPlaceOccupation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Dennis Wehrle
 */
public class LearningPlaceOccupationParser {

    private static final String LOCATION = "location_name";
    private static final String FREE_SEATS = "free_seats";
    private static final String OCCUPIED_SEATS = "occupied_seats";
    private static final String ESTIMATE = "seatestimate";
    private static final String TIMESTAMP = "timestamp";
    private static final String DATE = "date";

    public static List<LearningPlaceOccupation> parse(@NonNull String jsonString) throws JSONException {

        List<LearningPlaceOccupation> result = new ArrayList<>();

        JSONArray root = new JSONArray(jsonString);
        JSONObject location = root.getJSONObject(0).getJSONObject(ESTIMATE);

        JSONArray items = location.names();
        for (int index = 0; index < items.length(); index++) {
            try {
                JSONArray jsonEntry = location.getJSONArray(items.get(index).toString());
                if (jsonEntry.length() > 0) {
                    LearningPlaceOccupation item = parseJsonObject(jsonEntry.getJSONObject(0));
                    result.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static LearningPlaceOccupation parseJsonObject(JSONObject object) throws JSONException, ParseException {
        LearningPlaceOccupation item = new LearningPlaceOccupation();

        JSONObject jsonEntry = object.getJSONObject(TIMESTAMP);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);

        item.freeSeats = object.getInt(FREE_SEATS);
        item.occupiedSeats = object.getInt(OCCUPIED_SEATS);
        item.location = object.getString(LOCATION);
        item.updatedAt = format.parse(jsonEntry.getString(DATE));

        return item;
    }
}