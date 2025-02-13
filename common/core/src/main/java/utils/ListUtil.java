package utils;

import cn.hutool.core.bean.BeanUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 工具类
 *
 * @author fcbd
 * @data 2023/11/21 18:23
 */
public class ListUtil<T> {

    /**
     * 列表复制
     *
     * @param obj      原数据
     * @param list2    目标数据
     * @param classObj 目标类型
     * @param <T>      list
     */
    public static <T> void copyList(Object obj, List<T> list2, Class<T> classObj) {
        if ((!Objects.isNull(obj)) && (!Objects.isNull(list2))) {
            List list1 = (List) obj;
            list1.forEach(item -> {
                try {
                    T data = classObj.newInstance();
                    BeanUtil.copyProperties(item, data);
                    list2.add(data);
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }
            });
        }
    }

    /**
     * * 将一个list均分成n个list,主要通过偏移量来实现的
     * * @param source
     * * @return
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        //如果source集合无数据，则返回空数组
        if (source == null || source.size() == 0) {
            return result;
        }
        int remaider = source.size() % n; //(先计算出余数)
        int number = source.size() / n; //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }
}
