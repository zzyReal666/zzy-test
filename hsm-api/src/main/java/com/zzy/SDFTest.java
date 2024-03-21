package com.zzy;

import java.util.Random;

import com.zayk.sdf.api.sdk.ZaykKey;
import com.zayk.sdf.api.sdk.ZaykSDF;
import com.zayk.sdf.api.share.Arrays;
import com.zayk.sdf.api.struct.ECCCipher;
import com.zayk.sdf.api.struct.ECCSignature;
import com.zayk.sdf.api.struct.ECCrefPrivateKey;
import com.zayk.sdf.api.struct.ECCrefPublicKey;
import com.zayk.sdf.api.struct.RSArefPrivateKey;
import com.zayk.sdf.api.struct.RSArefPublicKey;
import com.zayk.util.encoders.Hex;

import com.zayk.sdf.api.provider.ZaykJceGlobal;

public class SDFTest {

	public static void main(String[] args) throws Exception {
		ECCrefPublicKey publicKey = new ECCrefPublicKey();
		ECCrefPrivateKey privateKey = new ECCrefPrivateKey();

		// ZaykSDF sdf = ZaykSDF.getInstance();//读配置文件

		ZaykSDF sdf = ZaykSDF.getInstance("192.168.7.112", "13556", 1); // ip，端口，连接池大小

		// 随机数测试
		System.out.println();
		System.out.println("**********随机数测试****************");
		byte[] randomData = sdf.SDF_GenerateRandom(16);
		if (randomData == null) {
			System.out.println("---------->SDF_GenerateRandom FAIL");
		} else {
			System.out.println("===========>SDF_GenerateRandom OK");
		}

		// 对称测试
		System.out.println();
		System.out.println("**********对称测试****************");
		// 不带补丁的加密 数据长度要求为密钥长度的整数倍
		byte[] encData = sdf.SDF_Encrypt_Ex(1, ZaykJceGlobal.SGD_SMS4_ECB, "1234567812345678".getBytes(), null,
				"1234567812345678".getBytes(), false);
		if (encData == null) {
			System.out.println("---------->SDF_Encrypt_Ex FAIL");
		} else {
			System.out.println("===========>SDF_Encrypt_Ex OK");
		}
		// 不带补丁的解密 数据长度要求为密钥长度的整数倍
		byte[] decData = sdf.SDF_Decrypt_Ex(1, ZaykJceGlobal.SGD_SMS4_ECB, "1234567812345678".getBytes(), null, encData,
				false);
		if (decData == null) {
			System.out.println("---------->SDF_Decrypt_Ex FAIL");
		} else {
			if ("1234567812345678".equals(new String(decData))) {
				System.out.println("===========>SDF_Decrypt_Ex OK");
			} else {
				System.out.println("---------->SDF_Decrypt_Ex FAIL");
			}
		}

		// 带补丁的加密 数据长度不要求为密钥长度的整数倍
		encData = sdf.SDF_Encrypt_Ex(1, ZaykJceGlobal.SGD_SMS4_ECB, "1234567812345678".getBytes(), null,
				"123".getBytes(), true);
		if (encData == null) {
			System.out.println("---------->SDF_Encrypt_Ex FAIL");
		} else {
			System.out.println("===========>SDF_Encrypt_Ex OK");
		}
		// 带补丁的解密 数据长度不要求为密钥长度的整数倍
		decData = sdf.SDF_Decrypt_Ex(1, ZaykJceGlobal.SGD_SMS4_ECB, "1234567812345678".getBytes(), null, encData, true);
		if (decData == null) {
			System.out.println("---------->SDF_Decrypt_Ex FAIL");
		} else {
			if ("123".equals(new String(decData))) {
				System.out.println("===========>SDF_Decrypt_Ex OK");
			} else {
				System.out.println("---------->SDF_Decrypt_Ex FAIL");
			}
		}

		ZaykKey keyHandle = new ZaykKey();
		byte[] encKey = new byte[16];
		boolean rv = sdf.SDF_GenerateKeyWithKEK(128, ZaykJceGlobal.SGD_SMS4_ECB, 1, encKey, keyHandle);
		if (rv) {
			System.out.println("===========>SDF_GenerateKeyWithKEK OK");
		} else {
			System.out.println("---------->SDF_GenerateKeyWithKEK FAIL");
		}
		byte[] cmac = sdf.SDF_CalculateMAC(keyHandle, ZaykJceGlobal.SGD_SMS4_MAC, "1234567812345678".getBytes(),
				"1234567812345678".getBytes());
		if (cmac == null) {
			System.out.println("---------->SDF_CalculateMAC FAIL");
		} else {
			System.out.println("===========>SDF_CalculateMAC OK: " + Hex.toHexString(cmac));
		}

		// 不带补丁的加密 数据长度要求为密钥长度的整数倍
		encData = sdf.SDF_Encrypt(keyHandle, ZaykJceGlobal.SGD_SMS4_ECB, "1234567812345678".getBytes(),
				"1234567812345678".getBytes());
		if (encData == null) {
			System.out.println("---------->SDF_Encrypt FAIL");
		} else {
			System.out.println("===========>SDF_Encrypt OK");
		}
		// 不带补丁的解密 数据长度要求为密钥长度的整数倍
		decData = sdf.SDF_Decrypt(keyHandle, ZaykJceGlobal.SGD_SMS4_ECB, "1234567812345678".getBytes(), encData);
		if (decData == null) {
			System.out.println("---------->SDF_Decrypt FAIL");
		} else {
			if ("1234567812345678".equals(new String(decData))) {
				System.out.println("===========>SDF_Decrypt OK");
			} else {
				System.out.println("---------->SDF_Decrypt FAIL");
			}
		}

		// 非对称测试
		System.out.println();
		System.out.println("**********SM2 运算****************");
		rv = sdf.SDF_GenerateKeyPair_ECC(ZaykJceGlobal.SGD_SM2_1, 256, publicKey, privateKey);
		if (!rv) {
			System.out.println("---------->SDF_GenerateKeyPair_ECC FAIL");
		} else {
			System.out.println("===========>SDF_GenerateKeyPair_ECC OK");
		}

		ECCSignature signature = sdf.SDF_InternalSign_ECC(1, "12341234123412341234123412341234".getBytes());
		if (signature == null) {
			System.out.println("---------->SDF_InternalSign_ECC FAIL");
		} else {
			System.out.println("===========>SDF_InternalSign_ECC OK");
		}

		rv = sdf.SDF_InternalVerify_ECC(1, "12341234123412341234123412341234".getBytes(), signature);
		if (!rv) {
			System.out.println("---------->SDF_InternalVerify_ECC FAIL");
		} else {
			System.out.println("===========>SDF_InternalVerify_ECC OK");
		}

		signature = sdf.SDF_ExternalSign_ECC(ZaykJceGlobal.SGD_SM2_1, privateKey,
				"12341234123412341234123412341234".getBytes());
		if (signature == null) {
			System.out.println("---------->SDF_ExternalSign_ECC FAIL");
		} else {
			System.out.println("===========>SDF_ExternalSign_ECC OK");
		}

		rv = sdf.SDF_ExternalVerify_ECC(ZaykJceGlobal.SGD_SM2_1, publicKey,
				"12341234123412341234123412341234".getBytes(), signature);
		if (!rv) {
			System.out.println("---------->SDF_ExternalVerify_ECC FAIL");
		} else {
			System.out.println("===========>SDF_ExternalVerify_ECC OK");
		}

		sdf.SDF_GenerateKeyPair_ECC(ZaykJceGlobal.SGD_SM2_3, 256, publicKey, privateKey);

		ECCCipher sm2Cipher = sdf.SDF_ExternalEncrypt_ECC(ZaykJceGlobal.SGD_SM2_3, publicKey, "123".getBytes());
		if (sm2Cipher == null) {
			System.out.println("---------->SDF_ExternalEncrypt_ECC FAIL");
		} else {
			System.out.println("===========>SDF_ExternalEncrypt_ECC OK");
		}

		decData = sdf.SDF_ExternalDecrypt_ECC(ZaykJceGlobal.SGD_SM2_3, privateKey, sm2Cipher);
		if (decData == null) {
			System.out.println("---------->SDF_ExternalDecrypt_ECC FAIL");
		} else {
			System.out.println("===========>SDF_ExternalDecrypt_ECC OK: " + new String(decData));
		}

		publicKey = sdf.SDF_ExportEncPublicKey_ECC(1);
		if (publicKey == null) {
			System.out.println("---------->SDF_ExportEncPublicKey_ECC FAIL");
		} else {
			System.out.println("===========>SDF_ExportEncPublicKey_ECC OK");
		}

		sm2Cipher = sdf.SDF_ExternalEncrypt_ECC(ZaykJceGlobal.SGD_SM2_3, publicKey, "123".getBytes());
		if (sm2Cipher == null) {
			System.out.println("---------->SDF_ExternalEncrypt_ECC FAIL");
		} else {
			System.out.println("===========>SDF_ExternalEncrypt_ECC OK");
		}

		decData = sdf.SDF_InternalDecrypt_ECC(ZaykJceGlobal.SGD_SM2_3, 1, sm2Cipher);
		if (decData == null) {
			System.out.println("---------->SDF_InternalDecrypt_ECC FAIL");
		} else {
			System.out.println("===========>SDF_InternalDecrypt_ECC OK: " + new String(decData));
		}

		publicKey = sdf.SDF_ExportSignPublicKey_ECC(1);
		if (publicKey == null) {
			System.out.println("---------->SDF_ExportSignPublicKey_ECC FAIL");
		} else {
			System.out.println("===========>SDF_ExportSignPublicKey_ECC OK");
			// System.out.println("public key x: " + Hex.toHexString(publicKey.x));
		}

		System.out.println();
		System.out.println("**********RSA 运算****************");

		byte[] rsaOptData = new byte[256];
		Arrays.fill(rsaOptData, (byte) (new Random().nextInt() % 128));
		rsaOptData[0] = 0x00;
		rsaOptData[1] = 0x02;
		rsaOptData[10] = 0x00;
		byte[] rsaOptDataOut = sdf.SDF_InternalPublicKeyOperation_RSA(2, rsaOptData);
		if (rsaOptDataOut == null) {
			System.out.println("---------->SDF_InternalPublicKeyOperation_RSA FAIL");
		} else {
			System.out.println("===========>SDF_InternalPublicKeyOperation_RSA OK");
		}

		rsaOptDataOut = sdf.SDF_InternalPrivateKeyOperation_RSA(2, rsaOptDataOut);
		if (rsaOptDataOut == null) {
			System.out.println("---------->SDF_InternalPrivateKeyOperation_RSA FAIL");
		} else {
			System.out.println("===========>SDF_InternalPrivateKeyOperation_RSA OK");
		}

		RSArefPublicKey rsaPublicKey = sdf.SDF_ExportEncPublicKey_RSA(2);
		if (rsaPublicKey == null) {
			System.out.println("---------->SDF_ExportEncPublicKey_RSA FAIL");
		} else {
			System.out.println("===========>SDF_ExportEncPublicKey_RSA OK");
		}

		rsaOptDataOut = sdf.SDF_ExternalPublicKeyOperation_RSA(rsaPublicKey, rsaOptData);
		if (rsaOptDataOut == null) {
			System.out.println("---------->SDF_ExternalPublicKeyOperation_RSA FAIL");
		} else {
			System.out.println("===========>SDF_ExternalPublicKeyOperation_RSA OK");
		}

		// 摘要运算测试
		System.out.println();
		System.out.println("**********摘要运算****************");
		rv = sdf.SDF_HashInit(ZaykJceGlobal.SGD_SM3, publicKey, null);
		if (!rv) {
			System.out.println("---------->SDF_HashInit FAIL");
		} else {
			System.out.println("===========>SDF_HashInit OK");
		}
		rv = sdf.SDF_HashUpdate("123".getBytes());
		if (!rv) {
			System.out.println("---------->SDF_HashUpdate FAIL");
		} else {
			System.out.println("===========>SDF_HashUpdate OK");
		}
		byte[] hashData = sdf.SDF_HashFinal();
		if (hashData == null) {
			System.out.println("---------->SDF_HashFinal FAIL");
		} else {
			System.out.println("===========>SDF_HashFinal OK");
		}
		System.out.println("============>SM3:" + Hex.toHexString(hashData));

		// hmac测试
		ZaykKey hmacHandle = sdf.SDF_HmacInit(ZaykJceGlobal.SGD_SM3, 1, null);
		if (hmacHandle == null) {
			System.out.println("---------->SDF_HmacInit FAIL");
		} else {
			System.out.println("===========>SDF_HmacInit OK");
		}
		rv = sdf.SDF_HmacUpdate(hmacHandle, "123".getBytes());
		if (!rv) {
			System.out.println("---------->SDF_HmacUpdate FAIL");
		} else {
			System.out.println("===========>SDF_HmacUpdate OK");
		}
		byte[] hmac = sdf.SDF_HmacFinal(hmacHandle);
		if (hmac == null) {
			System.out.println("---------->SDF_HmacFinal FAIL");
		} else {
			System.out.println("===========>SDF_HmacFinal OK");
			System.out.println("============>hmac:" + Hex.toHexString(hmac));
		}

		hmac = sdf.SDF_Hmac_WithIndex(1, ZaykJceGlobal.SGD_SM3, null, "123".getBytes());
		if (hmac == null) {
			System.out.println("---------->SDF_Hmac_WithIndex FAIL");
		} else {
			System.out.println("===========>SDF_Hmac_WithIndex OK");
			System.out.println("============>hmac:" + Hex.toHexString(hmac));
		}

		// 密钥管理类测试
		System.out.println();
		System.out.println("**********管理运算****************");
		rsaPublicKey = sdf.SDF_ExportEncPublicKey_RSA(2);
		if (rsaPublicKey == null) {
			System.out.println("---------->SDF_ExportEncPublicKey_RSA FAIL");
		} else {
			System.out.println("===========>SDF_ExportEncPublicKey_RSA OK");
		}

		rsaPublicKey = sdf.SDF_ExportSignPublicKey_RSA(2);
		if (rsaPublicKey == null) {
			System.out.println("---------->SDF_ExportSignPublicKey_RSA FAIL");
		} else {
			System.out.println("===========>SDF_ExportSignPublicKey_RSA OK");
		}

		RSArefPrivateKey rsaPrivateKey = new RSArefPrivateKey();
		rv = sdf.SDF_GenerateKeyPair_RSA(2048, rsaPublicKey, rsaPrivateKey);
		if (!rv) {
			System.out.println("---------->SDF_GenerateKeyPair_RSA FAIL");
		} else {
			System.out.println("===========>SDF_GenerateKeyPair_RSA OK");
		}

		keyHandle = sdf.SDF_GenerateKeyWithIPK_RSA(2, 128, rsaOptDataOut);
		if (keyHandle == null) {
			System.out.println("---------->SDF_GenerateKeyWithIPK_RSA FAIL");
		} else {
			System.out.println("===========>SDF_GenerateKeyWithIPK_RSA OK");
		}

		/*
		 * keyHandle = sdf.SDF_GenerateKeyWithEPK_RSA(rsaPublicKey, 128, rsaOptDataOut);
		 * if (keyHandle == null) {
		 * System.out.println("---------->SDF_GenerateKeyWithEPK_RSA FAIL"); } else {
		 * System.out.println("===========>SDF_GenerateKeyWithEPK_RSA OK"); }
		 */

		ECCrefPublicKey eccPublicKey = sdf.SDF_ExportSignPublicKey_ECC(1);
		if (eccPublicKey == null) {
			System.out.println("---------->SDF_ExportSignPublicKey_ECC FAIL");
		} else {
			System.out.println("===========>SDF_ExportSignPublicKey_ECC OK");
		}
		eccPublicKey = sdf.SDF_ExportEncPublicKey_ECC(1);
		if (eccPublicKey == null) {
			System.out.println("---------->SDF_ExportEncPublicKey_ECC FAIL");
		} else {
			System.out.println("===========>SDF_ExportEncPublicKey_ECC OK");
		}

		ECCrefPrivateKey eccPrivateKey = new ECCrefPrivateKey();
		rv = sdf.SDF_GenerateKeyPair_ECC(ZaykJceGlobal.SGD_SM2_1, 256, eccPublicKey, eccPrivateKey);
		if (!rv) {
			System.out.println("---------->SDF_GenerateKeyPair_ECC FAIL");
		} else {
			System.out.println("===========>SDF_GenerateKeyPair_ECC OK");
		}

		rv = sdf.SDF_GenerateKeyWithIPK_ECC(1, 128, sm2Cipher, keyHandle);
		if (!rv) {
			System.out.println("---------->SDF_GenerateKeyWithIPK_ECC FAIL");
		} else {
			System.out.println("===========>SDF_GenerateKeyWithIPK_ECC OK");
		}

		keyHandle = sdf.SDF_ImportKeyWithISK_ECC(1, sm2Cipher);
		if (keyHandle == null) {
			System.out.println("---------->SDF_ImportKeyWithISK_ECC FAIL");
		} else {
			System.out.println("===========>SDF_ImportKeyWithISK_ECC OK");
		}

		rv = sdf.SDF_GenerateKeyWithEPK_ECC(128, ZaykJceGlobal.SGD_SM2_3, eccPublicKey, sm2Cipher, keyHandle);
		if (!rv) {
			System.out.println("---------->SDF_GenerateKeyWithEPK_ECC FAIL");
		} else {
			System.out.println("===========>SDF_GenerateKeyWithEPK_ECC OK");
		}

		ECCrefPublicKey sponsorPublicKey = new ECCrefPublicKey();
		ECCrefPublicKey sponsorTmpPublicKey = new ECCrefPublicKey();
		ZaykKey agreemHandle = new ZaykKey();
		rv = sdf.SDF_GenerateAgreementDataWithECC(1, 128, "1234".getBytes(), sponsorPublicKey, sponsorTmpPublicKey,
				agreemHandle);
		if (!rv) {
			System.out.println("---------->SDF_GenerateAgreementDataWithECC FAIL");
		} else {
			System.out.println("===========>SDF_GenerateAgreementDataWithECC OK");
		}

		ECCrefPublicKey responsePublicKey = new ECCrefPublicKey();
		ECCrefPublicKey responseTmpPublicKey = new ECCrefPublicKey();
		rv = sdf.SDF_GenerateAgreementDataAndKeyWithECC(2, 128, "5678".getBytes(), "1234".getBytes(), sponsorPublicKey,
				sponsorTmpPublicKey, responsePublicKey, responseTmpPublicKey, keyHandle);
		if (!rv) {
			System.out.println("---------->SDF_GenerateAgreementDataAndKeyWithECC FAIL");
		} else {
			System.out.println("===========>SDF_GenerateAgreementDataAndKeyWithECC OK");
		}

		keyHandle = sdf.SDF_GenerateKeyWithECC("5678".getBytes(), responsePublicKey, responseTmpPublicKey,
				agreemHandle);
		if (keyHandle == null) {
			System.out.println("---------->SDF_GenerateKeyWithECC FAIL");
		} else {
			System.out.println("===========>SDF_GenerateKeyWithECC OK");
		}

		rv = sdf.SDF_GenerateKeyWithKEK(128, ZaykJceGlobal.SGD_SMS4_ECB, 1, encKey, keyHandle);
		if (!rv) {
			System.out.println("---------->SDF_GenerateKeyWithKEK FAIL");
		} else {
			System.out.println("===========>SDF_GenerateKeyWithKEK OK");
		}

		keyHandle = sdf.SDF_ImportKeyWithKEK(ZaykJceGlobal.SGD_SMS4_ECB, 1, encKey);
		if (!rv) {
			System.out.println("---------->SDF_ImportKeyWithKEK FAIL");
		} else {
			System.out.println("===========>SDF_ImportKeyWithKEK OK");
		}

		rv = sdf.SDF_DestoryKey(keyHandle);
		if (!rv) {
			System.out.println("---------->SDF_DestoryKey FAIL");
		} else {
			System.out.println("===========>SDF_DestoryKey OK");
		}

	}

	public static void showHex(String msg, byte[] data) {
		showHex(msg, data, data.length);
	}

	public static void showHex(String msg, byte[] data, int dataLen) {
		int limitLen = data.length < dataLen ? data.length : dataLen;
		System.out.println(msg);
		for (int i = 0; i < limitLen; ++i) {
			if (i != 0 && i % 32 == 0) {
				System.out.println();
			}
			System.out.printf("%02X", data[i]);
		}
		System.out.println();
	}

	public static byte[] subBytes(byte[] ori, int len) {
		byte[] ret = new byte[len];

		for (int i = 0; i < len; ++i) {
			ret[i] = ori[i];
		}

		return ret;
	}

	public static byte[] unionBytes(byte[] a, byte[] b) {
		byte[] ret = new byte[a.length + b.length];

		System.arraycopy(a, 0, ret, 0, a.length);
		System.arraycopy(b, 0, ret, a.length, b.length);

		return ret;
	}

}
