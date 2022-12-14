package com.defi.utils;

public class ShareCodeUtil {

    /**
     * 自定义进制(0,1没有加入,容易与o,l混淆)
     */
    private static final char[] r = new char[]{'F', 'L', 'G', 'W', '5', 'X', 'C', '3', '9', 'Z', 'M', '6', '7', 'Y', 'R', 'T', '2', 'H', 'S', '8', 'D', 'V', 'E', 'J', '4', 'K', 'Q', 'P', 'U', 'A', 'N', 'B'};

    /**
     * 进制长度
     */
    private static final int binLen = r.length;

    private static final long startNumber = 0L;

    private static final long minLength = 8;

    /**
     * @param id ID
     * @return 随机码
     */
    public static String idToCode(long id, long costomStartNumber) {
        if (costomStartNumber < 0) {
            costomStartNumber = startNumber;
        }
        id += costomStartNumber;
        char[] buf = new char[32];
        int charPos = 32;

        while ((id / binLen) > 0) {
            int ind = (int) (id % binLen);
            buf[--charPos] = r[ind];
            id /= binLen;
        }
        buf[--charPos] = r[(int) (id % binLen)];
        String str = new String(buf, charPos, (32 - charPos));
        String code = str.toUpperCase();
        if (code.length() < 8) {
            String team = "00000000";
            code = team.substring(0, 8 - code.length()) + code;
        }
        return code.toUpperCase();
    }

    public static String idToCode(long idL) {
        return idToCode(idL, -1L);
    }

    public static String idToCode(String id) {
        long idL = Long.parseLong(id);
        return idToCode(idL, -1L);
    }

    public static String idToCode(String id, long costomStartNumber) {
        long idL = Long.parseLong(id);
        return idToCode(idL, costomStartNumber);
    }

    public static long codeToId(String code) {
        code = code.replace("0", "");
        code = code.toUpperCase();
        char chs[] = code.toCharArray();
        long res = 0L;
        for (int i = 0; i < chs.length; i++) {
            int ind = 0;
            for (int j = 0; j < binLen; j++) {
                if (chs[i] == r[j]) {
                    ind = j;
                    break;
                }
            }
            if (i > 0) {
                res = res * binLen + ind;
            } else {
                res = ind;
            }
        }
        res -= startNumber;
        return res;
    }
}
