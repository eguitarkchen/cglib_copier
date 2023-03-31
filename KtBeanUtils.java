package com.kingint.base.commonxiruan.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author N_Xiang
 * @describe JavaBean工具类
 * @time 2021/11/10 11:21 下午
 */
public class KtBeanUtils {

    private static Map<String,Method> cachMap = new ConcurrentHashMap<>();

    /**
     * 仅仅拷贝本身
     * @param source
     * @param target
     */
    public static void copy(Object source ,Object target){
        toJavaBean(source,target,false);
    }

    /**
     * 仅仅拷贝本身
     * @param source
     * @param target
     * @param type 类型  true拷贝父类 false正常拷贝
     */
    public static void copy(Object source ,Object target,Boolean type){
        toJavaBean(source,target,type);
    }

    /**
     * Bean拷贝
     * @param source 元数据
     * @param target 目标数据
     * @param type 类型  true拷贝父类 false正常拷贝
     */
    private static void toJavaBean(Object source ,Object target,Boolean type){
        //Map<String, String> map = KtAliasPars.ParsAlias(source);
        Class<?> cTarget = target.getClass();
        Class<?> cSource = source.getClass();
        //获得某个类的所有声明的字段
        Field[] fields = cTarget.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            // 获得属性的首字母并转换为大写
            String firstLetter = fieldName.substring(0, 1).toUpperCase().concat(fieldName.substring(1));
            String setMethodName = "set" + firstLetter;
            String getMethodName = "get" + firstLetter;
           // if (map.containsKey(fieldName)){
                //取出某个字段对呀目标数据的字段名
               // String k = map.get(fieldName);
               // String kFirstLetter = k.substring(0,1).toUpperCase().concat(k.substring(1));
               // getMethodName = "get"+kFirstLetter;
//                setMethodName = "set"+kFirstLetter;
                //}

            try {
                //获取源对象的值
                Method cmethod = cachMap.getOrDefault(cSource.getName()+"@"+getMethodName,cSource.getMethod(getMethodName));
                Object cinvoke = cmethod.invoke(source);
                cachMap.put(cSource.getName()+"@"+getMethodName,cmethod);
                if (!isWrapClass(field.getType())){

                    if (field.getType().equals(List.class)){
                        List<Object> o = new ArrayList<>();
                        copyListProperties((List<Object>)cinvoke, o,getT(field));
                        cinvoke = o;
                    }else {
                        Object o = field.getType().newInstance();
                        toJavaBean(cinvoke,o,false);
                        cinvoke = o;
                    }
                }
                //调用方法写入对象
                Method setMethod = cachMap.getOrDefault(cTarget.getName()+"@"+setMethodName,cTarget.getMethod(setMethodName,
                        new Class[] { field.getType() }));
                cachMap.put(cTarget.getName()+"@"+setMethodName,setMethod);
                setMethod.invoke(target, new Object[] { cinvoke });


            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }

    private static boolean isWrapClass(Class clz) {
        if (clz.equals(String.class)){
            return true;
        }
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 带回调函数的集合数据的拷贝（可自定义字段拷贝规则）
     * @param sources : 数据源类
     * @param target : 目标类::new(eg: UserVO::new)
     * @return
     */
    public static void copyListProperties(List<Object> sources, List<Object> target,Class<?> targetType) {
        for (Object source : sources) {
            Object o = null;
            try {
                o = targetType.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            toJavaBean(source, o,false);
            target.add(o);
        }
    }

    /**
     * 获取list的范性
     * @param f list
     * @return
     */
    private static Class<?> getT(Field f){
        Type genericType = f.getGenericType();
        // 如果是泛型参数的类型
        if(genericType instanceof ParameterizedType){
            ParameterizedType pt = (ParameterizedType) genericType;
            //得到泛型里的class类型对象
            return (Class<?>)pt.getActualTypeArguments()[0];
        }
        return null;
    }

}

