package GUI.Forms;

import Enums.Mode;
import Utils.DriftyConstants;
import Utils.Environment;
import Utils.MessageBroker;
import Utils.Utility;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.LinearGradient;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import static Utils.DriftyConstants.DRIFTY_WEBSITE_URL;
import static javafx.scene.layout.AnchorPane.*;

public class Main extends Application {
    private static Main guiInstance;
    private static MessageBroker msgBroker;
    private Stage primaryStage;
    private Scene scene;
    private boolean firstRun = true;

    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        Mode.setGUIMode();
        msgBroker = new MessageBroker();
        Environment.setMessageBroker(msgBroker);
        msgBroker.msgLogInfo(DriftyConstants.GUI_APPLICATION_STARTED);
        Environment.initializeEnvironment();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = Constants.getStage("Drifty GUI", true);
        createScene();
        guiInstance = this;
    }

    private void createScene() {
        AnchorPane ap = new AnchorPane();
        MainGridPane gridPane = new MainGridPane();
        MenuBar menu = menuBar(getMenuItemsOfMenu(), getEditMenu(), getWindowMenu(), getHelpMenu());
        ap.getChildren().add(gridPane);
        ap.getChildren().add(menu);
        placeControl(gridPane, 40, 40, 40, 40);
        placeControl(menu, 0, 0, 0, -1);
        primaryStage.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (firstRun) {
                firstRun = false;
                return;
            }
            if (guiController.isAutoPaste()) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                if (clipboard.hasString()) {
                    String clipboardText = clipboard.getString();
                    if (Utility.isURL(clipboardText)) {
                        guiController.pasteFromClipboard(clipboardText);
                    }
                }
            }
        }));
        scene = Constants.getScene(ap);
        scene.setOnContextMenuRequested(e -> getRightClickContextMenu().show(scene.getWindow(), e.getScreenX(), e.getScreenY()));
        primaryStage.setScene(scene);
        primaryStage.show();
        menu.setUseSystemMenuBar(true);
        guiController.initLogic(gridPane);
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
            msgBroker.msgLogInfo(DriftyConstants.GUI_APPLICATION_TERMINATED);
            System.exit(0);
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
        fullScreen.setOnAction(e -> Main.toggleFullScreen());
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
            VBox root = new VBox(10);
            root.setPadding(new Insets(10));
            root.setAlignment(Pos.TOP_CENTER);
            ImageView appIcon = new ImageView(String.valueOf(Constants.class.getResource("/GUI/Splash.png")));
            appIcon.setFitWidth(Constants.SCREEN_WIDTH * .2);
            appIcon.setFitHeight(Constants.SCREEN_HEIGHT * .2);
            appIcon.setPreserveRatio(true);
            Label lblDescription = new Label("An Open-Source Interactive File Downloader System");
            lblDescription.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            lblDescription.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #8e2de2, #4a00e0)"));
            Label lblVersion = new Label(DriftyConstants.VERSION_NUMBER);
            lblVersion.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            lblVersion.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
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
            root.getChildren().addAll(appIcon, lblDescription, lblVersion, websiteLink, discordLink, githubLink);
            Scene aboutScene = Constants.getScene(root);
            stage.setMinHeight(Constants.SCREEN_HEIGHT * .5);
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
        wipeHistory.setOnAction(e -> {
            AskYesNo ask = new AskYesNo("Clear Download History", "Are you sure you wish to wipe out all of your download history?\n(This will NOT delete any downloaded files)", false);
            if (ask.getResponse().isYes()) {
                guiController.clearJobHistory();
            }
        });
        menu.getItems().addAll(wipeHistory);
        return menu;
    }

    private ContextMenu getRightClickContextMenu() {
        MenuItem miAdd = new MenuItem("Add Directory");
        MenuItem miDir = new MenuItem("Manage Directories");
        miAdd.setOnAction(e -> guiController.getDirectory());
        miDir.setOnAction(e -> {
            ManageFolders manage = new ManageFolders();
            manage.showScene();
            guiController.resetDownloadFoldersToActiveList();
        });
        ContextMenu contextMenu = new ContextMenu(miAdd, miDir);
        contextMenu.getStyleClass().add("rightClick");
        return contextMenu;
    }

    protected static void openWebsite(String websiteURL) {
        guiInstance.getHostServices().showDocument(websiteURL);
    }

    public static void toggleFullScreen() {
        guiInstance.primaryStage.setFullScreen(!guiInstance.primaryStage.isFullScreen());
    }
}
