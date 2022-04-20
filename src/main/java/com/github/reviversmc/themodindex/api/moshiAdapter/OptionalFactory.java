package com.github.reviversmc.themodindex.api.moshiAdapter;

import com.squareup.moshi.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;

/**
 * Completely stolen from <a href="https://github.com/square/moshi/issues/1329#issuecomment-812301954">https://github.com/square/moshi/issues/1329#issuecomment-812301954</a>
 */
public final class OptionalFactory implements JsonAdapter.Factory {

    @Override
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        if (!annotations.isEmpty()) return null;
        if (!(type instanceof ParameterizedType)) return null;

        Class<?> rawType = Types.getRawType(type);
        if (rawType != Optional.class) return null;

        Type optionalType = ((ParameterizedType) type).getActualTypeArguments()[0];

        JsonAdapter<?> optionalTypeAdapter = moshi.adapter(optionalType).nullSafe();

        return new OptionalJsonAdapter<>(optionalTypeAdapter);
    }

    private static class OptionalJsonAdapter<T> extends JsonAdapter<Optional<T>> {

        private final JsonAdapter<T> optionalTypeAdapter;

        public OptionalJsonAdapter(JsonAdapter<T> optionalTypeAdapter) {
            this.optionalTypeAdapter = optionalTypeAdapter;
        }

        @Override
        public Optional<T> fromJson(JsonReader reader) throws IOException {
            T instance = optionalTypeAdapter.fromJson(reader);
            return Optional.ofNullable(instance);
        }

        @Override
        public void toJson(JsonWriter writer, Optional<T> value) throws IOException {
            if (value.isPresent()) {
                optionalTypeAdapter.toJson(writer, value.get());
            } else {
                writer.nullValue();
            }
        }
    }
}
