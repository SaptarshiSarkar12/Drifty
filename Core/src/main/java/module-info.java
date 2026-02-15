module Core {
    requires com.google.gson;
    requires org.apache.commons.text;
    requires org.hildan.fxgson;
    requires org.apache.commons.io;
    requires org.slf4j;
    requires jproc;
    requires java.prefs;
    requires java.net.http;
    requires java.sql;
    exports init;
    exports settings;
    exports properties;
    exports support;
    exports updater;
    exports utils;
    exports data;
    opens support to com.google.gson;
}