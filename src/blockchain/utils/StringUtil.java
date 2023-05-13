package blockchain.utils;

import java.nio.charset.StandardCharsets;
import java.security.*;

public class StringUtil {

    public static boolean hasNumberOfZeros(String input, int numberOfZeros) {
        StringBuilder zeros = new StringBuilder();
        for (int i = 0; i < numberOfZeros; i++) {
            zeros.append("0");
        }
        return input.startsWith(zeros.toString()) && !input.startsWith(zeros.toString() + '0');
    }

    public static String applySha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte elem: hash) {
                String hex = Integer.toHexString(0xff & elem);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static KeyPair generateKeys() {
        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            keygen.initialize(1024, random);
            return keygen.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] sign(String input, PrivateKey sk) {
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(sk);
            signature.update(input.getBytes());
            return signature.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(String input, byte[] signedInput, PublicKey pk) {
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(pk);
            signature.update(input.getBytes());
            return signature.verify(signedInput);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
