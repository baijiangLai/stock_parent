package com.lbj.stock.controller;

import com.lbj.stock.pojo.domain.*;
import com.lbj.stock.vo.resp.PageResult;
import com.lbj.stock.vo.resp.R;
import com.lbj.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quot")
public class StockController {

    @Autowired
    private StockService stockService;

    /**
     * 获取国内最新大盘指数
     * @return
     */
    @GetMapping("/index/all")
    public R<List<InnerMarketDomain>> innerIndexAll(){
        return stockService.innerIndexAll();
    }

    /**
     *获取沪深两市板块最新数据，以交易总金额降序查询，取前10条数据
     * @return
     */
    @GetMapping("/sector/all")
    public R<List<StockBlockDomain>> sectorAll(){
        return stockService.sectorAllLimit();
    }

    /**
     * 分页查询股票最新数据，并按照涨幅排序查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/stock/all")
    public R<PageResult> getStockPageInfo(@RequestParam(name = "page",required = false,defaultValue = "1") Integer page,
                                          @RequestParam(name = "pageSize",required = false,defaultValue = "20") Integer pageSize){
        return stockService.getStockPageInfo(page,pageSize);
    }

    /**
     * 分页查询股票最新数据，并按照涨幅排序查询
     */
    @GetMapping("/stock/increase")
    public R<List<StockUpdownDomain>> getIncreaseTop4() {
        return stockService.getIncreaseTop4();
    }

    /**
     * 统计最新交易日下股票每分钟涨跌停的数量
     * @return
     */
    @GetMapping("/stock/updown/count")
    public R<Map> getStockUpdownCount(){
        return stockService.getStockUpDownCount();
    }

    /**
     * 将指定页的股票数据导出到excel表下
     * @param response
     * @param page  当前页
     * @param pageSize 每页大小
     */
    @GetMapping("/stock/export")
    public void stockExport(HttpServletResponse response,
                            @RequestParam(name = "page",required = false,defaultValue = "1") Integer page,
                            @RequestParam(name = "pageSize",required = false,defaultValue = "20") Integer pageSize){
        stockService.stockExport(response,page,pageSize);
    }

    /**
     * 功能描述：统计国内A股大盘T日和T-1日成交量对比功能（成交量为沪市和深市成交量之和）
     * @return
     */
    @GetMapping("/stock/tradeAmt")
    public R<Map> stockTradeVol4InnerMarket(){
        return stockService.stockTradeVol4InnerMarket();
    }

    /**
     * 查询当前时间下股票的涨跌幅度区间统计功能
     * 如果当前日期不在有效时间内，则以最近的一个股票交易时间作为查询点
     * @return
     */
    @GetMapping("/stock/updown")
    public R<Map> getStockUpDown(){
        return stockService.stockUpDownScopeCount();
    }

    /**
     * 功能描述：查询单个个股的分时行情数据，也就是统计指定股票T日每分钟的交易数据；
     *         如果当前日期不在有效时间内，则以最近的一个股票交易时间作为查询时间点
     * @param code 股票编码
     * @return
     */
    @GetMapping("/stock/screen/time-sharing")
    public R<List<Stock4MinuteDomain>> stockScreenTimeSharing(String code){
        return stockService.stockScreenTimeSharing(code);
    }

    /**
     * 单个个股日K 数据查询 ，可以根据时间区间查询数日的K线数据
     * @param stockCode 股票编码
     */
    @GetMapping("/stock/screen/dkline")
    public R<List<Stock4EvrDayDomain>> getDayKLinData(@RequestParam("code") String stockCode){
        return stockService.stockScreenDkLine(stockCode);
    }

    /**
     * 外盘指数行情数据查询，根据时间和大盘点数降序排序取前4
     */
    @GetMapping("/external/index")
    public R<List<StockExternalDomain>> getExternalIndexAll() {
        return stockService.externalIndexAll();
    }

    /**
     * 根据输入的个股代码，进行模糊查询，返回证券代码和证券名称
     * @param searchStr
     * @return
     */
    @GetMapping("/stock/search")
    public R<List<Map>> fuzzyStockCode(@RequestParam(value = "searchStr", required = true) String searchStr) {
        return stockService.fuzzyStockCode(searchStr);
    }

    /**
     * 个股主营业务查询接口
     */
    @GetMapping("/stock/describe")
    public R<StockRtDescribeDomain> getStockRtDescribe(@RequestParam(value = "code", required = true) String code) {
        return stockService.getStockRtDescribe(code);
    }

    @GetMapping("/stock/screen/weekkline")
    public R<List<Stock4EvrWeekDomain>> getWeekKLinData(@RequestParam("code") String stockCode) {
        return stockService.getWeekKLinData(stockCode);
    }

    @GetMapping("/stock/screen/second/detail")
    public R<StockRtDetailDomain> getRtData(@RequestParam("code") String stockCode) {
        return stockService.getRtData(stockCode);
    }

    @GetMapping("/stock/screen/second")
    public R<List<StockSecondDomain>> getTradeTop10(@RequestParam("code") String stockCode) {
        return stockService.getTradeTop10(stockCode);
    }
}
