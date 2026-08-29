package cn.charlotte.pit.data.serializer;

import cn.charlotte.pit.data.sub.PlayerTrash;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class TrashSerializer extends JsonSerializer<PlayerTrash> {

    @Override
    public void serialize(PlayerTrash value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
            gen.writeStartObject();
            gen.writeStringField("content", value.serialize());
            gen.writeStringField("backup", value.getBackup());
            gen.writeNumberField("lastCleanTime", value.getLastCleanTime());
            gen.writeEndObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
