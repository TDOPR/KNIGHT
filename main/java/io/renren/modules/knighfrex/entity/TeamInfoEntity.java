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
@TableName("team_info")
public class TeamInfoEntity {

    /**
     * 
     */
	private Integer userId;
    /**
     * 
     */
	private Integer teamPerform;
    /**
     * 
     */
	private Integer teamNum;
}