package com.truthbean.debbie.spring;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.env.EnvironmentContent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.Properties;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/02/03 10:09.
 */
public class SpringEnvContent implements EnvironmentContent {

    private final ConfigurableEnvironment environment;

    public SpringEnvContent(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public String getProfile() {
        return "spring";
    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        MutablePropertySources sources = environment.getPropertySources();
        for (PropertySource<?> source : sources) {
            String name = source.getName();
            properties.put(name, source.getProperty(name));
        }
        return properties;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringEnvContent.class);
}
