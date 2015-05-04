package com.googlecode.totallylazy.template;

import com.googlecode.totallylazy.Value;

import java.util.Map;


public class Text implements Value<String>, Renderer<Map<String, Object>> {
    private final String value;

    public Text(String value) {
        this.value = value;
    }


    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public <A extends Appendable> A render(Map<String, Object> instance, A appendable) throws Exception {
        appendable.append(value);
        return appendable;
    }
}
