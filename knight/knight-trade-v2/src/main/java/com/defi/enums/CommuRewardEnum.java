package com.defi.enums;

import java.math.BigDecimal;

public enum CommuRewardEnum {
    REWARD_RATE_ZERO(0, new BigDecimal("0")),
    REWARD_RATE_ONE(1, new BigDecimal("7")),
    REWARD_RATE_TW0(2, new BigDecimal("12")),
    REWARD_RATE_THREE(3, new BigDecimal("17")),
    REWARD_RATE_FOUR(4, new BigDecimal("22")),
    REWARD_RATE_FIVE(5, new BigDecimal("27"));

    private int level;
    private BigDecimal rate;

    CommuRewardEnum(int level, BigDecimal rate) {
        this.rate = rate;
        this.level = level;
    }

//    public int getCode() {
//        return code;
//    }


    public BigDecimal getRate() {
        return rate;
    }

    public int getLevel() {
        return level;
    }

    public static BigDecimal getRateByLevel(int level) {
        for (CommuRewardEnum commuRewardEnum : CommuRewardEnum.values()) {
            if (level == commuRewardEnum.getLevel()) {
                return commuRewardEnum.getRate();
            }
        }
        return BigDecimal.ZERO;
    }

//    public static RobotEnum parseValue(int value) {
//
//        for (final RobotEnum sys : RobotEnum.values()) {
//            if (sys.getCode() == value) {
//                return sys;
//            }
//        }
//        return null;
//    }
}
