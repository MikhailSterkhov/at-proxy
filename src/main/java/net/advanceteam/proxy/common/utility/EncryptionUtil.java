package net.advanceteam.proxy.common.utility;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.advanceteam.proxy.netty.protocol.packet.impl.login.EncryptionRequestPacket;
import net.advanceteam.proxy.netty.protocol.packet.impl.login.EncryptionResponsePacket;

@UtilityClass
public class EncryptionUtil {

    private final Random random = new Random();

    @Getter
    private final KeyPair keys;

    @Getter
    private static final SecretKey secret = new SecretKeySpec(new byte[16], "AES");

    static {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);

            keys = generator.generateKeyPair();

        } catch (NoSuchAlgorithmException ex) {
            throw new ExceptionInInitializerError(ex);
        }

    }

    public EncryptionRequestPacket createEncryptionRequestPacket() {
        String hash = Long.toString(random.nextLong(), 16);

        byte[] pubKey = keys.getPublic().getEncoded();
        byte[] verify = new byte[4];

        random.nextBytes(verify);

        return new EncryptionRequestPacket(hash, pubKey, verify);
    }

    public SecretKey getSecret(EncryptionResponsePacket resp, EncryptionRequestPacket request) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, keys.getPrivate());

        byte[] decrypted = cipher.doFinal(resp.getVerifyToken());

        if (!Arrays.equals(request.getVerifyToken(), decrypted)) {
            throw new IllegalStateException("Key pairs do not match!");
        }

        cipher.init(Cipher.DECRYPT_MODE, keys.getPrivate());

        return new SecretKeySpec(cipher.doFinal(resp.getSharedSecret()), "AES");
    }

}
