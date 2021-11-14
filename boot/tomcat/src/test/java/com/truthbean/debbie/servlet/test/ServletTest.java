package com.truthbean.debbie.servlet.test;

import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

@DebbieApplicationTest
public class ServletTest {

    @Test
    public void content() {
        System.out.println("nothing");
    }
}
