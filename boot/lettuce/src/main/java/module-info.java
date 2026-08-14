module com.truthbean.debbie.lettuce {
    exports com.truthbean.debbie.lettuce;

    requires transitive com.truthbean.debbie.core;
    requires static lettuce.core;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.lettuce.LettuceModuleStarter;
}