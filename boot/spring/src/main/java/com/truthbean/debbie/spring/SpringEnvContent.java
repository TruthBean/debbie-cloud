package com.truthbean.debbie.spring;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.environment.Environment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.Properties;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/02/03 10:09.
 */
public class SpringEnvContent implements Environment {

    private final ConfigurableEnvironment environment;

    public SpringEnvContent(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public Properties properties() {
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

    @Override
    public void clear() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringEnvContent.class);
}
