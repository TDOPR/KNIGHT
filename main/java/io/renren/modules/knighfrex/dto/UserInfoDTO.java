package io.renren.modules.demo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2022-09-13
 */
@Data
@ApiModel(value = "")
public class UserInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "")
	private Integer userId;

	@ApiModelProperty(value = "")
	private String userAddress;

	@ApiModelProperty(value = "")
	private String referAddress;

	@ApiModelProperty(value = "")
	private Integer betVal;

	@ApiModelProperty(value = "")
	private Integer botLevel;

	@ApiModelProperty(value = "")
	private Integer allBuy;


}