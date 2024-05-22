package domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author hxy
 * @date 2024/5/21
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest<T>  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 成功
     */
    public static final int SUCCESS = 200;
    /**
     * 失败
     */
    public static final int FAIL = 500;

    private int code;

    private String msg;

    private T data;
    public static <T> ServiceRequest<T> ok() {
        return restResult(null, SUCCESS, "操作成功");
    }

    public static <T> ServiceRequest<T> ok(T data) {
        return restResult(data, SUCCESS, "操作成功");
    }

    public static <T> ServiceRequest<T> ok(String msg) {
        return restResult(null, SUCCESS, msg);
    }

    public static <T> ServiceRequest<T> ok(String msg, T data) {
        return restResult(data, SUCCESS, msg);
    }

    public static <T> ServiceRequest<T> fail() {
        return restResult(null, FAIL, "操作失败");
    }

    public static <T> ServiceRequest<T> fail(String msg) {
        return restResult(null, FAIL, msg);
    }

    public static <T> ServiceRequest<T> fail(T data) {
        return restResult(data, FAIL, "操作失败");
    }

    public static <T> ServiceRequest<T> fail(String msg, T data) {
        return restResult(data, FAIL, msg);
    }

    public static <T> ServiceRequest<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }
    private static <T> ServiceRequest<T> restResult(T data, int code, String msg) {
        ServiceRequest<T> r = new ServiceRequest<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }

}
