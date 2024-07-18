package com.lbj.stock.service;

import com.lbj.stock.pojo.domain.*;
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

    R<Map> stockTradeVol4InnerMarket();

    R<Map> stockUpDownScopeCount();

    R<List<Stock4MinuteDomain>> stockScreenTimeSharing(String code);

    R<List<Stock4EvrDayDomain>> stockScreenDkLine(String stockCode);

    R<List<StockExternalDomain>> externalIndexAll();

    R<List<Map>> fuzzyStockCode(String searchStr);

    R<StockRtDescribeDomain> getStockRtDescribe(String code);

    R<List<Stock4EvrWeekDomain>> getWeekKLinData(String stockCode);
}
