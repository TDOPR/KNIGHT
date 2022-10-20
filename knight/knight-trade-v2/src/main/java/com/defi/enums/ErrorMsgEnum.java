package com.defi.enums;

public enum ErrorMsgEnum {

    ERROR_600S("Transaction receipt was not generated after 600 seconds for transaction: ", "600秒超时"),
    /**
     * Transaction has failed with status: 0x0. Gas used: 100000. (not-enough gas?)
     */
    ERROR_NOT_ENOUGH_GAS("not-enough gas", "Gas不足"),
    ERROR_TIMEOUT("timeout", "超时");

    private String msg;
    private String desc;

    ErrorMsgEnum(String msg, String desc) {
        this.msg = msg;
        this.desc = desc;
    }

    public String getMsg() {
        return msg;
    }


    public String getDesc() {
        return desc;
    }


}
