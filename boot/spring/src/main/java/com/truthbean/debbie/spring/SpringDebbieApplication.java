package com.truthbean.debbie.spring;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.ApplicationContext;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/02/03 10:26.
 */
public class SpringDebbieApplication implements DebbieApplication {

    private final ApplicationContext applicationContext;

    public SpringDebbieApplication(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void start() {
        LOGGER.info("debbie start by spring");
    }

    @Override
    public void exit() {
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringDebbieApplication.class);
}
