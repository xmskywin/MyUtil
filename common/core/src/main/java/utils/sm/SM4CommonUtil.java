package utils.sm;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * 一个SM4加解密工具类，没什么特别
 */
public final class SM4CommonUtil {

    static final String SM4_STRING = "SM4";
    private static final Logger logger = LoggerFactory.getLogger(SM4CommonUtil.class);
    private static final String TRANSFORMATION = "SM4/ECB/PKCS5Padding";
    static SymmetricCrypto sm4 = SmUtil.sm4();

    /**
     * SM4加密
     *
     * @param secretKey
     * @param data
     * @return
     */
    public static String commonEncode(String secretKey, String data) {
        if (secretKey == null || StringUtils.isEmpty(secretKey)) {
            throw new RuntimeException("secretKey不能为空");
        }
        if (secretKey.length() != 32) { // 32个十六进制字符 = 16字节
            throw new RuntimeException("secretKey 数据有误");
        }
        if (data == null || StringUtils.isEmpty(data)) {
            throw new RuntimeException("需加密数据不能为空");
        }

        try {
            // 将16进制秘钥转换为字节数组
            byte[] keyBytes = hexStringToByteArray(secretKey);

            // 初始化密钥和Cipher
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, SM4_STRING);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            // 将数据转换为字节数组
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

            // 执行加密
            byte[] encryptedData = cipher.doFinal(dataBytes);

            // 将加密后的字节数组转换为Base64编码的字符串
            return Base64.encode(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    /**
     * SM4加密
     *
     * @param secretKey
     * @param data
     * @return
     */
    public static String commonEncode(String secretKey, byte[] data) {
        if (StringUtils.isBlank(secretKey)) {
            throw new RuntimeException("secretKey不能为空");
        }
        if (secretKey.length() < 16) {
            throw new RuntimeException("secretKey 数据有误");
        }
        if (data == null || data.length == 0) {
            throw new RuntimeException("需加密数据不能为空");
        }
        String sk = secretKey.substring(0, 16);
//        SymmetricCrypto sm4 = SmUtil.sm4(sk.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec secretKeySpec = new SecretKeySpec(sk.getBytes(StandardCharsets.UTF_8), SM4_STRING);
        sm4.init(SM4_STRING, secretKeySpec);
        // 加密
        byte[] encrypt = sm4.encrypt(data);

        String res = Base64.encode(encrypt);
        if (StringUtils.isBlank(res)) {
            throw new RuntimeException("加密失败secretKey:" + secretKey + " -- " + data.length);
        }
        return res;
    }

    /**
     * SM4解密
     *
     * @param secretKey
     * @param data
     * @return
     */
    public static String commonDecode(String secretKey, String data) {
        if (StringUtils.isBlank(secretKey)) {
            logger.error("secretKey不能为空");
            throw new RuntimeException("secretKey不能为空");
        }
        if (secretKey.length() != 32) { // 32个十六进制字符 = 16字节
            logger.error("secretKey数据长度有误");
            throw new RuntimeException("secretKey 数据有误");
        }
        if (StringUtils.isBlank(data)) {
            logger.error("data不能为空");
            throw new RuntimeException("data不能为空");
        }

        try {
            // 将16进制秘钥转换为字节数组
            byte[] keyBytes = hexStringToByteArray(secretKey);

            // 初始化密钥和Cipher
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, SM4_STRING);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            // 将Base64编码的数据转换为字节数组
            byte[] dataBytes = Base64.decode(data);

            // 执行解密
            byte[] decryptedData = cipher.doFinal(dataBytes);

            // 将解密后的字节数组转换为字符串
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("解密失败", e);
        }
    }

    /**
     * SM4解密
     *
     * @param secretKey
     * @param data
     * @return
     */
    public static byte[] commonDecodeByte(String secretKey, String data) {
        if (StringUtils.isBlank(secretKey)) {
            throw new RuntimeException("secretKey不能为空");
        }
        if (secretKey.length() < 16) {
            throw new RuntimeException("secretKey 数据有误");
        }
        if (StringUtils.isBlank(data)) {
            throw new RuntimeException("data不能为空");
        }
        String sk = secretKey;
//        SymmetricCrypto sm4 = SmUtil.sm4(sk.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec secretKeySpec = new SecretKeySpec(sk.getBytes(StandardCharsets.UTF_8), SM4_STRING);
        sm4.init(SM4_STRING, secretKeySpec);

        // 解密
        byte[] bytes = Base64.decode(data);

        byte[] decodeData = sm4.decrypt(bytes);

        if (decodeData == null || decodeData.length == 0) {
            throw new RuntimeException("解密失败，请传输正确信息！");
        }
        return decodeData;
    }

//    public static void main(String[] args) {
//        long l = System.currentTimeMillis();
//        String key = "1234567890123456";
//        String sss = "";
//        String aaa = "";
//
//        sss = SM4CommonUtil.commonEncode(key, "我是一个好人".getBytes(StandardCharsets.UTF_8));
//        aaa = SM4CommonUtil.commonDecode(key, sss);
//        System.out.println(sss);
//        System.out.println(aaa);
//        System.out.println(System.currentTimeMillis() - l);
//        // 密钥，SM4 要求 16 字节长度
//        byte[] keys = "0123456789abcdeffedcba9876".getBytes();
//        SymmetricCrypto sm4 = SmUtil.sm4(key.getBytes());
//        byte[] bbb = sm4.encrypt("我是一个好人");
//        String ccc = sm4.encryptBase64("我是一个好人");
//        System.out.println("加密后的密文（16进制）: " + bytesToHex(bbb));
//        System.out.println("加密后的密文base64: " + ccc);
//        // 解密
//        byte[] decryptResult = sm4.decrypt(sss);
//        byte[] result = sm4.decrypt(ccc);
//        System.out.println("解密后的明文: " + new String(decryptResult));
//        System.out.println("解密后的明文base64: " + new String(result));
//        String eee = SmUtil.sm3("我是一个好人");
//        System.out.println("签名: " + eee);
//    }

    // 辅助方法：将字节数组转换为16进制字符串
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    //将32位十六进制字符串转换为16字节的密钥
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    //base64转为16进制
    public static String base64ToHex(String base64Str) {
        // 解码Base64字符串为字节数组
        byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Str);
        // 将字节数组转换为16进制字符串
        StringBuilder hexStr = new StringBuilder();
        for (byte b : decodedBytes) {
            hexStr.append(String.format("%02x", b));
        }
        // 输出16进制字符串
        return hexStr.toString();
    }

}
