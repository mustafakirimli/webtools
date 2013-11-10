package com.mkirimli.webtools;

/**
 *
 * @author mustafa
 */
public class PageRankHash {

    private static final String PREFIX = "7";

    private static long strToNum(CharSequence string, long num, long factor) {
        for (int i = 0; i < string.length(); i++) {
            num *= factor;
            num += string.charAt(i);
            num &= 0xFFFFFFFFl;
        }
        return num;
    }

    private static long hashURL(CharSequence string) {
        long c1 = strToNum(string, 0x1505, 0x21);
        long c2 = strToNum(string, 0, 0x1003F);
        c1 >>= 2;
        c1 = ((c1 >> 4) & 0x3FFFFC0) | (c1 & 0x3F);
        c1 = ((c1 >> 4) & 0x3FFC00) | (c1 & 0x3FF);
        c1 = ((c1 >> 4) & 0x3C000) | (c1 & 0x3FFF);
        long t1 = ((((c1 & 0x3C0) << 4) | (c1 & 0x3C)) << 2) | (c2 & 0xF0F);
        long t2 = ((((c1 & 0xFFFFC000) << 4) | (c1 & 0x3C00)) << 0xA) | (c2 & 0xF0F0000);
        return (t1 | t2);
    }

    private static char checkHash(long hash) {
        long check = 0, flag = 0;
        do {
            long remainder = hash % 10;
            hash /= 10;
            if (1 == (flag % 2)) {
                remainder += remainder;
                remainder = (remainder / 10) + (remainder % 10);
            }
            check += remainder;
            flag++;
        } while (0 != hash);
        check %= 10;
        if (0 != check) {
            check = 10 - check;
            if (1 == (flag % 2)) {
                if (1 == (check % 2)) {
                    check += 9;
                }
                check >>= 1;
            }
        }
        check += 0x30;
        return (char) check;
    }

    public static String checksum(String url) {
        long hash = hashURL(url);
        return PREFIX + checkHash(hashURL(url)) + hash;
    }
}
