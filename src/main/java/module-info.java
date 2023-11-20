module Drifty {
    requires org.apache.commons.text;
    requires org.apache.commons.io;
    requires com.google.gson;
    requires org.hildan.fxgson;
    requires jproc;
    requires javafx.graphics;
    requires javafx.controls;
    requires java.prefs;
    requires org.yaml.snakeyaml;
    requires org.jetbrains.annotations;
    requires java.desktop;

    exports GUI.Forms to javafx.graphics;
    exports GUI.Support;
    exports Enums;
    exports Utils;
    exports Backend;

    opens GUI.Support to com.google.gson;
    opens GUI.Forms to com.google.gson;
}