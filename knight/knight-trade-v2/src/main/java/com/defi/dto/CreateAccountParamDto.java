package com.defi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提现参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountParamDto {

    private String agentNo;//代理商接收地址
    private String serialNo;//订单流水号

}
