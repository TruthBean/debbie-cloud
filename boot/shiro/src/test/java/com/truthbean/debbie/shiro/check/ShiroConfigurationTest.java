package com.truthbean.debbie.shiro.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.shiro.ShiroConfiguration;
import com.truthbean.debbie.shiro.ShiroSecurityManagerFactory;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class ShiroConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject ShiroConfiguration configuration) {
        Console.println(configuration);
        Console.println("iniConfigPath: " + configuration.getIniConfigPath());
        Console.println("sessionTimeout: " + configuration.getSessionTimeout());
    }

    @Test
    public void testSecurityManagerFactory(@BeanInject ShiroSecurityManagerFactory factory) {
        Console.println(factory);
        Console.println(factory.getConfiguration());
    }
}