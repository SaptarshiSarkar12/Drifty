package ui;

import preferences.AppSettings;
import gui.support.Constants;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.util.Objects;

import static gui.support.Constants.UI_COMPONENT_BUILDER_INSTANCE;

public class Theme {
    public static void applyTheme(String theme, Scene... scenes) {
        boolean isDark = "Dark".equals(theme);
        AppSettings.SET.setGuiTheme(theme);
        updateCSS(isDark, scenes);
        updateTextColors(isDark, scenes);
        changeImages(theme);
        updateButtonStyles(isDark, theme);
    }

    private static void setupButton(Image imageUp, Image imageDown, Button button) {
        ImageView imageViewUp = UI_COMPONENT_BUILDER_INSTANCE.buildImageView(imageUp, 0.45);
        ImageView imageViewDown = UI_COMPONENT_BUILDER_INSTANCE.buildImageView(imageDown, 0.45);
        button.setOnMousePressed(ev -> button.setGraphic(imageViewDown));
        button.setOnMouseReleased(ev -> button.setGraphic(imageViewUp));
        button.setGraphic(imageViewUp);
    }

    private static void setupButtonGraphics(String theme) {
        Image imageStartUp = getImageForButton(theme, "Start", "StartUp");
        Image imageStartDown = getImageForButton(theme, "Start", "StartDown");
        setupButton(imageStartUp, imageStartDown, UIController.form.btnStart);

        Image imageSaveUp = getImageForButton(theme, "Save", "SaveUp");
        Image imageSaveDown = getImageForButton(theme, "Save", "SaveDown");
        setupButton(imageSaveUp, imageSaveDown, UIController.form.btnSave);
    }

    private static Image getImageForButton(String theme, String buttonCategory, String buttonType) {
        String imagePath = "/Buttons/" + buttonCategory + "/" + buttonType + theme + ".png";
        return new Image(Objects.requireNonNull(Constants.class.getResource(imagePath)).toExternalForm());
    }

    private static void changeImages(String theme) {
        String bannerPath = "/Backgrounds/DriftyMain" + theme + ".png";
        String splashPath = "/Splash" + theme + ".png";
        Constants.imgMainGuiBanner = new Image(Objects.requireNonNull(Constants.class.getResource(bannerPath)).toExternalForm());
        MainGridPane.ivLogo.setImage(Constants.imgMainGuiBanner);
        Constants.imgSplash = new Image(Objects.requireNonNull(Constants.class.getResource(splashPath)).toExternalForm());
        About.getIvSplash().setImage(Constants.imgSplash);
    }

    private static void updateTextColors(boolean isDark, Scene... scenes) {
        // Labels
        Paint color = isDark ? Color.WHITE : LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)");
        for (Scene scene : scenes) {
            if (scene != null) {
                for (Node node : scene.getRoot().getChildrenUnmodifiable()) {
                    if (node instanceof Label) {
                        ((Label) node).setTextFill(color);
                    } else if (node instanceof Text) {
                        ((Text) node).setFill(color);
                    }
                }
            }
        }
        changeInfoTextFlow(color);
        updateTextFields(isDark, false, UIController.form.tfDir, UIController.form.tfFilename, UIController.form.tfLink);
        updateTextFields(isDark, true, Settings.getTfCurrentDirectory());
    }

    private static void changeInfoTextFlow(Paint color) {
        Color headingsColor = "Dark".equals(AppSettings.GET.getGuiTheme()) ? Color.LIGHTGREEN : Color.DARKBLUE;
        for (int i = 0; i < UIController.getInfoTf().getChildren().size(); i++) {
            if (UIController.getInfoTf().getChildren().get(i) instanceof Text text) {
                if (text.getFont().getSize() == 16) {
                    ((Text) UIController.getInfoTf().getChildren().get(i)).setFill(color);
                } else {
                    ((Text) UIController.getInfoTf().getChildren().get(i)).setFill(headingsColor);

                }
            }
        }
    }

    private static void updateButtonStyles(boolean isDark, String theme) {
        changeButtonStyle(isDark, Settings.getSelectDirectoryButton());
        changeButtonStyle(isDark, ConfirmationDialog.getBtnYes());
        changeButtonStyle(isDark, ConfirmationDialog.getBtnNo());
        changeButtonStyle(isDark, ConfirmationDialog.getBtnOk());
        changeButtonStyle(isDark, ManageFolders.getBtnClose());
        changeButtonStyle(isDark, ManageFolders.getBtnRemove());
        setupButtonGraphics(theme);
    }

    static void changeButtonStyle(boolean isDark, Button button) {
        if (button != null) {
            if (isDark) {
                button.setStyle(Constants.BUTTON_RELEASED);
                button.setOnMousePressed(ev -> button.setStyle(Constants.BUTTON_PRESSED));
                button.setOnMouseReleased(ev -> button.setStyle(Constants.BUTTON_RELEASED));
            } else {
                String style = "-fx-text-fill: Black;";
                String backColorReleased = "-fx-background-color: linear-gradient(rgb(54,151,225) 18%, rgb(121,218,232) 90%, rgb(126,223,255) 95%);";
                String backColorPressed = "-fx-background-color: linear-gradient(rgb(126,223,255) 20%, rgb(121,218,232) 20%, rgb(54,151,225) 100%);";
                button.setStyle(style + backColorReleased);
                button.setOnMousePressed(ev -> button.setStyle(style + backColorPressed));
                button.setOnMouseReleased(ev -> button.setStyle(style + backColorReleased));
            }
        }
    }

    static void updateTextFields(boolean isDark, boolean isBold, TextField... textFields) {
        String style = isDark ? "-fx-text-fill: White;" : "-fx-text-fill: Black;";
        for (TextField textField : textFields) {
            if (textField != null) {
                textField.setStyle(isBold ? style.concat("-fx-font-weight: Bold") : style);
            }
        }
    }

    private static void updateCSS(boolean isDark, Scene... scenes) {
        for (Scene scene : scenes) {
            if (scene != null) {
                Constants.addCSS(scene, isDark ? Constants.DARK_THEME_CSS : Constants.LIGHT_THEME_CSS);
                scene.getStylesheets().remove(isDark ? Objects.requireNonNull(Constants.LIGHT_THEME_CSS).toExternalForm() : Objects.requireNonNull(Constants.DARK_THEME_CSS).toExternalForm());
            }
        }
    }
}