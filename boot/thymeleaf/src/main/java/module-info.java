module com.truthbean.debbie.thymeleaf {
    exports com.truthbean.debbie.thymeleaf;

    requires transitive com.truthbean.debbie.mvc;
    requires thymeleaf;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.thymeleaf.ThymeleafModuleStarter;
    provides com.truthbean.debbie.mvc.response.view.AbstractTemplateViewHandler
            with com.truthbean.debbie.thymeleaf.ThymeleafHandler;
}