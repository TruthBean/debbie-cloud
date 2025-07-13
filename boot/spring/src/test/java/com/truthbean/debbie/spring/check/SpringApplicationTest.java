package com.truthbean.debbie.spring.check;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.DebbieScan;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.event.DebbieReadyEvent;
import com.truthbean.debbie.event.EventMethodListener;
import com.truthbean.debbie.spring.EnableDebbieApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author TruthBean
 * @since 0.5.3
 */
@SpringBootApplication
@EnableDebbieApplication(scan = @DebbieScan(basePackages = "com.truthbean"))
public class SpringApplicationTest {

    static {
        System.setProperty(DebbieApplication.DISABLE_DEBBIE, "false");
        System.setProperty("debbie.spring.enable", "false");
        System.setProperty("logging.level.root", "info");
        System.setProperty("logging.level.com.truthbean", "debug");
        System.setProperty("logging.level.org.springframework", "info");
    }

    @Autowired
    private EmptySpringBean springBean;

    @BeanInject
    private EmptyDebbieBean debbieBean;

    @EventMethodListener
    public void onDebbieReadyEvent(DebbieReadyEvent event) {
        Console.info("springBean: " + springBean);
        Console.info("debbieBean:" + debbieBean);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringApplicationTest.class, args);
    }
}
