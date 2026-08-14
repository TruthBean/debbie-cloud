module com.truthbean.debbie.pulsar {
    exports com.truthbean.debbie.pulsar;

    requires transitive com.truthbean.debbie.core;
    requires static pulsar.client.api;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.pulsar.PulsarModuleStarter;
}