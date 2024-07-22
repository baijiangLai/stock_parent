package com.lbj.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockSecondDomain {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date date;
    private Long tradeAmt;
    private BigDecimal tradeVol;
    private BigDecimal tradePrice;
}
