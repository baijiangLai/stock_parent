package com.lbj.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 外盘指数domain
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockExternalDomain {
    /**
     * 大盘名称
     */
    private String name;
    /**
     * 当前大盘点
     */
    private BigDecimal curPoint;
    /**
     * 涨跌值
     */
    private BigDecimal upDown;
    /**
     * 当前日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date curDate;
}
