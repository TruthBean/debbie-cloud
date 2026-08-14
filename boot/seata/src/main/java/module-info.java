module com.truthbean.debbie.seata {
    exports com.truthbean.debbie.seata;

    requires transitive com.truthbean.debbie.core;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.seata.SeataModuleStarter;
}