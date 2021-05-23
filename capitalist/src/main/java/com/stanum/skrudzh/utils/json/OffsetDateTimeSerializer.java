package com.stanum.skrudzh.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeSerializer extends StdSerializer<OffsetDateTime> {
    private static final long serialVersionUID = 1L;

    private static final String SHORT_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String FULL_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX";
    private static final DateTimeFormatter FULL_FORMAT = DateTimeFormatter.ofPattern(FULL_FORMAT_STRING);
    private static final DateTimeFormatter SHORT_FORMAT = DateTimeFormatter.ofPattern(SHORT_FORMAT_STRING);

    public OffsetDateTimeSerializer() {
        this(null);
    }

    public OffsetDateTimeSerializer(Class<OffsetDateTime> vc) {
        super(vc);
    }


    @Override
    public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeObject( FULL_FORMAT.format(value));
    }
}