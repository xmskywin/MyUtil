package utils;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.aop.framework.AopContext;

/**
 * @author hxy
 * @date 2024/5/21
 **/
public class SpringUtils extends SpringUtil {
    /**
     * 获取aop代理对象,用于启用事务
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }

}
