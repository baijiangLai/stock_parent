package com.lbj.stock.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.lbj.stock.constant.ParseType;
import com.lbj.stock.mapper.StockBlockRtInfoMapper;
import com.lbj.stock.mapper.StockBusinessMapper;
import com.lbj.stock.mapper.StockMarketIndexInfoMapper;
import com.lbj.stock.mapper.StockRtInfoMapper;
import com.lbj.stock.pojo.entity.StockBlockRtInfo;
import com.lbj.stock.pojo.entity.StockMarketIndexInfo;
import com.lbj.stock.pojo.entity.StockRtInfo;
import com.lbj.stock.pojo.vo.StockInfoConfig;
import com.lbj.stock.service.StockTimerTaskService;
import com.lbj.stock.utils.DateTimeUtil;
import com.lbj.stock.utils.IdWorker;
import com.lbj.stock.utils.ParserStockInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("stockTimerTaskService")
@Slf4j
public class StockTimerTaskServiceImpl implements StockTimerTaskService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StockInfoConfig stockInfoConfig;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;

    @Autowired
    private ParserStockInfoUtil parserStockInfoUtil;

    @Autowired
    private StockBusinessMapper stockBusinessMapper;

    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;

    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    private HttpEntity<Object> httpEntity;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public void getInnerMarketInfo() {
        //1.定义采集的url接口
        String url=stockInfoConfig.getMarketUrl() + String.join(",",stockInfoConfig.getInner());
        //2.调用restTemplate采集数据
        //2.3 resetTemplate发起请求
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        int statusCode = responseEntity.getStatusCodeValue();
        if (statusCode != 200) {
            log.error("当前时间点:{},采集数据失败,状态码:{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"), statusCode);
            return;
        }

        String body = responseEntity.getBody();
        log.info("当前采集的数据：{}",body);
        //log.info("当前采集的数据：{}",resString);
        //3.数据解析（重要）
//        var hq_str_sh000001="上证指数,3267.8103,3283.4261,3236.6951,3290.2561,3236.4791,0,0,402626660,398081845473,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2022-04-07,15:01:09,00,";
//        var hq_str_sz399001="深证成指,12101.371,12172.911,11972.023,12205.097,11971.334,0.000,0.000,47857870369,524892592190.995,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,2022-04-07,15:00:03,00";
        String reg="var hq_str_(.+)=\"(.+)\";";
        //编译表达式,获取编译对象
        Pattern pattern = Pattern.compile(reg);
        //匹配字符串
        Matcher matcher = pattern.matcher(body);
        ArrayList<StockMarketIndexInfo> list = new ArrayList<>();
        //判断是否有匹配的数值
        while (matcher.find()){
            //获取大盘的code
            String marketCode = matcher.group(1);
            //获取其它信息，字符串以逗号间隔
            String otherInfo=matcher.group(2);
            //以逗号切割字符串，形成数组
            String[] splitArr = otherInfo.split(",");
            //大盘名称
            String marketName=splitArr[0];
            //获取当前大盘的开盘点数
            BigDecimal openPoint=new BigDecimal(splitArr[1]);
            //前收盘点
            BigDecimal preClosePoint=new BigDecimal(splitArr[2]);
            //获取大盘的当前点数
            BigDecimal curPoint=new BigDecimal(splitArr[3]);
            //获取大盘最高点
            BigDecimal maxPoint=new BigDecimal(splitArr[4]);
            //获取大盘的最低点
            BigDecimal minPoint=new BigDecimal(splitArr[5]);
            //获取成交量
            Long tradeAmt=Long.valueOf(splitArr[8]);
            //获取成交金额
            BigDecimal tradeVol=new BigDecimal(splitArr[9]);
            //时间
            Date curTime = DateTimeUtil.getDateTimeWithoutSecond(splitArr[30] + " " + splitArr[31]).toDate();
            //组装entity对象
            StockMarketIndexInfo info = StockMarketIndexInfo.builder()
                    .id(idWorker.nextId())
                    .marketCode(marketCode)
                    .marketName(marketName)
                    .curPoint(curPoint)
                    .openPoint(openPoint)
                    .preClosePoint(preClosePoint)
                    .maxPoint(maxPoint)
                    .minPoint(minPoint)
                    .tradeVolume(tradeVol)
                    .tradeAmount(tradeAmt)
                    .curTime(curTime)
                    .build();
            //收集封装的对象，方便批量插入
            list.add(info);
        }
        log.info("采集的当前大盘数据：{}",list);
        //批量插入
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        //TODO 后续完成批量插入功能
        int count = stockMarketIndexInfoMapper.insertBatch(list);
        if (count > 0) {
            log.info("批量插入了：{}条数据", count);
            //通知backend刷新缓存
            rabbitTemplate.convertAndSend("stockExchange", "inner.market", new Date());
        }
    }

    @Override
    public void getStockRtIndex() {
        //批量获取股票ID集合
        List<String> stockIds = stockBusinessMapper.getStockIds();
        //计算出符合sina命名规范的股票id数据
        stockIds = stockIds.stream().map(id -> id.startsWith("6") ? "sh" + id : "sz" + id).collect(Collectors.toList());
        //一次性查询过多，我们将需要查询的数据先进行分片处理，每次最多查询20条股票数据
        Lists.partition(stockIds,15).forEach(codes->{
            threadPoolTaskExecutor.execute(() ->{
                //拼接股票url地址
                String stockUrl=stockInfoConfig.getMarketUrl()+String.join(",",codes);
                //2.调用restTemplate采集数据
                //2.3 resetTemplate发起请求
                ResponseEntity<String> responseEntity = restTemplate.exchange(stockUrl, HttpMethod.GET, httpEntity, String.class);

                int statusCode = responseEntity.getStatusCodeValue();
                if (statusCode != 200) {
                    log.error("当前时间点:{},采集数据失败,状态码:{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"), statusCode);
                    return;
                }

                String body = responseEntity.getBody();
                List<StockRtInfo> infos = parserStockInfoUtil.parser4StockOrMarketInfo(body, ParseType.ASHARE);
                log.info("数据量：{}",infos.size());
                log.info("数据是: {}", Arrays.toString(infos.toArray()));

                int count = stockRtInfoMapper.insertBatch(infos);
                if (count > 0) {
                    log.info("A股个股插入数据: {}", count);
                }
            });
        });
    }

    @Override
    public void getBlockRtInfo() {
        String blockUrl = stockInfoConfig.getBlockUrl();;
        ResponseEntity<String> responseEntity = restTemplate.exchange(blockUrl, HttpMethod.GET, httpEntity, String.class);
        int statusCode = responseEntity.getStatusCodeValue();
        if (statusCode != 200) {
            log.error("当前时间点:{},采集数据失败,状态码:{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"), statusCode);
        }
        String body = responseEntity.getBody();
        List<StockBlockRtInfo> stockBlockRtInfos = parserStockInfoUtil.parse4StockBlock(body);
        Lists.partition(stockBlockRtInfos, 10).forEach(info->{
            int count = stockBlockRtInfoMapper.insertBatch(info);
            if (count > 0) {
                log.info("A股板块插入数据: {}", count);
            }
        });


    }

    @PostConstruct
    public void initHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        //必须填写，否则数据采集不到
        headers.add("Referer","https://finance.sina.com.cn/stock/");
        headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        //2.2 组装请求对象
        httpEntity = new HttpEntity<>(headers);
    }
}
