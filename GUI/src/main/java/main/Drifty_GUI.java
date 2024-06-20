package main;

import gui.init.Environment;
import gui.preferences.AppSettings;
import gui.support.Constants;
import gui.utils.MessageBroker;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import properties.Mode;
import ui.*;
import utils.Utility;

import static gui.support.Constants.GUI_APPLICATION_TERMINATED;
import static javafx.scene.layout.AnchorPane.*;
import static support.Constants.DRIFTY_WEBSITE_URL;
import static support.Constants.VERSION_NUMBER;

public class Drifty_GUI extends Application {
    private static MessageBroker msgBroker;
    public static Stage primaryStage;
    public static Scene scene;
    public static Scene aboutScene;
    public static ImageView appIcon = new ImageView(Constants.IMG_SPLASH);

    public static VBox aboutRoot = new VBox(10);


    public static void main(String[] args) {
        System.setProperty("javafx.preloader", Splash.class.getCanonicalName());
        launch(args);
    }

    @Override
    public void init() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        Mode.setGUIMode();
        msgBroker = new MessageBroker();
        Environment.setGUIMessageBroker(msgBroker);
        msgBroker.msgLogInfo("Drifty GUI (Graphical User Interface) Application Started !");
    }

    @Override
    public void start(Stage primaryStage) {
        Environment.initializeEnvironment();
        this.primaryStage = Constants.getStage("Drifty GUI", true);
        this.primaryStage.setMinWidth(Constants.SCREEN_WIDTH * .46);
        this.primaryStage.setMinHeight(Constants.SCREEN_HEIGHT * .8125);
        createScene();
        this.primaryStage.setScene(scene);
        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_LOAD));
        this.primaryStage.show();
    }

    public static AnchorPane ap = new AnchorPane();

    private void createScene() {
        MainGridPane gridPane = new MainGridPane();
        MenuBar menu = menuBar(getMenuItemsOfMenu(), getEditMenu(), getWindowMenu(), getHelpMenu());
        ap.getChildren().add(gridPane);
        ap.getChildren().add(menu);

        placeControl(gridPane, 40, 40, 40, 40);
        placeControl(menu, 0, 0, 0, -1);
        scene = Constants.getScene(ap);
        if (AppSettings.GET.mainTheme().toString().equals("Dark")) {
            scene.getStylesheets().add(Constants.DARK_THEME_CSS.toExternalForm());
        }
        scene.setOnContextMenuRequested(e -> getRightClickContextMenu().show(scene.getWindow(), e.getScreenX(), e.getScreenY()));
        menu.setUseSystemMenuBar(true);
        UIController.initLogic(gridPane);
        primaryStage.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (UIController.isAutoPaste()) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                if (clipboard.hasString()) {
                    String clipboardText = clipboard.getString();
                    if (Utility.isURL(clipboardText)) {
                        UIController.pasteFromClipboard(clipboardText);
                    }
                }
            }
        }));
    }

    private void placeControl(Node node, double left, double right, double top, double bottom) {
        if (top != -1) {
            setTopAnchor(node, top);
        }
        if (bottom != -1) {
            setBottomAnchor(node, bottom);
        }
        if (left != -1) {
            setLeftAnchor(node, left);
        }
        if (right != -1) {
            setRightAnchor(node, right);
        }
    }

    private Menu getMenuItemsOfMenu() {
        Menu menu = new Menu("Menu");
        MenuItem website = new MenuItem("Project Website");
        website.setOnAction(e -> openWebsite(DRIFTY_WEBSITE_URL));
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> {
            msgBroker.msgLogInfo(GUI_APPLICATION_TERMINATED);
            Environment.terminate(0);
        });
        menu.getItems().setAll(website, exit);
        return menu;
    }

    private MenuBar menuBar(Menu... menus) {
        return new MenuBar(menus);
    }

    private Menu getWindowMenu() {
        Menu menu = new Menu("Window");
        MenuItem fullScreen = new MenuItem("Toggle Full Screen");
        fullScreen.setOnAction(e -> toggleFullScreen());
        menu.getItems().setAll(fullScreen);
        return menu;
    }

    private Menu getHelpMenu() {
        Menu menu = new Menu("Help");
        MenuItem contactUs = new MenuItem("Contact Us");
        MenuItem contribute = new MenuItem("Contribute");
        MenuItem bug = new MenuItem("Report a Bug");
        MenuItem securityVulnerability = new MenuItem("Report a Security Vulnerability");
        MenuItem feature = new MenuItem("Suggest a Feature");
        MenuItem about = new MenuItem("About Drifty");
        contactUs.setOnAction(e -> openWebsite("https://saptarshisarkar12.github.io/Drifty/contact"));
        contribute.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty"));
        bug.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=bug+%F0%9F%90%9B%2CApp+%F0%9F%92%BB&projects=&template=Bug-for-application.yaml&title=%5BBUG%5D+"));
        securityVulnerability.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/security/advisories/new"));
        feature.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=feature+%E2%9C%A8%2CApp+%F0%9F%92%BB&projects=&template=feature-request-application.yaml&title=%5BFEATURE%5D+"));
        about.setOnAction(event -> {
            Stage stage = Constants.getStage("About Drifty", false);
            aboutRoot.setPadding(new Insets(10));

            aboutRoot.setAlignment(Pos.TOP_CENTER);
            appIcon = new ImageView(Constants.IMG_SPLASH);
            appIcon.setFitWidth(Constants.SCREEN_WIDTH * .2);
            appIcon.setFitHeight(Constants.SCREEN_HEIGHT * .2);
            appIcon.setPreserveRatio(true);
            Label lblDescription = new Label("An Open-Source Interactive File Downloader System");
            lblDescription.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            lblDescription.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #8e2de2, #4a00e0)"));
            Label lblDriftyVersion = new Label("Drifty " + VERSION_NUMBER);
            Label lblYtDlpVersion = new Label("yt-dlp version: " + AppSettings.GET.ytDlpVersion());
            lblDriftyVersion.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            lblDriftyVersion.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
            lblYtDlpVersion.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            lblYtDlpVersion.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
            Hyperlink websiteLink = new Hyperlink("Website");
            websiteLink.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            websiteLink.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #fc466b, #3f5efb)"));
            websiteLink.setOnAction(e -> openWebsite("https://saptarshisarkar12.github.io/Drifty"));
            Hyperlink discordLink = new Hyperlink("Join Discord");
            discordLink.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            discordLink.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #00d956, #0575e6)"));
            discordLink.setOnAction(e -> openWebsite("https://discord.gg/DeT4jXPfkG"));
            Hyperlink githubLink = new Hyperlink("Contribute to Drifty");
            githubLink.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            githubLink.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #009fff, #ec2f4b)"));
            githubLink.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty"));
            aboutRoot.getChildren().addAll(appIcon, lblDescription, lblDriftyVersion, lblYtDlpVersion);
            if (AppSettings.GET.isFfmpegWorking() && AppSettings.GET.ffmpegVersion() != null && !AppSettings.GET.ffmpegVersion().isEmpty()) {
                Label lblFfmpegVersion = new Label("FFMPEG version: " + AppSettings.GET.ffmpegVersion());
                lblFfmpegVersion.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                lblFfmpegVersion.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
                aboutRoot.getChildren().add(lblFfmpegVersion);
            }
            aboutRoot.getChildren().addAll(websiteLink, discordLink, githubLink);
            aboutScene = Constants.getScene(aboutRoot);
            if (AppSettings.GET.mainTheme().toString().equals("Dark")) {
                aboutScene.getStylesheets().add(Constants.DARK_THEME_CSS.toExternalForm());
                for (Node node : Drifty_GUI.aboutRoot.getChildren()) {
                    if (node instanceof Label) {
                        ((Label) node).setTextFill(Color.WHITE);
                    }
                }
            }
            stage.setMinHeight(Constants.SCREEN_HEIGHT * .55);
            stage.setMinWidth(Constants.SCREEN_WIDTH * .5);
            stage.setScene(aboutScene);
            stage.show();
        });
        menu.getItems().setAll(contactUs, contribute, bug, securityVulnerability, feature, about);
        return menu;
    }

    private Menu getEditMenu() {
        Menu menu = new Menu("Edit");
        MenuItem wipeHistory = new MenuItem("Clear Download History");
        MenuItem settings = new MenuItem("Settings");
        wipeHistory.setOnAction(e -> {
            ConfirmationDialog ask = new ConfirmationDialog("Clear Download History", "Are you sure you wish to wipe out all of your download history?\n(This will NOT delete any downloaded files)", false);
            if (ask.getResponse().isYes()) {
                UIController.clearJobHistory();
            }
        });
        settings.setOnAction(e -> {
            Settings settings1 = new Settings();
            settings1.creatSettings();
        });
        menu.getItems().addAll(wipeHistory, settings);
        return menu;
    }

    private ContextMenu getRightClickContextMenu() {
        MenuItem miAdd = new MenuItem("Add Directory");
        MenuItem miDir = new MenuItem("Manage Directories");
        miAdd.setOnAction(e -> UIController.getDirectory());
        miDir.setOnAction(e -> {
            ManageFolders manage = new ManageFolders();
            manage.showScene();
            UIController.resetDownloadFoldersToActiveList();
        });
        ContextMenu contextMenu = new ContextMenu(miAdd, miDir);
        contextMenu.getStyleClass().add("rightClick");
        return contextMenu;
    }

    protected void openWebsite(String websiteURL) {
        getHostServices().showDocument(websiteURL);
    }

    public void toggleFullScreen() {
        primaryStage.setFullScreen(!primaryStage.isFullScreen());
    }
}
