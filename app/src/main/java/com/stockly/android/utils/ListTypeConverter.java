package com.stockly.android.utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stockly.android.models.Assets;
import com.stockly.android.models.Positions;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * List type converter that converts an object to string.
 * let it to save to DB and then can convert back to object.
 */
public class ListTypeConverter {

    static Gson gson = new Gson();

    @TypeConverter
    public static List<Positions> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Positions>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String someObjectListToString(List<Positions> assets) {
        return gson.toJson(assets);
    }
}
