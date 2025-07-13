package com.truthbean.debbie.spring;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.boot.ApplicationBootContext;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieExitedApplication;
import com.truthbean.debbie.boot.DebbieStartedApplication;

import java.util.function.Consumer;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/02/03 10:26.
 */
public class SpringDebbieApplication implements DebbieApplication {

    private final SpringApplicationFactory applicationContext;

    public SpringDebbieApplication(SpringApplicationFactory applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public DebbieStartedApplication start() {
        LOGGER.info("debbie start by spring");
        return this;
    }

    @Override
    public DebbieApplication then(Consumer<ApplicationBootContext> applicationBootContextConsumer) {
        return this;
    }

    @Override
    public DebbieStartedApplication afterStarted(Consumer<ApplicationBootContext> applicationBootContextConsumer) {
        return this;
    }

    @Override
    public DebbieExitedApplication exit() {
        return this;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringDebbieApplication.class);
}
