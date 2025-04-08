module GUI {
    requires Core;
    requires javafx.graphics;
    requires javafx.controls;
    requires org.apache.commons.text;
    requires org.apache.commons.io;
    requires com.google.gson;
    requires org.hildan.fxgson;
    requires java.prefs;
    requires jproc;
    requires java.sql;

    exports gui.support;
    exports gui.utils;
    exports main to javafx.graphics;
    exports ui to javafx.graphics;

    opens gui.support to com.google.gson;
    opens ui to com.google.gson;
}