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
public class StockRtDetailDomain {
    private Long tradeAmt;
    private BigDecimal preClosePrice;
    private BigDecimal lowPrice;
    private BigDecimal highPrice;
    private BigDecimal openPrice;
    private BigDecimal tradeVol;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date curDate;
}
