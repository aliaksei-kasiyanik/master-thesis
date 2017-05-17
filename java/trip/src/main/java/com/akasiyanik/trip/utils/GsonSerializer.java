package com.akasiyanik.trip.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author akasiyanik
 *         5/11/17
 */
public class GsonSerializer {

    public static <T> String serialize(List<T> routes) {
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        return gson.toJson(routes, listType);
    }

    public static <T> List<T> deserialize(String json, Class<T> typeClass) {
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        return gson.fromJson(json, new ListOfJson<T>(typeClass));
    }

    private static class ListOfJson<T> implements ParameterizedType {
        private Class<?> wrapped;

        public ListOfJson(Class<T> wrapper) {
            this.wrapped = wrapper;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{wrapped};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
