package com.lbj.stock.controller;

import com.lbj.stock.pojo.domain.InnerMarketDomain;
import com.lbj.stock.pojo.domain.StockBlockDomain;
import com.lbj.stock.pojo.domain.StockUpdownDomain;
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
}
