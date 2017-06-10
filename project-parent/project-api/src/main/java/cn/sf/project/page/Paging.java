package cn.sf.project.page;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
public class Paging<T> implements Serializable {

    private Long total;
    private List<T> data;


    private Paging(Long total, List<T> data) {
        this.data = data;
        this.total = total;
    }

    public Boolean isEmpty() {
        return Objects.equals(0L, this.total) || this.data == null || this.data.isEmpty();
    }

    public static <T> Paging<T> empty() {
        return new Paging<T>(0L, Collections.emptyList());
    }

    public static <T> Paging<T> gen(Long total,List<T> data) {
        return new Paging<T>(total, data);
    }
}