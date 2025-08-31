package dev.araopj.hrplatformapi.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class JsonRedactor {

    private final String REDACTED_VALUE = "****";
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Redacts specified fields from the given object and returns a JsonNode.
     *
     * @param data           The input object (DTO, entity, map, etc.).
     * @param fieldsToRedact The set of field names to redact.
     * @return A JsonNode with sensitive fields redacted.
     */
    public JsonNode redact(Object data, Set<String> fieldsToRedact) {
        return redactNode(mapper.valueToTree(data), fieldsToRedact);
    }

    private JsonNode redactNode(JsonNode node, Set<String> fieldsToRedact) {
        if (node.isObject()) {
            var objectNode = (ObjectNode) node;
            for (var entry : objectNode.properties()) {
                var fieldName = entry.getKey();
                var child = entry.getValue();

                if (fieldsToRedact.contains(fieldName)) {
                    objectNode.put(fieldName, REDACTED_VALUE);
                } else {
                    redactNode(child, fieldsToRedact);
                }
            }
        } else if (node.isArray()) {
            for (var child : node) {
                redactNode(child, fieldsToRedact);
            }
        }
        return node;
    }

}
