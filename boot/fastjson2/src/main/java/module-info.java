/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
module com.truthbean.debbie.fastjson2 {
    exports com.truthbean.debbie.fastjson2;
    exports com.truthbean.debbie.fastjson2.data;
    exports com.truthbean.debbie.data.transformer.text.fastjson2;

    requires transitive com.truthbean.debbie.core;
    requires transitive com.alibaba.fastjson2;
}