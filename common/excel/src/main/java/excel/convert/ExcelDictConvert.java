package excel.convert;

import cn.hutool.core.annotation.AnnotationUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import excel.annotation.ExcelDictFormat;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * 字典格式化转换处理
 *
 * @author hxy
 */
@Slf4j
public class ExcelDictConvert implements Converter<Object> {

    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

//    @Override
//    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
//        ExcelDictFormat anno = getAnnotation(contentProperty.getField());
//        String type = anno.dictType();
//        String label = cellData.getStringValue();
//        String value;
//        if (StringUtils.isBlank(type)) {
//            value = ExcelUtil.reverseByExp(label, anno.readConverterExp(), anno.separator());
//        } else {
//            value = SpringUtils.getBean(DictService.class).getDictValue(type, label, anno.separator());
//        }
//        return Convert.convert(contentProperty.getField().getType(), value);
//    }
//
//    @Override
//    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
//        if (ObjectUtil.isNull(object)) {
//            return new WriteCellData<>("");
//        }
//        ExcelDictFormat anno = getAnnotation(contentProperty.getField());
//        String type = anno.dictType();
//        String value = Convert.toStr(object);
//        String label;
//        if (StringUtils.isBlank(type)) {
//            label = ExcelUtil.convertByExp(value, anno.readConverterExp(), anno.separator());
//        } else {
//            label = SpringUtils.getBean(DictService.class).getDictLabel(type, value, anno.separator());
//        }
//        return new WriteCellData<>(label);
//    }

    private ExcelDictFormat getAnnotation(Field field) {
        return AnnotationUtil.getAnnotation(field, ExcelDictFormat.class);
    }
}
