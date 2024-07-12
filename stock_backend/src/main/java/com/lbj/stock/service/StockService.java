package com.lbj.stock.service;

import com.lbj.stock.pojo.domain.InnerMarketDomain;
import com.lbj.stock.pojo.domain.StockBlockDomain;
import com.lbj.stock.vo.resp.R;

import java.util.List;

public interface StockService {
    R<List<InnerMarketDomain>> innerIndexAll();

    R<List<StockBlockDomain>> sectorAllLimit();
}
