package com.lbj.stock.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.lbj.stock.mapper.StockBlockRtInfoMapper;
import com.lbj.stock.mapper.StockMarketIndexInfoMapper;
import com.lbj.stock.config.StockInfoConfig;
import com.lbj.stock.pojo.domain.InnerMarketDomain;
import com.lbj.stock.pojo.domain.StockBlockDomain;
import com.lbj.stock.vo.resp.R;
import com.lbj.stock.vo.resp.ResponseCode;
import com.lbj.stock.service.StockService;
import com.lbj.stock.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("stockService")
@Slf4j
public class StockServiceImpl implements StockService {
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;
    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;
    @Autowired
    private StockInfoConfig stockInfoConfig;
    @Override
    public R<List<InnerMarketDomain>> innerIndexAll() {
        //1.获取国内A股大盘的id集合
        List<String> innerCodes = stockInfoConfig.getInner();
        //2.获取最近股票交易日期
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //TODO mock测试数据，后期数据通过第三方接口动态获取实时数据 可删除
        curDate=DateTime.parse("2022-01-02 09:32:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //3.将获取的java Date传入接口
        List<InnerMarketDomain> list= stockMarketIndexInfoMapper.getMarketInfo(innerCodes,curDate);
        //4.返回查询结果
        return R.ok(list);
    }

    @Override
    public R<List<StockBlockDomain>> sectorAllLimit() {
        //获取股票最新交易时间点
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //TODO mock数据,后续删除
        curDate=DateTime.parse("2021-12-21 14:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //1.调用mapper接口获取数据
        List<StockBlockDomain> data=stockBlockRtInfoMapper.sectorAllLimit(curDate);
        //2.组装数据
        if (CollectionUtil.isEmpty(data)) {
            return R.error(ResponseCode.NO_RESPONSE_DATA.getMessage());
        }
        return R.ok(data);
    }
}
