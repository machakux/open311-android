package com.github.codetanzania.api.adapter;

import com.activeandroid.serializer.TypeSerializer;

import java.util.Date;

public final class UtilDateSerializer extends TypeSerializer {

    @Override
    public Class<?> getDeserializedType() {
        return Date.class;
    }

    @Override
    public Class<?> getSerializedType() {
        return Long.class;
    }

    @Override
    public Object serialize(Object data) {
        return data == null ? null : ((Date) data).getTime();
    }

    @Override
    public Object deserialize(Object data) {
        return data == null ? null : new Date((Long) data);
    }
}
