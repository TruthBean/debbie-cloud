package com.truthbean.debbie.spring.check;

import com.truthbean.Console;
import org.springframework.stereotype.Component;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.6
 */
@Component
public class EmptySpringBean {
    public EmptySpringBean() {
        Console.info("EmptySpringBean created by spring");
    }
}
