package ui;

import gui.preferences.AppSettings;
import gui.support.Constants;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import main.Drifty_GUI;
import java.util.Objects;

public class Theme {
    public static void applyTheme(String theme, Scene... scenes) {
        boolean isDark = theme.equals("Dark");
        AppSettings.SET.mainTheme(theme);
        updateCSS(isDark, scenes);
        updateTextColors(isDark, scenes);
        changeImages(theme);
        changeButtonStyle(isDark, Settings.button);
        Theme.changeButtonStyle(isDark, ConfirmationDialog.getBtnYes());
        Theme.changeButtonStyle(isDark, ConfirmationDialog.getBtnNo());
        setupButtonGraphics(theme);
    }
    private static void setupButtonGraphics(String theme) {
        Image imageStartUp = getImageForButton(theme, "StartUp");
        Image imageStartDown = getImageForButton(theme, "StartDown");
        ImageView ivStartUp = MainGridPane.newImageView(imageStartUp, 0.45);
        ImageView ivStartDown = MainGridPane.newImageView(imageStartDown, 0.45);
        UIController.form.btnStart.setOnMousePressed(ev -> UIController.form.btnStart.setGraphic(ivStartDown));
        UIController.form.btnStart.setOnMouseReleased(ev -> UIController.form.btnStart.setGraphic(ivStartUp));
        UIController.form.btnStart.setGraphic(ivStartUp);

        Image imageSaveUp = getImageForButton(theme, "SaveUp");
        Image imageSaveDown = getImageForButton(theme, "SaveDown");
        ImageView ivSaveUp = MainGridPane.newImageView(imageSaveUp, 0.45);
        ImageView ivSaveDown = MainGridPane.newImageView(imageSaveDown, 0.45);
        UIController.form.btnSave.setOnMousePressed(ev -> UIController.form.btnSave.setGraphic(ivSaveDown));
        UIController.form.btnSave.setOnMouseReleased(ev -> UIController.form.btnSave.setGraphic(ivSaveUp));
        UIController.form.btnSave.setGraphic(ivSaveUp);
    }

    private static Image getImageForButton(String theme, String buttonType) {
        String imagePath;
        if (buttonType.equals("StartUp") || buttonType.equals("StartDown")) {
            imagePath = "/Buttons/Start/" + buttonType + theme + ".png";
        } else {
            imagePath = "/Buttons/Save/" + buttonType + theme + ".png";
        }
        return new Image(Objects.requireNonNull(Constants.class.getResource(imagePath)).toExternalForm());
    }

    private static void changeImages(String theme) {
        String bannerPath = "/Backgrounds/DriftyMain" + theme + ".png";
        String splashPath = "/Splash" + theme + ".png";
        Constants.imgMainGuiBanner = new Image(Objects.requireNonNull(Constants.class.getResource(bannerPath)).toExternalForm());
        MainGridPane.ivLogo.setImage(Constants.imgMainGuiBanner);
        Constants.imgSplash = new Image(Objects.requireNonNull(Constants.class.getResource(splashPath)).toExternalForm());
        Drifty_GUI.getAppIcon().setImage(Constants.imgSplash);
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
        // TextFields
        String style = isDark ? "-fx-text-fill: White;" : "-fx-text-fill: Black;";
        UIController.form.tfDir.setStyle(style);
        UIController.form.tfFilename.setStyle(style);
        UIController.form.tfLink.setStyle(style);
        Settings.tfCurrDir.setStyle(style + "-fx-font-weight: Bold");
    }

    static void changeButtonStyle(boolean isDark, Button button) {
        if (button != null) {
            if (isDark) {
                button.setStyle(Constants.BTN_THEME);
                button.setOnMousePressed(ev -> button.setStyle(
                        Constants.BTN_THEME_PRESSED
                ));
                button.setOnMouseReleased(ev -> button.setStyle(
                        Constants.BTN_THEME
                ));
            } else {
                String style = "-fx-text-fill: Black;";
                String backColorRealesd = "-fx-background-color: linear-gradient(rgb(54,151,225) 18%, rgb(121,218,232) 90%, rgb(126,223,255) 95%);";
                String backColorPressed = "-fx-background-color: linear-gradient(rgb(126,223,255) 20%, rgb(121,218,232) 20%, rgb(54,151,225) 100%);";
                button.setStyle(style + backColorRealesd);
                button.setOnMousePressed(ev -> button.setStyle(
                        style + backColorPressed
                ));
                button.setOnMouseReleased(ev -> button.setStyle(
                        style + backColorRealesd
                ));
            }
        }

    }

    private static void updateCSS(boolean isDark, Scene... scenes) {
        if (isDark) {
            for (Scene scene : scenes) {
                if (scene != null) {
                    Constants.addCSS(scene, Constants.DARK_THEME_CSS);
                }
            }
        } else {
            for (Scene scene : scenes) {
                if (scene != null) {
                    scene.getStylesheets().remove(Objects.requireNonNull(Settings.class.getResource("/CSS/DarkTheme.css")).toExternalForm());
                    Constants.addCSS(scene, Constants.LIGHT_THEME_CSS);
                }
            }
        }

    }

}
