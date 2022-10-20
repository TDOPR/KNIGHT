package com.defi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 下单请求参数
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenTransEntranceParamDto {

    String from;//转出方账号
    String to;//转入方账号
    String contract;//合约地址
    BigDecimal amount;//数量
    int round;//token小数位数
}
