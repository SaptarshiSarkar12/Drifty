module Core {
    requires com.google.gson;
    requires org.apache.commons.text;
    requires org.hildan.fxgson;
    requires org.apache.commons.io;
    requires jproc;
    requires java.prefs;
    requires java.net.http;
    exports init;
    exports preferences;
    exports properties;
    exports support;
    exports utils;
    opens support to com.google.gson;
}