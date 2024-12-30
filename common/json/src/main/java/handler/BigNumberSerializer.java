package handler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;

import java.io.IOException;

/**
 * 超出 JS 最大最小值 处理
 *
 * @author hxy
 * @date 2024/12/26
 **/
@JacksonStdImpl
public class BigNumberSerializer extends NumberSerializer {
    public static final BigNumberSerializer BIG_NUMBER_SERIALIZER = new BigNumberSerializer(Number.class);
    /**
     * 根据 JS Number.MAX_SAFE_INTEGER 与 Number.MIN_SAFE_INTEGER 得来
     */
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;
    private static final long MIN_SAFE_INTEGER = -9007199254740991L;

    public BigNumberSerializer(Class<? extends Number> numberClass) {
        super(numberClass);
    }

    @Override
    public void serialize(Number value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (value.longValue() > MAX_SAFE_INTEGER || value.longValue() < MIN_SAFE_INTEGER) {
            super.serialize(value, g, provider);
        } else {
            g.writeString(value.toString());
        }
    }
}
