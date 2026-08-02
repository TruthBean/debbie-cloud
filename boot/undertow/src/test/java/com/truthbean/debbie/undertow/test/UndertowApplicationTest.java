package com.truthbean.debbie.undertow.test;

import com.truthbean.Console;
import com.truthbean.debbie.bean.DebbieScan;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

@DebbieApplicationTest(scan = @DebbieScan(basePackages = "com.truthbean"))
public class UndertowApplicationTest {

    @Test
    void content() {
        Console.println("nothing");
    }

    public static void main(String[] args) {
        DebbieApplication application = DebbieApplication.create(UndertowApplicationTest.class, args);
        application.start();
        application.exit();
    }
}
