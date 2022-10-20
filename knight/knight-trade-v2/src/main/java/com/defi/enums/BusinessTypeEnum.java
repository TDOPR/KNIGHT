package com.defi.enums;


import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 资金流水业务类型
 * @Author: Chen Long
 * @Date: Created in 2018/5/13 下午2:37
 * @Modified by: Chen Long
 */
public enum BusinessTypeEnum {

    RETURN("return", "拒绝提取返还", "return"),
    SYSTEM_RECHARGE("system_recharge", "系统充值", "System top-up"),
    SYSTEM_WITHDRAW("system_withdraw", "系统扣除", "System to deduct"),
    SYSTEM_SEND("system_send", "系统赠送", "System to send"),
    STAR_BUY("star_buy", "交易TTS", "buy TTS"),
    STAR_SELL("star_sell", "出售TTS", "sell TTS"),
    PURCHASE_MINE("purchase_mine", "购买矿机", "Purchase Mine"),
    TRADE_ORDER_CANCEL_RETURN("trade_order_cancel_return", "撤销订单返还", "Order cancellation return"),
    STAR_COMMISSION_PRODUCE("star_commission_produce", "GOD挖矿收益", "GOD mining earnings"),
    STAR_COMMISSION_MINE("star_commission_mine", "矿机收益", "mine earnings"),
    STAR_2_DIAMOND("star_2_diamond", "GOD兑换金豆", "GOD for gold beans"),
    STAR_TRANSFER_LOG("star_transfer_log", "GOD划转", "GOD transfer"),
    UNDO_WITHDRAW("undo_withdraw", "撤销提币", "Cancel the mention money"),
    STAR_SHOP_REBATES("star_shop_rebates", "商城返佣GOD", "Mall return to God"),
    PROMOTIONAL_PROFIT("promotional_profit", "推广收益", "Promotion of earnings"),
    GROUP_PROFIT("group_profit", "团队收益", "The team earnings"),
    SAME_LEVEL("same_level", "平级收益", "Tsame levele arnings"),
    CONTRACT_STAR_PROFIT("contract_star_profit", "合约GOD收益", "Contract GOD revenue"),
    LUCK_DRAW("luck_draw", "抽奖", "luck draw"),
    BOOKING_PANIC_BUYING("booking_panic_buying", "预约抢购", "booking panic buying"),
    DEPOSIT_BONUS("deposit_bonus", "GOD存币生息", "deposit bonus"),
    PANIC_BUYING("panic_buying", "抢购", "panic buying"),
    BOOKING_PANIC_BUYING_RETURN("booking_panic_buying_return", "预约抢购退款", "booking panic buying return"),
    STAR_TRANS_OVERTIME_RETURN("star_trans_overtime_return", "交易超时返还", "Transaction timeout return"),
    STAR_APPEAL_OVERTIME_RETURN("star_appeal_overtime_return", "申诉成功返还", "Complaint returned successfully"),
    PRODUCT_CONVERT_FG("product_convert_fg", "酒品转换GOD", "Wine conversion GOD"),
    COMISSION_2_FG("comission_2_fg", "收益兑换GOD", "Revenue Exchange GOD"),
    STAR_LUCKY_RED_ENVELOPE("star_lucky_red_envelope", "参与幸运红包", "Participate in Lucky Red Envelopes"),
    STAR_LUCKY_BTC("star_lucky_btc", "参与BTC竞猜", "Participate in the BTC contest"),
    STAR_LUCKY_RED_ENVELOPE_DIVIDEND("star_lucky_red_envelope_dividend", "幸运红包分红", "Lucky red envelope bonus"),
    STAR_LUCKY_BTC_DIVIDEND("star_lucky_btc_dividend", "幸运BTC分红", "Lucky BTC dividends"),
    PPD_RECEIVE_LOG("PPD_RECEIVE_LOG", "PPD领取", "PPD to receive"),

    STAR_DYNAMIC_REVENUE("star_dynamic_revenue", "直推返佣收益", "dynamic revenue"),
    STAR_PROMOTION_INCOME("star_pomotion_income", "推广收益返佣", "pomotion revenue"),
    STAR_DIRECT_REVENUE("star_direct_revenue", "间推返佣收益", "direct revenue"),
    STAR_TEAM_REVENUE("star_team_revenue", "团队返佣收益", "team revenue"),
    STAR_INDIRECT_REVENUE("star_indirect_revenue", "动态分享收益", "Dynamic revenue sharing"),
    STAR_COMMUNITY_REVENUE("star_community_revenue", "社区收益", "community revenue"),
    PPT_REBATE_REVENUE("ppt_rebate_revenue", "PPT返点收益", "ppt rebate revenue"),

