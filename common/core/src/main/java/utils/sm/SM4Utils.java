package utils.sm;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class SM4Utils
{
    public static final String secretKey = "11HDESaAhibbugDz";
    private String iv = "";
    private boolean hexString = false;

    private Encoder encoder = Base64.getEncoder();
    private Decoder decoder = Base64.getDecoder();

    public SM4Utils()
    {
    }


    //换为32位十六进制字符串
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toUpperCase(); // 转换为大写
    }

    // 生成随机对称密钥
    public static byte[] generateSM4Key() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[16];
        secureRandom.nextBytes(key);
        return key;
    }
}
