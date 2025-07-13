package com.truthbean.debbie.spring.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanType;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.6
 */
@BeanComponent(type = BeanType.SINGLETON)
public class EmptyDebbieBean {
    public EmptyDebbieBean() {
        Console.info("EmptyDebbieBean created by debbie");
    }
}
