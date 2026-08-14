module com.truthbean.debbie.rocketmq {
    exports com.truthbean.debbie.rocketmq;

    requires transitive com.truthbean.debbie.core;
    requires static rocketmq.client;
    requires static rocketmq.common;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.rocketmq.RocketMqModuleStarter;
}