    COMISSION_SALE_FEE("comission_sale_fee", "收益出售手续费", "Revenue sale commission"),
    DBQ_EXCHANGE_USDT("DBQ_EXCHANGE_USDT", "代币券兑换USDT", "DBQ exchange USDT"),
    PPT_TRANSFER("ppt_transfer", "PPT转账", "ppt transfer"),
    PPD_TRANSFER("ppd_transfer", "PPD转账", "ppd transfer"),
    STAR_TRANSFER("star_transfer", "转账", "star transfer"),
    PPT_MINE_CANCEL("ppt_mine_cancel", "取消矿机返还PPT", "ppt mine cancel"),
    PPUSDT_MINE_CANCEL("PPUSDT_mine_cancel", "取消矿机返还PPUSDT", "PPUSDT mine cancel"),

    SCAN_PAY_GOD("scan_pay_gpm", "扫码支付", "scan pay"),
    SCAN_PAY_STAR_RETURN("scan_pay_star_return", "扫码支付退回", "scan pay star return"),


    BZT_INVEST("BZT_INVEST", "参与兑换", "invest"),
    BZT_HANDSEL("BZT_HANDSEL", "赠送TTS", "handsel"),
    BZT_INCOME_INITIALIZATION("BZT_INCOME_INITIALIZATION", "初始化可收益资产", "incomeInitialization"),
    BZT_DBQ_INCOME("BZT_DBQ_INCOME", "兑换收益", "dbqDaily"),
    BZT_DBQ_INCOME_OUT("BZT_DBQ_INCOME_OUT", "兑换收益", "dbqDailyOut"),
    BZT_BZT_INCOME("BZT_BZT_INCOME", "TTS每日释放", "bztDaily"),
    BZT_BZT_INVITE_ONE("BZT_BZT_INVITE_ONE", "直接分享", "bztInviteOne"),
    BZT_BZT_INVITE_TWO("BZT_BZT_INVITE_TWO", "间接分享", "bztInviteTwo"),
    BZT_BZT_TEAM_EQ("BZT_BZT_TEAM_EQ", "社区平级", "bztTeamEq"),
    BZT_BZT_TEAM("BZT_BZT_TEAM", "社区级差", "bztTeam"),
    BZT_DBQ_DYNAMIC("BZT_DBQ_DYNAMIC", "社区收益", "dbqDynamicDaily"),





    POWER_NET("POWER_NET", "网体奖", "netIncome"),
    POWER_STATIC_DAILY("POWER_STATIC_DAILY", "静态收益", "staticDaily"),
    LP_POWER_STATIC_DAILY("LP_POWER_STATIC_DAILY", "lp静态收益", "lpStaticDaily"),
    LP_POWER_RECHARGE("LP_POWER_RECHARGE", "lp算力投入", "lpPowerRecharge"),
    LP_POWER_INVITE("LP_POWER_INVITE", "lp直推收益", "lpInvite"),
    LP_POWER_TEAM("LP_POWER_TEAM", "lp团队收益", "lpTeam"),
    LP_POWER_TEAM_EQ("LP_POWER_TEAM_EQ", "lp团队收益平级", "lpTeamEq"),
    POWER_WITHDRAW("POWER_WITHDRAW", "power提现", "withdraw"),
    LP_POWER_WITHDRAW("LP_POWER_WITHDRAW", "lppower提现", "lpwithdraw"),
    LP_POWER_WITHDRAWLPTOKEN("LP_POWER_WITHDRAWLPTOKEN", "lptoken提现", "lptokenwithdraw"),
    QR_WITHDRAWLPTOKEN("QR_WITHDRAWLPTOKEN", "二维码收款提现", "QRwithdraw"),

    BUY_ROBOT("BUY_ROBOT","首次购买机器人","buyRobot"),
    UPGRADE_ROBOT("UPGRADE_ROBOT","升级机器人","upgradeRobot"),
    REWARD_FREEZE_ROBOT("REWARD_FREEZE_ROBOT","机器人推广奖（冻结状态）","rewardFreezeRobot"),
    REWARD_UNFREEZE_ROBOT("REWARD_UNFREEZE_ROBOT","返还冻结机器人推广奖","rewardUnFreezeRobot"),
    REWARD_ROBOT("REWARD_ROBOT","机器人推广奖","rewardRobot"),

    REWARD_COMMU("REWARD_COMMU", "社区推广奖", "rewardCommu"),

    RECHARGE("RECHARGE","托管","recharge");


    /**
     *
     */
    private String code;
    /**
     * 标识
     */
    private String desc;
    private String descEn;

    BusinessTypeEnum(String code, String desc, String descEn) {
        this.code = code;
        this.desc = desc;
        this.descEn = descEn;
    }

    public static Map<String, String> getDescByCode(String code) {
        HashMap<String, String> resultMap = new HashMap<>();
        for (BusinessTypeEnum type : BusinessTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                resultMap.put("desc", type.getDesc());
                resultMap.put("descEn", type.getDescEn());
            }
        }
        return resultMap;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDescEn() {
        return descEn;
    }

    public void setDescEN(String descEn) {
        this.descEn = descEn;
    }
}