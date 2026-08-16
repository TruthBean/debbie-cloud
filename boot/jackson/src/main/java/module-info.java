import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.data.JsonHelper;
import com.truthbean.debbie.data.XmlHelper;
import com.truthbean.debbie.jackson.data.JacksonJsonHelper;
import com.truthbean.debbie.jackson.data.JacksonXmlHelper;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/01/03 12:12.
 */
module com.truthbean.debbie.jackson {
    requires transitive com.truthbean.debbie.core;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive com.fasterxml.jackson.dataformat.xml;
    requires transitive com.fasterxml.jackson.dataformat.yaml;
    requires transitive com.fasterxml.jackson.datatype.jsr310;
    // requires com.fasterxml.jackson.module.jaxb;
    requires org.yaml.snakeyaml;

    exports com.truthbean.debbie.jackson.util;
    exports com.truthbean.debbie.data.serialize.jackson;
    exports com.truthbean.debbie.jackson.data;

    provides DebbieModuleStarter
            with com.truthbean.debbie.jackson.JacksonModuleStarter;

    provides JsonHelper
            with JacksonJsonHelper;

    provides XmlHelper
            with JacksonXmlHelper;
}