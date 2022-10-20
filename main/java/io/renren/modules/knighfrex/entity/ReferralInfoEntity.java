package io.renren.modules.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2022-09-13
 */
@Data
@TableName("referral_info")
public class ReferralInfoEntity {

    /**
     * 
     */
	private Integer userId;
    /**
     * 
     */
	private String referralAddress;
    /**
     * 
     */
	private Integer referralId;
}