package com.defi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 资金账户流水
 * 
 * @date 2019-11-25 10:51:13
 */
@Data
@TableName("account_detail")
public class AccountDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(type = IdType.AUTO)
	private long id;
	/**
	 * 用户id
	 */
	private Integer uid;
	/**
	 * 币种id
	 */
	private Integer coinId;
	/**
	 * 账户id
	 */
	private Integer accountId;
	/**
	 * 该笔流水资金关联方的账户id
	 */
	private Integer refAccountId;
	/**
	 * 订单ID
	 */
	private Integer orderId;
	/**
	 * 入账为1，出账为2
	 */
	private Integer direction;
	/**
	 * 业务类型
	 */
	private String businessType;
	/**
	 * 资产数量
	 */
	private BigDecimal amount;
	/**
	 * 手续费
	 */
	private BigDecimal fee;

	private BigDecimal usdtAmount;
	/**
	 * 流水状态：
充值
提现
冻结
解冻
转出
转入 签到
	 */
	private String remark;
	/**
	 * 日期
	 */
	private Date created;

	private BigDecimal price;

}
