package gui.utils;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import main.Drifty_GUI;

import java.util.Objects;

import static gui.support.Constants.MONACO_TTF;

public class UIComponentBuilder {
    private static UIComponentBuilder instance;

    public static UIComponentBuilder getInstance() {
        if (instance == null) {
            instance = new UIComponentBuilder();
        }
        return instance;
    }

    public Label buildLabel() {
        Label label = new Label("");
        label.setFont(new Font(Objects.requireNonNull(MONACO_TTF).toExternalForm(), 20 * .75));
        label.prefWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    public Label buildLabel(String text, Font font, Paint textFill) {
        Label label = new Label(text);
        label.setAlignment(Pos.TOP_CENTER);
        label.setFont(font);
        label.setTextFill(textFill);
        return label;
    }

    public HBox buildHBox(Node node) {
        HBox hbox = new HBox(node);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    public ImageView buildImageView(Image image, double scale) {
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(image.getWidth() * scale);
        return imageView;
    }

    public TextField buildTextField() {
        TextField tf = new TextField("");
        tf.setPrefWidth(Double.MAX_VALUE);
        return tf;
    }

    public Hyperlink buildHyperlink(String text, Font font, LinearGradient fill, String url) {
        Hyperlink link = new Hyperlink(text);
        link.setFont(font);
        link.setBorder(Border.EMPTY);
        link.setTextFill(fill);
        link.setOnAction(e -> Drifty_GUI.INSTANCE.openWebsite(url));
        return link;
    }
}