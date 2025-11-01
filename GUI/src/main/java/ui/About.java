package ui;

import static gui.support.Constants.UI_COMPONENT_BUILDER_INSTANCE;
import static support.Constants.VERSION_NUMBER;

import gui.preferences.AppSettings;
import gui.support.Constants;
import gui.utils.MessageBroker;
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

public class About {
    private static Scene aboutScene;
    private static final ImageView IV_SPLASH = new ImageView(Constants.imgSplash);
    private static MessageBroker msgBroker;
    private Label lblDescription;
    private Label lblDriftyVersion;
    private Label lblYtDlpVersion;
    private Hyperlink websiteLink;
    private Hyperlink discordLink;
    private Hyperlink githubLink;
    private Stage stage;

    private void setupLayout() {
        VBox aboutRoot = new VBox(10);
        aboutRoot.setPadding(new Insets(10));
        aboutRoot.setAlignment(Pos.TOP_CENTER);
        setupImageView();
        createLabels();
        createHyperlinks();
        setupStage();

        aboutRoot.getChildren().addAll(
                IV_SPLASH,
                lblDescription,
                lblDriftyVersion,
                lblYtDlpVersion
        );

        if (AppSettings.GET.isFfmpegWorking()
                && AppSettings.GET.ffmpegVersion() != null
                && !AppSettings.GET.ffmpegVersion().isEmpty()) {
            Label lblFfmpegVersion = UI_COMPONENT_BUILDER_INSTANCE.buildLabel(
                    "FFMPEG version: " + AppSettings.GET.ffmpegVersion(),
                    Font.font("Arial", FontWeight.BOLD, 14),
                    LinearGradient.valueOf(
                            "linear-gradient(to right, #0f0c29, #302b63, #24243e)"
                    )
            );
            aboutRoot.getChildren().add(lblFfmpegVersion);
        }

        aboutRoot.getChildren().addAll(websiteLink, discordLink, githubLink);
        aboutScene = Constants.getScene(aboutRoot);
        applyThemeSettings(aboutRoot);
    }

    private void setupImageView() {
        IV_SPLASH.setFitWidth(Constants.SCREEN_WIDTH * 0.2);
        IV_SPLASH.setFitHeight(Constants.SCREEN_HEIGHT * 0.2);
        IV_SPLASH.setPreserveRatio(true);
    }

    private void setupStage() {
        stage = Constants.getStage("About Drifty", false);
        stage.setMinHeight(Constants.SCREEN_HEIGHT * 0.55);
        stage.setMinWidth(Constants.SCREEN_WIDTH * 0.5);
    }

    private void applyThemeSettings(VBox aboutRoot) {
        if ("Dark".equals(AppSettings.GET.mainTheme())) {
            Constants.addCSS(aboutScene, Constants.DARK_THEME_CSS);
            for (Node node : aboutRoot.getChildren()) {
                if (node instanceof Label) {
                    ((Label) node).setTextFill(Color.WHITE);
                }
            }
        }
    }

    public void show() {
        try {
            if (stage != null && stage.isShowing()) {
                stage.toFront();
            } else {
                setupLayout();
                stage.setScene(aboutScene);
                stage.showAndWait();
            }
        } catch (Exception e) {
            msgBroker.msgLogError("Error displaying About Drifty window");
        }
    }

    private void createLabels() {
        lblDescription = UI_COMPONENT_BUILDER_INSTANCE.buildLabel(
                "An Open-Source Interactive File Downloader System",
                Font.font("Arial", FontWeight.BOLD, 24),
                LinearGradient.valueOf(
                        "linear-gradient(to right, #8e2de2, #4a00e0)"
                )
        );

        lblDriftyVersion = UI_COMPONENT_BUILDER_INSTANCE.buildLabel(
                "Drifty " + VERSION_NUMBER,
                Font.font("Arial", FontWeight.BOLD, 20),
                LinearGradient.valueOf(
                        "linear-gradient(to right, #0f0c29, #302b63, #24243e)"
                )
        );

        lblYtDlpVersion = UI_COMPONENT_BUILDER_INSTANCE.buildLabel(
                "yt-dlp version: " + AppSettings.GET.ytDlpVersion(),
                Font.font("Arial", FontWeight.BOLD, 14),
                LinearGradient.valueOf(
                        "linear-gradient(to right, #0f0c29, #302b63, #24243e)"
                )
        );
    }

    private void createHyperlinks() {
        websiteLink = UI_COMPONENT_BUILDER_INSTANCE.buildHyperlink(
                "Website",
                Font.font("Arial", FontWeight.BOLD, 18),
                LinearGradient.valueOf(
                        "linear-gradient(to right, #fc466b, #3f5efb)"
                ),
                "https://drifty.vercel.app/"
        );

        discordLink = UI_COMPONENT_BUILDER_INSTANCE.buildHyperlink(
                "Join Discord",
                Font.font("Arial", FontWeight.BOLD, 18),
                LinearGradient.valueOf(
                        "linear-gradient(to right, #00d956, #0575e6)"
                ),
                "https://discord.gg/DeT4jXPfkG"
        );

        githubLink = UI_COMPONENT_BUILDER_INSTANCE.buildHyperlink(
                "Contribute to Drifty",
                Font.font("Arial", FontWeight.BOLD, 18),
                LinearGradient.valueOf(
                        "linear-gradient(to right, #009fff, #ec2f4b)"
                ),
                "https://github.com/SaptarshiSarkar12/Drifty"
        );
    }

    public static Scene getScene() {
        return aboutScene;
    }

    public static ImageView getIvSplash() {
        return IV_SPLASH;
    }
}
