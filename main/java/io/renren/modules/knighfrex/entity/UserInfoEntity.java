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
@TableName("user_info")
public class UserInfoEntity {

    /**
     * 
     */
	private Integer userId;
    /**
     * 
     */
	private String userAddress;
    /**
     * 
     */
	private String referAddress;
    /**
     * 
     */
	private Integer betVal;
    /**
     * 
     */
	private Integer botLevel;
    /**
     * 
     */
	private Integer allBuy;
}