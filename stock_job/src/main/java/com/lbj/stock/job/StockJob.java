package com.lbj.stock.job;

import com.lbj.stock.service.StockTimerTaskService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定义股票相关数据的定时任务
 * @author laofang
 */
@Component
public class StockJob {

    @Autowired
    private StockTimerTaskService stockTimerTaskService;

    @XxlJob("myJobHandler")
    public void demoJobHandler() throws Exception {
        System.out.println("当前时间点为: " + DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 定义定时任务，采集国内每小时前半小时大盘数据
     */
    @XxlJob("getStockInnerMarketInfos")
    public void getStockInnerMarketInfos(){
        stockTimerTaskService.getInnerMarketInfo();
    }

    /**
     * 定时采集A股个股数据
     */
    @XxlJob("getStockInfos")
    public void getStockInfos(){
        stockTimerTaskService.getStockRtIndex();
    }

    @XxlJob("getBlockRtInfos")
    public void getStockBlockInfoTask(){
        stockTimerTaskService.getBlockRtInfo();
    }
}
