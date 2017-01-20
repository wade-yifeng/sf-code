package cn.sf.utils.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Paging<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long total;
    private List<T> data;

    private Paging() {}

    public Paging(Long total, List<T> data) {
        this.data = data;
        this.total = total;
    }

    public List<T> getData() {
        return this.data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Long getTotal() {
        return this.total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Boolean isEmpty() {
        return Boolean.valueOf(Objects.equals(Long.valueOf(0L), this.total) || this.data == null || this.data.isEmpty());
    }

    public static <T> Paging<T> empty() {
        return new Paging(Long.valueOf(0L), Collections.emptyList());
    }
}