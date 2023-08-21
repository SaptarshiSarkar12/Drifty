module Drifty {
    requires org.apache.commons.io;
    requires org.apache.commons.text;
    requires com.google.gson;
    requires org.apache.commons.lang3;
    requires jproc;
    requires javafx.graphics;
    requires javafx.controls;
    requires java.prefs;
    requires org.yaml.snakeyaml;
    requires org.hildan.fxgson;

    exports GUI.Forms to javafx.graphics;
}