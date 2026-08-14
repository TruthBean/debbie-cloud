module com.truthbean.debbie.shiro {
    exports com.truthbean.debbie.shiro;

    requires transitive com.truthbean.debbie.core;
    requires static org.apache.shiro.core;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.shiro.ShiroModuleStarter;
}