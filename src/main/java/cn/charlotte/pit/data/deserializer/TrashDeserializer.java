package cn.charlotte.pit.data.deserializer;

import cn.charlotte.pit.data.sub.PlayerTrash;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class TrashDeserializer extends JsonDeserializer<PlayerTrash> {

    @Override
    public PlayerTrash deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            JsonNode node = p.getCodec().readTree(p);
            String content = node.hasNonNull("content") ? node.get("content").asText() : "";
            String backup = node.hasNonNull("backup") ? node.get("backup").asText() : "";
            long lastCleanTime = node.hasNonNull("lastCleanTime") ? node.get("lastCleanTime").asLong() : 0L;
            return PlayerTrash.deserialization(content, backup, lastCleanTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new PlayerTrash();
    }
}
