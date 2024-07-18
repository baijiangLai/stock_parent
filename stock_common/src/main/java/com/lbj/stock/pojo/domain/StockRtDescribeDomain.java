package com.lbj.stock.pojo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockRtDescribeDomain {
    private String code;
    private String trade;
    private String business;
    private String name;
}
