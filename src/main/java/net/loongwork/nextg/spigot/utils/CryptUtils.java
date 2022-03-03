package net.loongwork.nextg.spigot.utils;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.loongwork.nextg.spigot.NextGSpigot;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@UtilityClass
public class CryptUtils {

    private final String CIPHER_NAME = "AES/CBC/PKCS5PADDING";
    private final int CIPHER_KEY_LEN = 16; // 128 bits

    public String encrypt(String data) {
        try {
            val key = getFormattedKey("loongwork");
            val iv = getFormattedKey(getIV());

            IvParameterSpec initVector = new IvParameterSpec(iv.getBytes(StandardCharsets.ISO_8859_1));
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.ISO_8859_1), "AES");

            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CIPHER_NAME);
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, keySpec, initVector);

            byte[] encryptedData = cipher.doFinal((data.getBytes()));

            String base64_EncryptedData = Base64.getEncoder().encodeToString(encryptedData);
            String base64_IV = Base64.getEncoder().encodeToString(iv.getBytes(StandardCharsets.ISO_8859_1));

            return base64_EncryptedData + ":" + base64_IV;

        } catch (Exception e) {
            NextGSpigot.instance().getLogger().severe("Error while encrypting data");
            e.printStackTrace();
        }

        return null;
    }

    public String decrypt(String data) {
        try {
            val key = getFormattedKey("loongwork");

            String[] parts = data.split(":");

            IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(parts[1]));
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.ISO_8859_1), "AES");

            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CIPHER_NAME);
            cipher.init(javax.crypto.Cipher.DECRYPT_MODE, keySpec, iv);

            byte[] decodedEncryptedData = Base64.getDecoder().decode(parts[0]);

            byte[] original = cipher.doFinal(decodedEncryptedData);

            return new String(original);
        } catch (Exception e) {
            NextGSpigot.instance().getLogger().severe("Error while decrypting data");
            e.printStackTrace();
        }

        return null;
    }

    private String getFormattedKey(String key) {
        if (key.length() < CIPHER_KEY_LEN) {
            return String.format("%1$" + CIPHER_KEY_LEN + "s", key).replace(' ', '0');
        }
        return key.substring(key.length() - CIPHER_KEY_LEN);
    }

    private String getIV() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = ni.getHardwareAddress();
            String[] hexadecimal = new String[hardwareAddress.length];
            for (int i = 0; i < hardwareAddress.length; i++) {
                hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
            }
            return String.join("", hexadecimal);
        } catch (Exception e) {
            NextGSpigot.instance().getLogger().severe("Error while getting IV");
            e.printStackTrace();
            return "";
        }
    }
}
