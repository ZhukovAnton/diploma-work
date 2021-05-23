package com.stanum.skrudzh.utils.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
public class OffsetDateTimeDeserializer extends StdDeserializer<OffsetDateTime> {
    private static final long serialVersionUID = 1L;

    private static final String SHORT_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String FULL_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX";
    private static final DateTimeFormatter FULL_FORMAT = DateTimeFormatter.ofPattern(FULL_FORMAT_STRING);
    private static final DateTimeFormatter SHORT_FORMAT = DateTimeFormatter.ofPattern(SHORT_FORMAT_STRING);

    public OffsetDateTimeDeserializer() {
        this(null);
    }

    public OffsetDateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public OffsetDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        final String date = node.textValue();

        try {
            return OffsetDateTime.parse(date, FULL_FORMAT);
        } catch (Exception e) {
            try {
                log.warn("Error while parsing date {} in format {}", date, FULL_FORMAT_STRING);
                LocalDateTime ldateTime = LocalDateTime.parse(date, SHORT_FORMAT);
                ZoneOffset offset = OffsetDateTime.now().getOffset();
                return ldateTime.atOffset(offset);
            } catch (Exception ex) {
                log.warn("Error while parsing date {} in format {}", date, SHORT_FORMAT_STRING);
            }
        }
        throw new JsonParseException(jp, "Unparseable date: \"" + date + "\".");
    }
}
