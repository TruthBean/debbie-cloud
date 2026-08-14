module com.truthbean.cloud.gateway {
    exports com.truthbean.cloud.gateway;

    requires transitive com.truthbean.debbie.core;
    requires transitive com.truthbean.debbie.mvc;
    requires java.net.http;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.cloud.gateway.GatewayModuleStarter;
}