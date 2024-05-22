package exception.handler;

import domain.ServiceRequest;
import exception.GlobalException;
import exception.base.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author hxy
 * @date 2024/5/21
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 自定义业务异常
     */
    @ExceptionHandler(BaseException.class)
    public ServiceRequest<Void> handleBaseException(BaseException e, HttpServletRequest request) {
        log.error(e.getMessage());
        return ServiceRequest.fail(e.getMessage());
    }
    /**
     * 系统异常
     */
    @ExceptionHandler(GlobalException.class)
    public ServiceRequest<Void> handleException(GlobalException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return ServiceRequest.fail(e.getMessage());
    }
    /**
     * 拦截未知的系统异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ServiceRequest<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return ServiceRequest.fail(e.getMessage());
    }
    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public ServiceRequest<Void> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return ServiceRequest.fail(e.getMessage());
    }


}
