package io.renren.modules.demo.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2022-09-13
 */
@Data
public class TeamInfoExcel {
    @Excel(name = "")
    private Integer userId;
    @Excel(name = "")
    private Integer teamPerform;
    @Excel(name = "")
    private Integer teamNum;

}