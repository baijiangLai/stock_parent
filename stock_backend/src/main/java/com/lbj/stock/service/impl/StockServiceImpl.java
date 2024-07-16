package com.lbj.stock.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lbj.stock.mapper.StockBlockRtInfoMapper;
import com.lbj.stock.mapper.StockMarketIndexInfoMapper;
import com.lbj.stock.config.StockInfoConfig;
import com.lbj.stock.mapper.StockRtInfoMapper;
import com.lbj.stock.pojo.domain.*;
import com.lbj.stock.vo.resp.PageResult;
import com.lbj.stock.vo.resp.R;
import com.lbj.stock.vo.resp.ResponseCode;
import com.lbj.stock.service.StockService;
import com.lbj.stock.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service("stockService")
@Slf4j
public class StockServiceImpl implements StockService {
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;
    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;
    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;
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

    /**
     * 分页查询股票最新数据，并按照涨幅排序查询
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public R<PageResult> getStockPageInfo(Integer page, Integer pageSize) {
        //1.设置PageHelper分页参数
        PageHelper.startPage(page,pageSize);
        //2.获取当前最新的股票交易时间点
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //todo
        curDate= DateTime.parse("2022-06-07 15:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //3.调用mapper接口查询
        List<StockUpdownDomain> infos= stockRtInfoMapper.getNewestStockInfo(curDate);
        if (CollectionUtil.isEmpty(infos)) {
            return R.error(ResponseCode.NO_RESPONSE_DATA);
        }
        //4.组装PageInfo对象，获取分页的具体信息,因为PageInfo包含了丰富的分页信息，而部分分页信息是前端不需要的
        //PageInfo<StockUpdownDomain> pageInfo = new PageInfo<>(infos);
//        PageResult<StockUpdownDomain> pageResult = new PageResult<>(pageInfo);
        PageResult<StockUpdownDomain> pageResult = new PageResult<>(new PageInfo<>(infos));
        //5.封装响应数据
        return R.ok(pageResult);
    }

    @Override
    public R<List<StockUpdownDomain>> getIncreaseTop4() {
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        curDate= DateTime.parse("2022-06-07 15:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        List<StockUpdownDomain> data = stockRtInfoMapper.getIncreaseTop4(curDate);
        if (CollectionUtil.isEmpty(data)){
            return R.error(ResponseCode.NO_RESPONSE_DATA);
        }
        return R.ok(data);
    }

    @Override
    public R<Map> getStockUpDownCount() {
        //1.获取最新的交易时间范围 openTime  curTime
        //1.1 获取最新股票交易时间点
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date curTime = curDateTime.toDate();
        //TODO
        curTime= DateTime.parse("2022-01-06 14:25:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //1.2 获取最新交易时间对应的开盘时间
        DateTime openDate = DateTimeUtil.getOpenDate(curDateTime);
        Date openTime = openDate.toDate();
        //TODO
        openTime= DateTime.parse("2022-01-06 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.查询涨停数据
        //约定mapper中flag入参： 1: 涨停数据 0: 跌停
        List<Map> upCounts=stockRtInfoMapper.getStockUpDownCount(openTime,curTime,1);
        //3.查询跌停数据
        List<Map> dwCounts=stockRtInfoMapper.getStockUpDownCount(openTime,curTime,0);
        //4.组装数据
        HashMap<String, List> mapInfo = new HashMap<>();
        mapInfo.put("upList",upCounts);
        mapInfo.put("downList",dwCounts);
        //5.返回结果
        return R.ok(mapInfo);
    }

    @Override
    public void stockExport(HttpServletResponse response, Integer page, Integer pageSize) {
        try {
            //1.获取最近最新的一次股票有效交易时间点（精确分钟）
            Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
            //因为对于当前来说，我们没有实现股票信息实时采集的功能，所以最新时间点下的数据
            //在数据库中是没有的，所以，先临时指定一个假数据,后续注释掉该代码即可
            curDate=DateTime.parse("2022-01-05 09:47:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
            //2.设置分页参数 底层会拦截mybatis发送的sql，并动态追加limit语句实现分页
            PageHelper.startPage(page,pageSize);
            //3.查询
//            List<StockUpdownDomain> infos=stockRtInfoMapper.getAllStockUpDownByTime(curDate);
            List<StockUpdownDomain> infos = getStockPageInfo(page, pageSize).getData().getRows();
            //如果集合为空，响应错误提示信息
            if (CollectionUtil.isEmpty(infos)) {
                //响应提示信息
//                RequestInfoUtil.setUtf8(response);
                R<Object> r = R.error(ResponseCode.NO_RESPONSE_DATA);
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write(new ObjectMapper().writeValueAsString(r));
                return;
            }
            //设置响应excel文件格式类型
            response.setContentType("application/vnd.ms-excel");
            //2.设置响应数据的编码格式
            response.setCharacterEncoding("utf-8");
            //3.设置默认的文件名称
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("股票信息表", "UTF-8");
            //设置默认文件名称：兼容一些特殊浏览器
            response.setHeader("content-disposition", "attachment;filename=" + fileName + ".xlsx");
            //4.响应excel流
            EasyExcel
                    .write(response.getOutputStream(),StockUpdownDomain.class)
                    .sheet("股票信息")
                    .doWrite(infos);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("当前导出数据异常，当前页：{},每页大小：{},异常信息：{}",page,pageSize,e.getMessage());
        }
    }

    @Override
    public R<Map> stockTradeVol4InnerMarket() {
        //1.获取T日和T-1日的开始时间和结束时间
        //1.1 获取最近股票有效交易时间点--T日时间范围
        DateTime lastDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        DateTime openDateTime = DateTimeUtil.getOpenDate(lastDateTime);
        //转化成java中Date,这样jdbc默认识别
        Date curStartTime = openDateTime.toDate();
        Date curEndTime=lastDateTime.toDate();
        //TODO  mock数据
        curStartTime=DateTime.parse("2022-01-03 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        curEndTime=DateTime.parse("2022-01-03 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //1.2 获取T-1日的区间范围
        //获取lastDateTime的上一个股票有效交易日
        DateTime preLastDateTime = DateTimeUtil.getPreviousTradingDay(lastDateTime);
        DateTime preOpenDateTime = DateTimeUtil.getOpenDate(preLastDateTime);
        //转化成java中Date,这样jdbc默认识别
        Date preStartTime = preOpenDateTime.toDate();
        Date preEndTime=preLastDateTime.toDate();
        //TODO  mock数据
        preStartTime=DateTime.parse("2022-01-02 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        preEndTime=DateTime.parse("2022-01-02 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //2.获取上证和深证的配置的大盘id
        //2.1 获取大盘的id集合
        List<String> markedIds = stockInfoConfig.getInner();
        //3.分别查询T日和T-1日的交易量数据，得到两个集合
        //3.1 查询T日大盘交易统计数据
        List<Map> tData=stockMarketIndexInfoMapper.getSumAmtInfo(markedIds,curStartTime,curEndTime);
        if (CollectionUtil.isEmpty(tData)) {
            tData=new ArrayList<>();
        }
        //3.2 查询T-1日大盘交易统计数据
        List<Map> preTData=stockMarketIndexInfoMapper.getSumAmtInfo(markedIds,preStartTime,preEndTime);
        if (CollectionUtil.isEmpty(preTData)) {
            preTData=new ArrayList<>();
        }
        //4.组装响应数据
        HashMap<String, List> info = new HashMap<>();
        info.put("amtList",tData);
        info.put("yesAmtList",preTData);
        //5.返回数据
        return R.ok(info);
    }

    @Override
    public R<Map> stockUpDownScopeCount() {
        //1.获取股票最新一次交易的时间点
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //mock data
        curDate=DateTime.parse("2022-01-06 09:55:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.查询股票信息
        List<Map> maps=stockRtInfoMapper.getStockUpDownSectionByTime(curDate);
        //2.1 获取有序的标题集合
        List<String> orderSections = stockInfoConfig.getUpDownRange();
        List<Map> orderMaps  =  orderSections.stream().map(title->{
            Map mp=null;
            Optional<Map> op = maps.stream().filter(m -> m.containsValue(title)).findFirst();
            //判断是否存在符合过滤条件的元素
            if (op.isPresent()) {
                mp=op.get();
            }else{
                mp=new HashMap();
                mp.put("count",0);
                mp.put("title",title);
            }
            return mp;
        }).collect(Collectors.toList());
        //3.组装数据
        HashMap<String, Object> mapInfo = new HashMap<>();
        //获取指定日期格式的字符串
        String curDateStr = new DateTime(curDate).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        mapInfo.put("time",curDateStr);
        mapInfo.put("infos",orderMaps);
        //4.返回数据
        return R.ok(mapInfo);
    }

    @Override
    public R<List<Stock4MinuteDomain>> stockScreenTimeSharing(String code) {
        //1.获取最近最新的交易时间点和对应的开盘日期
        //1.1 获取最近有效时间点
        DateTime lastDate4Stock = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date endTime = lastDate4Stock.toDate();
        //TODO mockdata
        endTime=DateTime.parse("2021-12-30 14:47:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //1.2 获取最近有效时间点对应的开盘日期
        DateTime openDateTime = DateTimeUtil.getOpenDate(lastDate4Stock);
        Date startTime = openDateTime.toDate();
        //TODO MOCK DATA
        startTime=DateTime.parse("2021-12-30 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.根据股票code和日期范围查询
        List<Stock4MinuteDomain> list=stockRtInfoMapper.getStockInfoByCodeAndDate(code,startTime,endTime);
        //判断非空处理
        if (CollectionUtil.isEmpty(list)) {
            list=new ArrayList<>();
        }
        //3.返回响应数据
        return R.ok(list);
    }


    @Override
    public R<List<Stock4EvrDayDomain>> stockScreenDkLine(String code) {
        //1.获取查询的日期范围
        //1.1 获取截止时间
        DateTime endDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date endTime = endDateTime.toDate();
        //TODO MOCKDATA
        endTime=DateTime.parse("2022-01-07 15:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //1.2 获取开始时间
        DateTime startDateTime = endDateTime.minusDays(10);
        Date startTime = startDateTime.toDate();
        //TODO MOCKDATA
        startTime=DateTime.parse("2022-01-01 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.调用mapper接口获取查询的集合信息-方案1
        List<Stock4EvrDayDomain> data= stockRtInfoMapper.getStockInfo4EvrDay(code,startTime,endTime);
        //3.组装数据，响应
        return R.ok(data);
    }


}
