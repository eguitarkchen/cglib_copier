package net.tt.xuanliu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @ClassName BeanUtils
 * @Description bean 工具
 * @Author liuxuan
 * @Date 2020/8/28 16:18
 * @Version 1.0
 **/
public class BeanUtils {


    /**
     * 缓存BeanCopier 对象 提升性能
     */
    private static final ConcurrentHashMap<String, org.springframework.cglib.beans.BeanCopier> BEAN_COPIER_MAP = new ConcurrentHashMap<>();


    /**
     * Bean属性复制工具方法。
     * @param sources   原始集合
     * @param supplier: 目标类::new(eg: UserVO::new)
     */
    public static <S, T> List<T> cgLibCopyList(List<S> sources, Supplier<T> supplier) {
        List<T> list = new ArrayList<>(sources.size());
        org.springframework.cglib.beans.BeanCopier beanCopier = null;
        for (S source : sources) {
            T t = supplier.get();
            if (beanCopier == null) {
                beanCopier = getBeanCopier(source.getClass(), t.getClass());
            }
            beanCopier.copy(source, t, null);
            list.add(t);
        }
        return list;
    }

    /**
     * Bean属性复制工具方法。
     * @param source    目标对象
     * @param supplier: 目标类::new(eg: UserVO::new)
     */
    public static <T> T cgLibCopyBean(Object source, Supplier<T> supplier) {
        T t = supplier.get();
        getBeanCopier(source.getClass(), t.getClass()).copy(source, t, null);
        return t;
    }


    //获取BeanCopier对象 如果缓存中有从缓存中获取 如果没有则新创建对象并加入缓存
    private static org.springframework.cglib.beans.BeanCopier getBeanCopier(Class<?> sourceClass, Class<?> targetClass) {
        String key = getKey(sourceClass.getName(), targetClass.getName());
        org.springframework.cglib.beans.BeanCopier beanCopier;
        beanCopier = BEAN_COPIER_MAP.get(key);
        if (beanCopier == null) {
            beanCopier = org.springframework.cglib.beans.BeanCopier.create(sourceClass, targetClass, false);
            BEAN_COPIER_MAP.put(key, beanCopier);
        }
        return beanCopier;
    }

    /**
     * 生成缓存key
     */
    private static String getKey(String sourceClassName, String targetClassName) {
        return sourceClassName + targetClassName;
    }


}

