module com.truthbean.debbie.sentinel {
    exports com.truthbean.debbie.sentinel;

    requires transitive com.truthbean.debbie.core;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.sentinel.SentinelModuleStarter;
}