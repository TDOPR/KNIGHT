package com.defi.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 以太坊数据转换工具类
 */
public class EthereumUtils {
    public static final int PASS_RADIX = 16;//pay 数字都是16进制
    public static final double FEE = 0.000000000000021000;
    static final double PASS_VALUE = 1.0E18; //pay 单位wei 转成 币
    //public static final BigDecimal PASS_VALUE_BIGDECIMAL = new BigDecimal("1000000000000000000"); //pay 单位wei 转成 币
    //public static final int ETH_ROUND = 18;
    public static final int SCALE = 9;//保留小数点

    public static final String BSC_721 = "BSC-721"; // 721代币
    public static final String BSC_20 = "BSC-20"; // 20代币
    public static final String BSC = "bsc"; //


    /**
     * 转换成token币精度
     * @param amount
     * @param round
     * @return
     */
    /*public static BigDecimal revertToTokenAmount(BigDecimal amount, int round){
        if(round >= ETH_ROUND){
            return amount;
        }
        int tokenScale = round;//小数位数
        round = ETH_ROUND-round;//18-8=10
        double pow = Math.pow(Double.parseDouble(round+""),
                Double.parseDouble((round)+""));
        amount = amount.multiply(new BigDecimal(pow));//.setScale(tokenScale, BigDecimal.ROUND_HALF_UP)
        return amount;
    }*/

    /**
     * 转换成ETH币精度
     * @param amount
     * @param round
     * @return
     */
    /*public static BigDecimal revertToEthAmount(BigDecimal amount, int round){
        if(round >= ETH_ROUND){
            return amount;
        }
        int tokenScale = round;//小数位数
        round = ETH_ROUND-round;//18-8=10
        double pow = Math.pow(Double.parseDouble(round+""),
                Double.parseDouble((round)+""));
        amount = amount.divide(new BigDecimal(pow));
        return amount;
    }*/

    /**
     * 16进制转BigDecimal
     *
     * @param hex
     * @return
     */
    public static BigDecimal hexToBigDecimal(String hex, int round) {
        BigDecimal PASS_VALUE_BIGDECIMAL = revertBigDecimalByRound(round);
        return hexToBigDecimal(hex, PASS_VALUE_BIGDECIMAL);
    }

    /**
     * 16进制转BigDecimal
     *
     * @param hex
     * @param passValueBigdecimal
     * @return
     */
    public static BigDecimal hexToBigDecimal(String hex, BigDecimal passValueBigdecimal) {
        if (hex == null || hex.length() <= 2) {
            return BigDecimal.ZERO;
        }
        hex = hex.toLowerCase();
        if (!hex.startsWith("0x")) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(new BigInteger(hex.substring(2), PASS_RADIX).toString())
                .divide(passValueBigdecimal);//.setScale(SCALE, BigDecimal.ROUND_DOWN)
    }

    /**
     * BigInteger 转 BigDecimal(wei 换算成平常单位)
     *
     * @param value
     * @return
     */
    public static BigDecimal bigIntegerToBigDecimal(BigInteger value, int round) {
        BigDecimal PASS_VALUE_BIGDECIMAL = revertBigDecimalByRound(round);
        return new BigDecimal(value.toString())
                .divide(PASS_VALUE_BIGDECIMAL).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * BigDecimal 转 BigInteger(平常单位换算成wei)
     *
     * @param value
     * @return
     */
    public static BigInteger generally2wei(BigDecimal value, int round) {
        BigDecimal PASS_VALUE_BIGDECIMAL = revertBigDecimalByRound(round);
        return new BigDecimal(value.toString()).multiply(PASS_VALUE_BIGDECIMAL).toBigInteger();
    }

    public static BigDecimal revertBigDecimalByRound(int round){
        double pow = Math.pow(Double.parseDouble("10"),Double.parseDouble((round)+""));
        return new BigDecimal(pow);
    }
}