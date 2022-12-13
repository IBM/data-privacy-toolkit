/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.task;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class TaskOptions {
    public void setOption(String optionName, Object optionValue) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        PropertyDescriptor pd = new PropertyDescriptor(optionName, this.getClass());
        Method setter = pd.getWriteMethod();
        setter.invoke(this, optionValue);
    }
}
