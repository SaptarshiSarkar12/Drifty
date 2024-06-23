package ui;

import gui.preferences.AppSettings;
import gui.support.Constants;
import gui.utils.UIComponentBuilder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import static support.Constants.VERSION_NUMBER;

public class About {
    private static Scene aboutScene;
    private static ImageView ivSplash = new ImageView(Constants.imgSplash);
    private Label lblDescription;
    private Label lblDriftyVersion;
    private Label lblYtDlpVersion;
    private Hyperlink websiteLink;
    private Hyperlink discordLink;
    private Hyperlink githubLink;

    public void show() {
        VBox aboutRoot = new VBox(10);
        Stage stage = Constants.getStage("About Drifty", false);
        aboutRoot.setPadding(new Insets(10));
        aboutRoot.setAlignment(Pos.TOP_CENTER);
        ivSplash = new ImageView(Constants.imgSplash);
        ivSplash.setFitWidth(Constants.SCREEN_WIDTH * .2);
        ivSplash.setFitHeight(Constants.SCREEN_HEIGHT * .2);
        ivSplash.setPreserveRatio(true);
        createLabels();
        aboutRoot.getChildren().addAll(ivSplash, lblDescription, lblDriftyVersion, lblYtDlpVersion);
        if (AppSettings.GET.isFfmpegWorking() && AppSettings.GET.ffmpegVersion() != null && !AppSettings.GET.ffmpegVersion().isEmpty()) {
            Label lblFfmpegVersion = UIComponentBuilder.getInstance().newLabel("FFMPEG version: " + AppSettings.GET.ffmpegVersion(), Font.font("Arial", FontWeight.BOLD, 14), LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
            aboutRoot.getChildren().add(lblFfmpegVersion);
        }
        createHyperlinks();
        aboutRoot.getChildren().addAll(websiteLink, discordLink, githubLink);
        aboutScene = Constants.getScene(aboutRoot);
        if (AppSettings.GET.mainTheme().equals("Dark")) {
            Constants.addCSS(aboutScene, Constants.DARK_THEME_CSS);
            for (Node node : aboutRoot.getChildren()) {
                if (node instanceof Label) {
                    ((Label) node).setTextFill(Color.WHITE);
                }
            }
        }
        stage.setMinHeight(Constants.SCREEN_HEIGHT * .55);
        stage.setMinWidth(Constants.SCREEN_WIDTH * .5);
        stage.setScene(aboutScene);
        stage.showAndWait();
    }

    private void createLabels() {
        lblDescription = UIComponentBuilder.getInstance().newLabel("An Open-Source Interactive File Downloader System", Font.font("Arial", FontWeight.BOLD, 24), LinearGradient.valueOf("linear-gradient(to right, #8e2de2, #4a00e0)"));
        lblDriftyVersion = UIComponentBuilder.getInstance().newLabel("Drifty " + VERSION_NUMBER, Font.font("Arial", FontWeight.BOLD, 20), LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
        lblYtDlpVersion = UIComponentBuilder.getInstance().newLabel("yt-dlp version: " + AppSettings.GET.ytDlpVersion(), Font.font("Arial", FontWeight.BOLD, 14), LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
    }

    private void createHyperlinks() {
        websiteLink = UIComponentBuilder.getInstance().newHyperlink("Website", Font.font("Arial", FontWeight.BOLD, 18), LinearGradient.valueOf("linear-gradient(to right, #fc466b, #3f5efb)"), "https://saptarshisarkar12.github.io/Drifty");
        discordLink = UIComponentBuilder.getInstance().newHyperlink("Join Discord", Font.font("Arial", FontWeight.BOLD, 18), LinearGradient.valueOf("linear-gradient(to right, #00d956, #0575e6)"), "https://discord.gg/DeT4jXPfkG");
        githubLink = UIComponentBuilder.getInstance().newHyperlink("Contribute to Drifty", Font.font("Arial", FontWeight.BOLD, 18), LinearGradient.valueOf("linear-gradient(to right, #009fff, #ec2f4b)"), "https://github.com/SaptarshiSarkar12/Drifty");
    }

    public static Scene getScene() {
        return aboutScene;
    }

    public static ImageView getIvSplash() {
        return ivSplash;
    }
}
