package utils;

import cn.hutool.core.lang.Dict;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author hxy
 * @date 2024/12/27
 **/
@Component
@RequiredArgsConstructor
public class JsonUtils {
    // 用于不同的转换类型
    public static final int CAMEL_AND_SNAKE = 1;
    public static final int NORMAL = 3;
    @Resource(name = "defaultObjectMapper")
    private final ObjectMapper camelCaseMapper;
    @Resource(name = "snakeToCamelMapper")
    private final ObjectMapper snakeAndCamelMapper;

    public Dict parseMap(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            return camelCaseMapper.readValue(text, camelCaseMapper.getTypeFactory().constructType(Dict.class));
        } catch (MismatchedInputException e) {
            // 类型不匹配说明不是json
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Dict> parseArrayMap(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            return camelCaseMapper.readValue(text, camelCaseMapper.getTypeFactory().constructCollectionType(List.class, Dict.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 默认的转换方法（不做命名转换）
    public String toJsonString(Object obj) {
        return toJsonString(obj, NORMAL);
    }

    // 根据转换类型进行转换的toJson方法
    public String toJsonString(Object obj, int conversionType) {
        try {
            return switch (conversionType) {
                case CAMEL_AND_SNAKE -> snakeAndCamelMapper.writeValueAsString(obj);
                case NORMAL -> camelCaseMapper.writeValueAsString(obj);
                default -> throw new IllegalArgumentException("Unknown conversion type: " + conversionType);
            };
        } catch (JsonProcessingException e) {
            throw new RuntimeException("toJson异常: " + e.getMessage(), e);
        }
    }

    // 默认的反序列化方法（不做命名转换）
    public <T> T parseObjectfromJson(String json, Class<T> clazz) {
        return parseObjectfromJson(json, clazz, NORMAL);
    }

    // 根据转换类型进行转换的fromJson方法
    public <T> T parseObjectfromJson(String json, Class<T> clazz, int conversionType) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return switch (conversionType) {
                case CAMEL_AND_SNAKE -> snakeAndCamelMapper.readValue(json, clazz);
                case NORMAL -> camelCaseMapper.readValue(json, clazz);
                default -> throw new IllegalArgumentException("Unknown conversion type: " + conversionType);
            };
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json反序列化异常: " + e.getMessage(), e);
        }
    }

    // JsonNode -> List<T>
    public <T> List<T> fromJsonNodeToList(JsonNode node, Class<T> clazz, int conversionType) {
        if (node == null || !node.isArray()) {
            return Collections.emptyList();
        }
        try {
            CollectionType listType;
            return switch (conversionType) {
                case CAMEL_AND_SNAKE -> {
                    listType = snakeAndCamelMapper.getTypeFactory().constructCollectionType(List.class, clazz);
                    yield snakeAndCamelMapper.convertValue(node, listType);
                }
                case NORMAL -> {
                    listType = camelCaseMapper.getTypeFactory().constructCollectionType(List.class, clazz);
                    yield camelCaseMapper.convertValue(node, listType);
                }
                default -> throw new IllegalArgumentException("Unknown conversion type: " + conversionType);
            };
        } catch (Exception e) {
            throw new RuntimeException("JsonNode反序列化List异常: " + e.getMessage(), e);
        }
    }

    // 封装 readTree 方法，避免重复的 try-catch
    public JsonNode readTree(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return camelCaseMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json转换JsonNode异常: " + e.getMessage(), e);
        }
    }

    public <T> List<T> fromJsonList(String jsonStr, Class<T> clazz, int conversionType) {
        if (StringUtils.isBlank(jsonStr)) {
            return Collections.emptyList();
        }
        try {
            CollectionType listType;
            return switch (conversionType) {
                case CAMEL_AND_SNAKE -> {
                    listType = snakeAndCamelMapper.getTypeFactory().constructCollectionType(List.class, clazz);
                    yield snakeAndCamelMapper.readValue(jsonStr, listType);
                }
                case NORMAL -> {
                    listType = camelCaseMapper.getTypeFactory().constructCollectionType(List.class, clazz);
                    yield camelCaseMapper.readValue(jsonStr, listType);
                }
                default -> throw new IllegalArgumentException("Unknown conversion type: " + conversionType);
            };
        } catch (Exception e) {
            throw new RuntimeException("JsonList反序列化异常: " + e.getMessage(), e);
        }
    }
}
