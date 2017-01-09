package cn.sf.utils.bean;

import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;

public class BeanCopyUtil {


    public static <T> T emptyBean(Class<T> target){
        T temp;
        try {
            temp = target.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("没有默认构造方法", e);
        }
        return temp;
    }

    public static <S,T> T genBean(S source, Class<T> target){
        T temp;
        try {
            temp = target.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("没有默认构造方法", e);
        }
        BeanUtils.copyProperties(source, temp);
        return temp;
    }

    public static <S,T> List<T> genBeanList(List<S> sources, Class<T> target){
        if (sources==null||sources.size()==0) {
            return Collections.emptyList();
        }
        List<T> result = Lists.newArrayList();
        sources.forEach(s -> result.add(genBean(s, target)));
        return result;
    }


}
