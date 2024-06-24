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

    public LabelBuilder newLabel(String text) {
        Label label = new Label(text);
        return new LabelBuilder(label);
    }

    public TextFieldBuilder newTextField(String initialText) {
        TextField textField = new TextField(initialText);
        return new TextFieldBuilder(textField);
    }

    public HBoxBuilder newHBox(Node node) {
        HBox hbox = new HBox(node);
        return new HBoxBuilder(hbox);
    }

    public ImageViewBuilder newImageView(Image image, double scale) {
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(image.getWidth() * scale);
        return new ImageViewBuilder(imageView);
    }

    public TextField newTextField() {
        TextField tf = new TextField("");
        tf.setPrefWidth(Double.MAX_VALUE);
        return tf;
    }

    public Hyperlink newHyperlink(String text, Font font, LinearGradient fill, String url) {
        Hyperlink link = new Hyperlink(text);
        link.setFont(font);
        link.setTextFill(fill);
        link.setOnAction(e -> Drifty_GUI.INSTANCE.openWebsite(url));
        return link;
    }

    public static class TextFieldBuilder {
        private final TextField textField;

        public TextFieldBuilder(TextField textField) {
            this.textField = textField;
        }

        public TextFieldBuilder prefWidth(double width) {
            textField.setPrefWidth(width);
            return this;
        }

        public TextField build() {
            return textField;
        }
    }

    public static class HBoxBuilder {
        private final HBox hbox;

        public HBoxBuilder(HBox hbox) {
            this.hbox = hbox;
        }

        public HBoxBuilder alignment(Pos position) {
            hbox.setAlignment(position);
            return this;
        }

        public HBox build() {
            return hbox;
        }
    }

    public static class ImageViewBuilder {
        private final ImageView imageView;

        public ImageViewBuilder(ImageView imageView) {
            this.imageView = imageView;
        }

        public ImageViewBuilder preserveRatio(boolean preserve) {
            imageView.setPreserveRatio(preserve);
            return this;
        }

        public ImageViewBuilder fitWidth(double width) {
            imageView.setFitWidth(width);
            return this;
        }

        public ImageView build() {
            return imageView;
        }
    }

    public static class LabelBuilder {
        private final Label label;

        public LabelBuilder(Label label) {
            this.label = label;
        }

        public LabelBuilder font(Font font) {
            label.setFont(font);
            return this;
        }

        public LabelBuilder textFill(Paint paint) {
            label.setTextFill(paint);
            return this;
        }

        public Label build() {
            return label;
        }
    }
}
