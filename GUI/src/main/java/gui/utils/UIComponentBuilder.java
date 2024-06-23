package gui.utils;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    public Label newLabel() {
        Label label = new Label("");
        label.setFont(new Font(Objects.requireNonNull(MONACO_TTF).toExternalForm(), 20 * .75));
        label.setPrefWidth(Double.MAX_VALUE);
        label.getStyleClass().add("outline");
        return label;
    }

    public Label newLabel(String text, Font font, Paint textFill) {
        Label label = new Label(text);
        label.setFont(font);
        label.setTextFill(textFill);
        return label;
    }

    public TextField newTextField() {
        TextField tf = new TextField("");
        tf.setPrefWidth(Double.MAX_VALUE);
        return tf;
    }

    public HBox newHBox(Node node) {
        HBox box = new HBox(node);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    public ImageView newImageView(Image image, double scale) {
        ImageView iv = new ImageView(image);
        double width = image.getWidth();
        iv.setPreserveRatio(true);
        iv.setFitWidth(width * scale);
        return iv;
    }

    public Hyperlink newHyperlink(String text, Font font, LinearGradient fill, String url) {
        Hyperlink link = new Hyperlink(text);
        link.setFont(font);
        link.setTextFill(fill);
        link.setOnAction(e -> Drifty_GUI.INSTANCE.openWebsite(url));
        return link;
    }
}
