package com.lbj.stock;

import cn.hutool.core.util.ArrayUtil;
import com.lbj.stock.mapper.StockBusinessMapper;
import com.lbj.stock.service.StockTimerTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockTimerTaskServiceTest {
    @Autowired
    private StockTimerTaskService stockTimerTaskService;
    @Autowired
    private StockBusinessMapper stockBusinessMapper;
    @Test
    public void testGetData() {
        stockTimerTaskService.getOuterMarketInfo();
    }

    @Test
    public void testMapper() {
        List<String> stockIds = stockBusinessMapper.getStockIds();
        System.out.println(stockIds);
    }
}