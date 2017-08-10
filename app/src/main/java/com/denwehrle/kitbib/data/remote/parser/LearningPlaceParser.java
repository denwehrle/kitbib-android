package com.denwehrle.kitbib.data.remote.parser;

import android.support.annotation.NonNull;

import com.denwehrle.kitbib.data.model.LearningPlace;
import com.denwehrle.kitbib.utils.MappingUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dennis Wehrle
 */
public class LearningPlaceParser {

    private static final String NAME = "name";
    private static final String URL = "url";
    private static final String GEO_COORDINATES = "geo_coordinates";
    private static final String AVAILABLE_SEATS = "available_seats";
    private static final String LOCATION = "location";

    public static List<LearningPlace> parse(@NonNull String jsonString) throws JSONException {

        List<LearningPlace> result = new ArrayList<>();

        JSONArray root = new JSONArray(jsonString);
        JSONObject location = root.getJSONObject(0).getJSONObject(LOCATION);

        JSONArray items = location.names();
        for (int index = 0; index < items.length(); index++) {
            try {
                JSONArray jsonEntry = location.getJSONArray(items.get(index).toString());
                LearningPlace item = parseJsonObject(jsonEntry.getJSONObject(0));
                result.add(item);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    private static LearningPlace parseJsonObject(JSONObject object) throws JSONException {
        LearningPlace item = new LearningPlace();

        item.availableSeats = object.getString(AVAILABLE_SEATS);
        item.geoCoordinates = object.getString(GEO_COORDINATES);
        item.url = object.getString(URL);
        item.name = object.getString(NAME);
        item.longName = MappingUtils.getLongName.get(object.getString(NAME));
        item.location = MappingUtils.getLocation.get(object.getString(NAME));
        item.index = MappingUtils.getIndex.get(object.getString(NAME));

        return item;
    }
}