package utils.sm;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import utils.JsonUtils;

import java.util.Map;


public class SM4_Base64 {
    @Resource
    private JsonUtils jsonUtils;

    /**
     * 对动态码进行加密并进行Base64编码
     *
     * @param dynamicCodeMap 生成动态码的集合
     * @param key            配置中心SM4.secKey的值（32位十六进制的SM4对称密钥）
     * @return
     */
    public String encode(Map dynamicCodeMap, String key) {
        String SM4String = null;
        try {
            SM4String = SM4CommonUtil.commonEncode(key, jsonUtils.toJsonString(dynamicCodeMap));
        } catch (Exception e) {
            //加密失败返回空
            return null;
        }
        return SM4String;
    }

    /**
     * 解密动态码
     *
     * @param cipherText 动态码密文
     * @param key        配置中心SM4.secKey的值（32位十六进制的SM4对称密钥）
     * @return
     */
    public JsonNode decode(String cipherText, String key) {
        try {
            String decData = SM4CommonUtil.commonDecode(key, cipherText);
            return jsonUtils.readTree(decData);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 如果解密失败，返回 null
        }
    }
}
