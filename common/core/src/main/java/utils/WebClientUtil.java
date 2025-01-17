package utils;

import exception.HttpCallException;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Web 客户端实用程序
 *
 * @author xcm
 * @date 2024/08/20
 */

public class WebClientUtil {
    private static final Logger log = LoggerFactory.getLogger(WebClientUtil.class);

    private static final WebClient WEB_CLIENT;

    static {
        try {
            SslContext sslContext = SslContextBuilder.forClient()
                // 绕过认证
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

            HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

            WEB_CLIENT = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        } catch (SSLException e) {
            throw new RuntimeException("Failed to create SSL context for WebClient", e);
        }
    }

    /**
     * 发起GET请求，支持Get parameter
     */
    public static String getParam(String url, HttpHeaders headers, MultiValueMap<String, String> queryParams) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        try {
            HttpHeaders finalHeaders = headers;
            return WEB_CLIENT.get()
                .uri(uriBuilder -> uriBuilder.path(url).queryParams(queryParams).build())
                .headers(httpHeaders -> httpHeaders.putAll(finalHeaders))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block(); // 同步调用
        } catch (WebClientResponseException e) {
            // 处理HTTP错误状态异常
            setLog(url);
            throw new exception.HttpCallException("HTTP error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            // 处理其他异常
            setLog(url);
            throw new HttpCallException("Request failed: " + e.getMessage(), e);
        }
    }

