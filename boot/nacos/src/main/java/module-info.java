module com.truthbean.debbie.nacos {
    exports com.truthbean.debbie.nacos;

    requires transitive com.truthbean.debbie.core;
    // requires nacos.client;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.nacos.NacosModuleStarter;
}