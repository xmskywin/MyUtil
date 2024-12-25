package domain;

import java.util.List;

/**
 * 树形结构基础类
 *
 * @param <T>
 * @author zzr 20200114
 */
public class BaseTree<T> {

    private List<T> children;

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }
}
