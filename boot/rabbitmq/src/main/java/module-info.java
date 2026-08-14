module com.truthbean.debbie.rabbitmq {
    exports com.truthbean.debbie.rabbitmq;

    requires transitive com.truthbean.debbie.core;
    requires static com.rabbitmq.client;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.rabbitmq.RabbitMqModuleStarter;
}