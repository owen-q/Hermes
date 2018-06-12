package org.lunker.new_proxy.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by dongqlee on 2018. 3. 21..
 */
public class HashUtil {

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();

        for(int i = 0; i < data.length; ++i) {
            int halfbyte = data[i] >>> 4 & 15;
            int var4 = 0;

            do {
                if (0 <= halfbyte && halfbyte <= 9) {
                    buf.append((char)(48 + halfbyte));
                } else {
                    buf.append((char)(97 + (halfbyte - 10)));
                }

                halfbyte = data[i] & 15;
            } while(var4++ < 1);
        }

        return buf.toString();
    }

    private static String reduceHash(String hash, int maxChars) {
        return hash.length() > maxChars ? hash.substring(0, maxChars) : hash;
    }

    public static String hashString(String input, int length) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException var5) {
            throw new IllegalArgumentException("The SHA Algorithm could not be found", var5);
        }

        byte[] bytes = input.getBytes();
        md.update(bytes);
        String hashed = convertToHex(md.digest());
        hashed = reduceHash(hashed, length);
        return hashed;
    }

}
