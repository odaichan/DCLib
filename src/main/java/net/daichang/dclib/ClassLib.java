package net.daichang.dclib;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 此处是对类的一些修改
 * <p>
 * Here are some modifications to the class
 * */
public interface ClassLib extends BaseLib {
    default void backtrack(Class<?> caller) {
        try {
            Field[] fields = caller.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) && field.getType().getTypeName().equals("boolean")) {
                    field.setAccessible(true);
                    field.set(null, Boolean.valueOf(false));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    default void setClass(Object oldObject, Class<?> newClass) {
        Helper.setClass(oldObject, newClass);
    }
}
