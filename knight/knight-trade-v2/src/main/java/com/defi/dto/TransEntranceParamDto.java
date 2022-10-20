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
public class TransEntranceParamDto {

    private String from;//转出方账号
    private String to;//转入方账号
    private BigDecimal amount;//数量
}
