package utils;


import domain.BaseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树形结构工具类
 * T 继承了BaseTree的实体对象，实体对象中需要有表示父子关系的id和pid
 * N id和pid的类型
 * 由于 实体对象中表示父子关系的属性名称不一定是id和pid，所以提供了getId和getPid方法
 *
 * @author
 */
public abstract class TreeUtil<T extends BaseTree<T>, N> {


    /**
     * id的get方法
     *
     * @param t 对象
     * @return id 的值
     */
    protected abstract N getId(T t);

    /**
     * 父级id的get方法
     *
     * @param t 对象
     * @return pid 的值
     */
    protected abstract N getPid(T t);

    /**
     * 格式化成树形结构
     * 要点：非基本类型的变量存储的是内存地址，对它的操作会影响到原始值。
     *
     * @param list 要操作的列表
     * @return 树形结果列表
     */
    public List<T> parseTree(List<T> list) {
        List<T> resultList = new ArrayList<>();
        Map<N, T> tmpMap = new HashMap<>();

        for (T t : list) {
            tmpMap.put(this.getId(t), t);
        }

        for (T t : list) {
            /*1、tmpMap存储的是以id为key的键值对。
               2、如果以pid为key可以从tmpMap中取出对象，则说明该元素是子级元素。
               3、如果以pid为key不能从tmpMap中取出对象，则说明该元素是最上层元素。
               4、子级元素放在父级元素的children中。
               5、最上层元素添加到结果中*/
            if (tmpMap.get(this.getPid(t)) != null) {
                T tmap = tmpMap.get(this.getPid(t));
                // 初始化children
                if (null == tmap.getChildren()) {
                    tmap.setChildren(new ArrayList<>());
                }
                tmap.getChildren().add(t);
            } else {
                resultList.add(t);
            }
        }
        return resultList;
    }

    /**
     * 格式化成树形结构 且树皆为传入pid的子集以及本身
     * 要点：非基本类型的变量存储的是内存地址，对它的操作会影响到原始值。
     *
     * @param list 要操作的列表
     * @param n    格式化数据为pid的子集
     * @return 树形结果列表
     */
    public List<T> parseTree(List<T> list, N n) {
        List<T> child = getChild(list, n);
        T c = null;
        for (T t : list) {
            if (this.getId(t).equals(n)) {
                c = t;
            }
        }
        c.setChildren(child);
        T t = getParent(list, c);
        ArrayList<T> result = new ArrayList<>();
        result.add(t);
        return result;
    }

    private List<T> getChild(List<T> list, N n) {
        List<T> resultList = new ArrayList<>();
        for (T t : list) {
            //如果当前pid=数据的pid
            if (this.getPid(t).equals(n)) {
                List<T> ts = getChild(list, this.getId(t));
                if (!ts.isEmpty()) {
                    t.setChildren(ts);
                }
                resultList.add(t);
            }
        }
        return resultList;
    }

    private T getParent(List<T> list, T child) {
        for (T t : list) {
            if (this.getId(t).equals(this.getPid(child))) {
                ArrayList<T> ts = new ArrayList<>();
                ts.add(child);
                t.setChildren(ts);
                T parent = getParent(list, t);
                if (parent != null) {
                    return parent;
                } else {
                    return t;
                }
            }
        }
        return child;
    }

    public List<T> getLeafNode(List<T> dataRights, List<T> data) {
        if (dataRights == null || dataRights.size() == 0) return null;
        for (T dataRight : dataRights) {
            if (getLeafNode(dataRight.getChildren(), data) == null) data.add(dataRight);
        }
        return data;
    }

    public List<T> getLeafNode(List<T> dataRights) {
        return getLeafNode(dataRights, new ArrayList<>());
    }
}
