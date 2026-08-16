/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
module com.truthbean.debbie.gson {
    exports com.truthbean.debbie.gson;
    exports com.truthbean.debbie.gson.data;
    exports com.truthbean.debbie.data.transformer.text.gson;

    requires transitive com.truthbean.debbie.core;
    requires transitive com.google.gson;
    provides com.truthbean.debbie.data.JsonHelper with com.truthbean.debbie.gson.data.GsonJsonHelper;
}