package com.defi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 机器人推广收益冻结表
 */
@Data
@TableName("reward_freeze_robot")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardFreezeRobotEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Integer id;
	/**
	 * 用户ID
	 */
	private Integer uid;
	/**
	 * 0未释放，1已释放
	 */
	private Integer type;
	/**
	 * 奖励金额
	 */
	private BigDecimal amount;
	/**
	 * 下级机器人等级
	 */
	private Integer robotLevel;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
