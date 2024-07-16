package com.lbj.stock.service;

import com.lbj.stock.pojo.domain.InnerMarketDomain;
import com.lbj.stock.pojo.domain.StockBlockDomain;
import com.lbj.stock.pojo.domain.StockUpdownDomain;
import com.lbj.stock.vo.resp.PageResult;
import com.lbj.stock.vo.resp.R;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface StockService {
    R<List<InnerMarketDomain>> innerIndexAll();

    R<List<StockBlockDomain>> sectorAllLimit();

    R<PageResult> getStockPageInfo(Integer page, Integer pageSize);

    R<List<StockUpdownDomain>> getIncreaseTop4();

    R<Map> getStockUpDownCount();

    void stockExport(HttpServletResponse response, Integer page, Integer pageSize);
}