    /**
     * 发起GET请求，不带参数
     */
    public static String getNoParam(String url, HttpHeaders headers) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        try {
            HttpHeaders finalHeaders = headers;
            return WEB_CLIENT.get()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.putAll(finalHeaders))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block(); // 同步调用
        } catch (WebClientResponseException e) {
            // 处理HTTP错误状态异常
            setLog(url);
            throw new HttpCallException("HTTP error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            // 处理其他异常
            setLog(url);
            throw new HttpCallException("Request failed: " + e.getMessage(), e);
        }
    }


    /**
     * 发起POST请求，支持JSON body
     */
    public static String postJson(String url, Map<String, String> headers, Object body) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        try {
            Map<String, String> finalHeaders = headers;
            return WEB_CLIENT.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(header -> finalHeaders.forEach(header::add))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block(); // 同步调用
        } catch (WebClientResponseException e) {
            // 处理HTTP错误状态异常 e.getResponseBodyAsString()
            setLog(url);
            throw new HttpCallException("HTTP error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            // 处理其他异常
            setLog(url);
            throw new HttpCallException("Request failed: " + e.getMessage(), e);
        }
    }

    /**
     * 发起POST请求，支持表单数据
     */
    public static String postForm(String url, Map<String, String> headers, MultiValueMap<String, String> formData) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        try {
            Map<String, String> finalHeaders = headers;
            return WEB_CLIENT.post()
                .uri(url)
                .headers(header -> finalHeaders.forEach(header::add))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block(); // 同步调用
        } catch (WebClientResponseException e) {
            // 处理HTTP错误状态异常
            setLog(url);
            throw new HttpCallException("HTTP error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            // 处理其他异常
            setLog(url);
            throw new HttpCallException("Request failed: " + e.getMessage(), e);
        }
    }

    /**
     * 发起POST请求，支持XML格式参数
     */
    public static String postXml(String url, Map<String, String> headers, String xmlData) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        try {
            Map<String, String> finalHeaders = headers;
            return WEB_CLIENT.post()
                .uri(url)
                .headers(header -> finalHeaders.forEach(header::add))
                .contentType(MediaType.APPLICATION_XML)
                .body(BodyInserters.fromValue(xmlData))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block(); // 同步调用
        } catch (WebClientResponseException e) {
            // 处理HTTP错误状态异常
            setLog(url);
            throw new HttpCallException("HTTP error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            // 处理其他异常
            setLog(url);
            throw new HttpCallException("Request failed: " + e.getMessage(), e);
        }
    }

    /**
     * 纯文本post请求
     *
     * @param url      路径
     * @param headers  请求头
     * @param textBody 请求体
     * @return 返回数据
     */
    public static String postText(String url, Map<String, String> headers, String textBody) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        try {
            Map<String, String> finalHeaders = headers;
            return WEB_CLIENT.post()
                .uri(url)
                .headers(header -> finalHeaders.forEach(header::add))
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(textBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block();
        } catch (WebClientResponseException e) {
            // 处理HTTP错误状态异常
            setLog(url);
            throw new HttpCallException("HTTP error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            setLog(url);
            throw new HttpCallException("Request failed: " + e.getMessage(), e);
        }
    }

    private static void setLog(String url) {
        log.error("{} 接口调用异常", url);
    }

    /**
     * 发起GET请求，支持JSON格式的请求体
     *
     * @param url      请求地址
     * @param headers  请求头
     * @param jsonBody JSON格式请求体
     * @return 响应内容
     */
    public static String getWithJsonBody(String url, HttpHeaders headers, String jsonBody) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        try {
            HttpHeaders finalHeaders = headers;

            return WEB_CLIENT.method(HttpMethod.GET)
                .uri(url)
                .headers(httpHeaders -> httpHeaders.putAll(finalHeaders))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block(); // 同步调用
        } catch (WebClientResponseException e) {
            // 处理HTTP错误状态异常
            setLog(url);
            throw new HttpCallException("HTTP error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            // 处理其他异常
            setLog(url);
            throw new HttpCallException("Request failed: " + e.getMessage(), e);
        }
    }

    /**
     * 发起GET请求，支持Get parameter，完整路径
     *
     * @param url
     * @param headers
     * @param queryParams
     * @return
     */
    public static String getParamUrl(String url, HttpHeaders headers, MultiValueMap<String, String> queryParams) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        try {
            HttpHeaders finalHeaders = headers;
            // 直接构建 URI，包含完整 URL 和查询参数
            URI fullUri = buildUriWithQueryParams(url, queryParams);
            log.debug("完整路径: {}", fullUri);
            return WEB_CLIENT.get()
                .uri(fullUri) // 直接传递完整 URI
                .headers(httpHeaders -> httpHeaders.putAll(finalHeaders))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block(); // 同步调用
        } catch (WebClientResponseException e) {
            // 处理 HTTP 错误状态异常
            setLog(url);
            throw new HttpCallException("HTTP error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            // 处理其他异常
            setLog(url);
            throw new HttpCallException("Request failed: " + e.getMessage(), e);
        }
    }

    public static String getParamUrlWithPathParam(String url, HttpHeaders headers, String pathParam) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        try {
            HttpHeaders finalHeaders = headers;
            // 构建包含路径参数的完整 URI
            URI fullUri = buildUriWithPathParam(url, pathParam);
            log.debug("完整路径: {}", fullUri);
            return WEB_CLIENT.get()
                .uri(fullUri) // 直接传递完整 URI
                .headers(httpHeaders -> httpHeaders.putAll(finalHeaders))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block(); // 同步调用
        } catch (WebClientResponseException e) {
            // 处理 HTTP 错误状态异常
            setLog(url);
            throw new HttpCallException("HTTP error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            // 处理其他异常
            setLog(url);
            throw new HttpCallException("Request failed: " + e.getMessage(), e);
        }
    }

    /**
     * 构建包含查询参数的完整 URI
     */
    private static URI buildUriWithQueryParams(String baseUrl, MultiValueMap<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return URI.create(baseUrl);
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParams(queryParams);
        return builder.build().encode().toUri();
    }

    /**
     * 构建包含路径参数的完整 URI
     */
    private static URI buildUriWithPathParam(String baseUrl, String pathParam) {
        if (pathParam == null || pathParam.isEmpty()) {
            return URI.create(baseUrl); // 如果 pathParam 为空，则返回基础 URL
        }

        // 拼接路径参数
        String fullUrl = baseUrl.endsWith("/") ? baseUrl + pathParam : baseUrl + "/" + pathParam;
        return URI.create(fullUrl);
    }

}
