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
public class CreateAccountResultDto {

    private String account;//账号名
    private String accountAddress;//账号地址
    private String serialNo;//订单流水号
}
