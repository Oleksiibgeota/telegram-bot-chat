package com.chatbot.core.utils;

import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.KeyParameter;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.PKCS5S2ParametersGenerator;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.SHA256Digest;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class PasswordResolver {
    public String encodedPassword(String userPassword) {
        Preconditions.checkArgument(StringUtils.isNotBlank(userPassword));

        int iterations = 150000;
        String salt = saltGenerator();
        String algorithm = "pbkdf2_sha256";

        return algorithm + "$" + iterations + "$" + salt + "$"
                + passwordResolver(userPassword, iterations, salt.getBytes());
    }

    public boolean matches(String encryptedPassword, String openUserPassword) {
        Preconditions.checkArgument(StringUtils.isNotBlank(encryptedPassword));
        Preconditions.checkArgument(StringUtils.isNotBlank(openUserPassword));

        String[] args = encryptedPassword.split("\\$");
        Preconditions.checkArgument(args.length == 4);
        return args[3].equals(passwordResolver(openUserPassword, Integer.parseInt(args[1]), args[2].getBytes()));
    }


    private String passwordResolver(String password, int iterations, byte[] salt) {

        int length = 256;
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        gen.init(password.getBytes(StandardCharsets.UTF_8), salt, iterations);

        byte[] dk = ((KeyParameter) gen.generateDerivedParameters(length)).getKey();
        byte[] hashBase64 = Base64.encodeBase64(dk);
        return new String(hashBase64);
    }

    private String saltGenerator() {
        RandomStringGenerator randomStringGenerator =
                new RandomStringGenerator.Builder()
                        .withinRange('0', 'z')
                        .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                        .build();
        return randomStringGenerator.generate(12);
    }
}
