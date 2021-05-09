package com.truthbean.debbie.javafx;

import com.truthbean.debbie.annotation.AliasFor;
import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanCondition;
import com.truthbean.debbie.bean.BeanType;

import java.lang.annotation.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent(type = BeanType.SINGLETON, name = "primaryStage")
public @interface DebbieJavaFx {

    /**
     * @return @see BeanComponent#conditions
     */
    @AliasFor(attribute = "conditions", annotation = BeanComponent.class)
    Class<? extends BeanCondition>[] conditions() default {};
}