package com.truthbean.debbie.shardingsphere.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.shardingsphere.ShardingSphereConfiguration;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class ShardingSphereConfigurationTest {

    @Test
    public void testConfiguration(@BeanInject ShardingSphereConfiguration configuration) {
        Console.println(configuration);
        Console.println("yamlConfigLocation: " + configuration.getYamlConfigLocation());
        Console.println("databaseName: " + configuration.getDatabaseName());
        Console.println("modeType: " + configuration.getModeType());
        Console.println("modeRepositoryType: " + configuration.getModeRepositoryType());
        Console.println("modeRepositoryFilePath: " + configuration.getModeRepositoryFilePath());
        Console.println("modeOverwrite: " + configuration.isModeOverwrite());
        Console.println("sqlShow: " + configuration.isSqlShow());
        Console.println("sqlSimple: " + configuration.isSqlSimple());
    }
}