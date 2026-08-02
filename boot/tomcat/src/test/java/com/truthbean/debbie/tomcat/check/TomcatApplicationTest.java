package com.truthbean.debbie.tomcat.check;

import com.truthbean.Console;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

@DebbieApplicationTest
public class TomcatApplicationTest {

    @Test
    public void content() {
        Console.println("nothing");
    }
}
