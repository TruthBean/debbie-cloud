package com.truthbean.debbie.spring.check;

import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieBootApplication;

/**
 * @author TruthBean
 * @since 0.5.3
 */
@DebbieBootApplication
public class DebbieApplicationTest {

    static {
        System.setProperty(DebbieApplication.DISABLE_DEBBIE, "true");
        System.setProperty("logging.level.com.truthbean", "debug");
        System.setProperty("logging.level.org.springframework", "debug");
    }

    public static void main(String[] args) {
        DebbieApplication.run(DebbieApplicationTest.class, args);
    }
}
