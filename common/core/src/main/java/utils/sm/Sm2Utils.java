package utils.sm;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * 对象加密算法，国密工具
 *
 * @author zgui
 */
public class Sm2Utils {

    /**
     * 生成公私钥对(默认压缩公钥)
     * @return
     */
//    public static SmKeyPair genKeyPair() {
//        SM2 sm2 = SmUtil.sm2();
//        //这里会自动生成对应的随机秘钥对
//        byte[] privateKey = BCUtil.encodeECPrivateKey(sm2.getPrivateKey());
//        //这里默认公钥压缩  公钥的第一个字节用于表示是否压缩 02或者03表示是压缩公钥,04表示未压缩公钥
//        byte[] publicKey = BCUtil.encodeECPublicKey(sm2.getPublicKey());
//        //这里得到未压缩的公钥/**/
//        byte[] nonCompressedPublicKey = BCUtil.encodeECPublicKey(sm2.getPublicKey(), false);
//        String priKey = HexUtil.encodeHexStr(privateKey);
//        String pubKey = HexUtil.encodeHexStr(publicKey);
//        String xPubKey = HexUtil.encodeHexStr(nonCompressedPublicKey);
//        return new SmKeyPair(priKey, pubKey,xPubKey);
//    }


    /**
     * SM2加密算法
     *
     * @param publicKey 公钥
     * @param text      数据
     * @return
     */
    public static String encrypt(String publicKey, String text) {
        ECPublicKeyParameters ecPublicKeyParameters = null;
        //这里需要根据公钥的长度进行加工
        if (publicKey.length() == 130) {
            //这里需要去掉开始第一个字节 第一个字节表示标记
            publicKey = publicKey.substring(2);
            String xhex = publicKey.substring(0, 64);
            String yhex = publicKey.substring(64, 128);
            ecPublicKeyParameters = BCUtil.toSm2Params(xhex, yhex);
        } else {
            PublicKey p = BCUtil.decodeECPoint(publicKey, SmUtil.SM2_CURVE_NAME);
            ecPublicKeyParameters = BCUtil.toParams(p);
        }
        //创建sm2 对象
        SM2 sm2 = new SM2(null, ecPublicKeyParameters);
        // 公钥加密
        return sm2.encryptBcd(text, KeyType.PublicKey);
    }

    /**
     * SM2加密算法
     *
     * @param publicKey 公钥
     * @param text      明文数据
     * @return
     */
    public static String encrypt(PublicKey publicKey, String text) {
        ECPublicKeyParameters ecPublicKeyParameters = BCUtil.toParams(publicKey);
        //创建sm2 对象
        SM2 sm2 = new SM2(null, ecPublicKeyParameters);
        // 公钥加密
        return sm2.encryptBcd(text, KeyType.PublicKey);
    }


    /**
     * SM2解密算法
     *
     * @param privateKey 私钥
     * @param cipherData 密文数据
     * @return
     */
    public static String decrypt(PrivateKey privateKey, String cipherData) {
        ECPrivateKeyParameters ecPrivateKeyParameters = BCUtil.toParams(privateKey);
        //创建sm2 对象
        SM2 sm2 = new SM2(ecPrivateKeyParameters, null);
        // 私钥解密
        return StrUtil.utf8Str(sm2.decryptFromBcd(cipherData, KeyType.PrivateKey));
    }

    /**
     * 私钥签名
     *
     * @param privateKey 私钥
     * @param content    待签名内容
     * @return
     */
    public static String sign(String privateKey, String content) {
        ECPrivateKeyParameters ecPrivateKeyParameters = BCUtil.toSm2Params(privateKey);
        //创建sm2 对象
        SM2 sm2 = new SM2(ecPrivateKeyParameters, null);
        String sign = sm2.signHex(HexUtil.encodeHexStr(content));
        return sign;
    }

    public static String signBase64(String privateKey, String content) {
        ECPrivateKeyParameters ecPrivateKeyParameters = BCUtil.toSm2Params(privateKey);
        //创建sm2 对象
        SM2 sm2 = new SM2(ecPrivateKeyParameters, null);

        byte[] sign1 = sm2.sign(content.getBytes());
        byte[] encode = Base64.getEncoder().encode(sign1);

        return new String(encode);
    }

    public static String base64ToHex(String base64Str) {
        // 解码Base64字符串为字节数组
        byte[] decodedBytes = Base64.getDecoder().decode(base64Str);
        // 将字节数组转换为16进制字符串
        StringBuilder hexStr = new StringBuilder();
        for (byte b : decodedBytes) {
            hexStr.append(String.format("%02x", b));
        }
        // 输出16进制字符串
        return hexStr.toString();
    }

    /**
     * 验证签名
     *
     * @param publicKey 公钥
     * @param content   待签名内容
     * @param sign      签名值
     * @return
     */
    public static boolean verify(String publicKey, String content, String sign) {
        ECPublicKeyParameters ecPublicKeyParameters = null;
        //这里需要根据公钥的长度进行加工
        if (publicKey.length() == 130) {
            //这里需要去掉开始第一个字节 第一个字节表示标记
            publicKey = publicKey.substring(2);
            String xhex = publicKey.substring(0, 64);
            String yhex = publicKey.substring(64, 128);
            ecPublicKeyParameters = BCUtil.toSm2Params(xhex, yhex);
        } else {
            PublicKey p = BCUtil.decodeECPoint(publicKey, SmUtil.SM2_CURVE_NAME);
            ecPublicKeyParameters = BCUtil.toParams(p);
        }
        //创建sm2 对象
        SM2 sm2 = new SM2(null, ecPublicKeyParameters);

        boolean verify = sm2.verifyHex(HexUtil.encodeHexStr(content), sign);
        return verify;
    }


    // 十六进制字符串转字节数组
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
