package config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import handler.BigNumberSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author hxy
 * @date 2024/12/26
 **/
@AutoConfiguration(before = JacksonAutoConfiguration.class)
@Slf4j
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return new Jackson2ObjectMapperBuilderCustomizer() {
            @Override
            public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
                JavaTimeModule javaTimeModule = new JavaTimeModule();
                javaTimeModule.addSerializer(Long.class, BigNumberSerializer.BIG_NUMBER_SERIALIZER);
                javaTimeModule.addSerializer(Long.TYPE, BigNumberSerializer.BIG_NUMBER_SERIALIZER);
                javaTimeModule.addSerializer(BigInteger.class, BigNumberSerializer.BIG_NUMBER_SERIALIZER);
                javaTimeModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
                javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
                jacksonObjectMapperBuilder.modulesToInstall(javaTimeModule);
                jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
            }
        };
    }

    // 反序列化使用驼峰命名（默认）
    @Bean(name = "defaultObjectMapper")
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    // 驼峰/下划线互转
    @Bean(name = "snakeToCamelMapper")
    public ObjectMapper snakeToCamelMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 启用下划线命名策略转换为驼峰命名
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }


}
