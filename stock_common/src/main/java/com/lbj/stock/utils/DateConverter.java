package com.lbj.stock.utils;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.metadata.data.WriteCellData;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 将时间格式转化
 */
public class DateConverter implements Converter<Date> {

    private static  final String PATTERN_YYYY_MM_DD = "yyyy-MM-dd HH:mm";


    @Override
    public Class<Date> supportJavaTypeKey() {
        return Date.class;
    }


    @Override
    public WriteCellData<String> convertToExcelData(WriteConverterContext<Date> context) throws Exception {
        Date date = context.getValue();
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_YYYY_MM_DD);
        return new WriteCellData<>(sdf.format(date));
    }
}
