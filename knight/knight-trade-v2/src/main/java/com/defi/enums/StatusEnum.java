package com.defi.enums;

public enum StatusEnum {

    INIT(-1, "初始化"),

    INSUFFICIENT_USDT(9, "USDT余额为0"),
    AVAILABLE(0, "可使用的有余额账户"),
    SUCCESSFUL(1, "成功"),
    REFUSED(2, "拒绝"),
    CANCELED(3, "撤销"),
    AUDIT_PASSED(4, "审核通过"),
    SENDING(5, "打币中"),
    WAITTING_BLOCK_CONFIRM(6, "待区块确认"),
    FAILED(7, "区块打币失败");

    private int code;
    private String desc;

    StatusEnum(int value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }


    public String getDesc() {
        return desc;
    }


    public static StatusEnum parseValue(int value) {

        for (final StatusEnum sys : StatusEnum.values()) {
            if (sys.getCode() == value) {
                return sys;
            }
        }
        return null;
    }
}
