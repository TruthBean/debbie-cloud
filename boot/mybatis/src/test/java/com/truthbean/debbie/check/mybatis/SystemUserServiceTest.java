package com.truthbean.debbie.check.mybatis;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-06-24 16:49.
 */
@DebbieApplicationTest
public class SystemUserServiceTest {

    @BeanInject
    private SystemUserService systemUserService;

    public void setSystemUserService(SystemUserService systemUserService) {
        this.systemUserService = systemUserService;
    }

    @Test
    void selectAll() {
        List<SystemUser> systemUsers = systemUserService.selectAll();
        Console.println(systemUsers);
    }
}