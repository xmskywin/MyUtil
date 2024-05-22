package config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 程序注解配置
 *
 * @author hxy
 * @date 2024/5/21
 **/
@AutoConfiguration
@EnableAspectJAutoProxy(exposeProxy = true)
public class ApplicationConfig {
}
