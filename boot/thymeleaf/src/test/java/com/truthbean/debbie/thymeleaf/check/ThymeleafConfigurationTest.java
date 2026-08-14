package com.truthbean.debbie.thymeleaf.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DebbieApplicationTest
public class ThymeleafConfigurationTest {

    @Test
    public void testTemplateEngine(@BeanInject(value = "thymeleafTemplateEngine") TemplateEngine templateEngine) {
        Console.println(templateEngine);
        Console.println("templateEngine is not null: " + (templateEngine != null));
    }

    @Test
    public void testTemplateEngineCreated() {
        var templateEngine = new TemplateEngine();
        Console.println(templateEngine);
        Console.println("templateEngine is not null: " + (templateEngine != null));
    }
}