package com.github.codetanzania.model.adapter;

import com.activeandroid.serializer.TypeSerializer;
import com.github.codetanzania.model.LongLat;


public class LongLatSerializer extends TypeSerializer {

    @Override
    public Class<?> getDeserializedType() {
        return LongLat.class;
    }

    @Override
    public Class<?> getSerializedType() {
        return (new Double[2]).getClass().getComponentType();
    }

    @Override
    public Object serialize(Object data) {
        if (data == null) { return null; }
        LongLat longLat = (LongLat) data;
        return new Double[]{ longLat.getLongitudes(), longLat.getLatitudes() };
    }

    @Override
    public Object deserialize(Object data) {
        if (data == null) { return null; }
        Double[] d = (Double[])data;
        return new LongLat(d[0], d[1]);
    }
}
