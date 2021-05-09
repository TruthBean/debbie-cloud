/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet.test;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-01-29 22:25
 */
@WebServlet(value = "/servlet/async/test")
public class TestAsyncServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1、支持异步处理asyncSupported=true
        //2、开启异步模式
        System.out.println("主线程开始。。。" + Thread.currentThread() + "==>" + System.currentTimeMillis());
        AsyncContext startAsync = req.startAsync();

        startAsync.setTimeout(4000L);

        //3、业务逻辑进行异步处理;开始异步处理
        startAsync.start(() -> {
            try {
                System.out.println("副线程开始。。。" + Thread.currentThread() + "==>" + System.currentTimeMillis());
                sayHello();
                startAsync.complete();
                //获取到异步上下文
                AsyncContext asyncContext = req.getAsyncContext();
                //4、获取响应
                ServletResponse response = asyncContext.getResponse();
                response.getWriter().write("hello async...");
                System.out.println("副线程结束。。。" + Thread.currentThread() + "==>" + System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.out.println("主线程结束。。。" + Thread.currentThread() + "==>" + System.currentTimeMillis());
    }

    public void sayHello() throws Exception {
        System.out.println(Thread.currentThread() + " processing...");
        Thread.sleep(3000);
    }
}
