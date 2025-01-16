package system.demo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author hxy
 * @date 2025/1/6
 **/


@Data
public class ReportData {

    @ExcelProperty("年")
    private Integer year;

    @ExcelProperty("月")
    private Integer month;

    @ExcelProperty("品种名称")
    private String varietyName;

    @ExcelProperty("品种分类")
    private String varietyCategory;

    @ExcelProperty("品种")
    private String variety;

    @ExcelProperty("代表名称")
    private String representativeName;

    @ExcelProperty("是否合作")
    private String isCooperative;

    @ExcelProperty("上级姓名")
    private String superiorName;

    @ExcelProperty("终端名称")
    private String terminalName;

    @ExcelProperty("市场属性")
    private String marketAttribute;

    @ExcelProperty("一级分类")
    private String primaryCategory;

    @ExcelProperty("二级分类")
    private String secondaryCategory;

    @ExcelProperty("旧一级分类")
    private String oldPrimaryCategory;

    @ExcelProperty("旧二级分类")
    private String oldSecondaryCategory;

    @ExcelProperty("等级")
    private String grade;

    @ExcelProperty("市")
    private String city;

    @ExcelProperty("县")
    private String county;

    @ExcelProperty("乡")
    private String township;

    @ExcelProperty("数量")
    private Double quantity;

    @ExcelProperty("单价")
    private Double unitPrice;

    @ExcelProperty("金额")
    private Double amount;

    @ExcelProperty("跟标属性")
    private String standardAttribute;

    @ExcelProperty("日期")
    private String date;

    @ExcelProperty("商业")
    private String commerce;

    @ExcelProperty("佣金系数")
    private Double commissionCoefficient;

    @ExcelProperty("佣金")
    private Double commission;

    @ExcelProperty("代表编码")
    private String representativeCode;

    @ExcelProperty("备案人上级汇总")
    private String recorderSuperiorSummary;
}

