package main;

import gui.init.Environment;
import gui.preferences.AppSettings;
import gui.support.Constants;
import gui.utils.MessageBroker;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import properties.Mode;
import ui.*;
import updater.UpdateChecker;
import utils.Utility;

import static gui.support.Constants.GUI_APPLICATION_TERMINATED;
import static javafx.scene.layout.AnchorPane.*;
import static support.Constants.DRIFTY_WEBSITE_URL;

public class Drifty_GUI extends Application {
    public static final Drifty_GUI INSTANCE = new Drifty_GUI();
    private static MessageBroker msgBroker;
    private static Scene scene;
    private final Settings settingsInstance = new Settings();
    private About aboutInstance;
    private Stage primaryStage;

    public static void main(String[] args) {
        Mode.setGUIMode();
        System.setProperty("javafx.preloader", Splash.class.getCanonicalName());
        launch(args);
    }

    @Override
    public void init() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        msgBroker = new MessageBroker();
        Environment.setGUIMessageBroker(msgBroker);
        msgBroker.msgLogInfo("Drifty GUI (Graphical User Interface) Application Started !");
        Environment.initializeEnvironment();
        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START));
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = Constants.getStage("Drifty GUI", true);
        this.primaryStage.setMinWidth(Constants.SCREEN_WIDTH * .46);
        this.primaryStage.setMinHeight(Constants.SCREEN_HEIGHT * .8125);
        createScene();
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    private void createScene() {
        AnchorPane ap = new AnchorPane();
        MainGridPane gridPane = new MainGridPane();
        MenuBar menu = menuBar(getMenuItemsOfMenu(), getEditMenu(), getWindowMenu(), getHelpMenu());
        ap.getChildren().add(gridPane);
        ap.getChildren().add(menu);

        placeControl(gridPane, 40, 40, 40, 40);
        placeControl(menu, 0, 0, 0, -1);
        scene = Constants.getScene(ap);
        if ("Dark".equals(AppSettings.GET.mainTheme())) {
            Constants.addCSS(scene, Constants.DARK_THEME_CSS);
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

    public static Scene getScene() {
        return scene;
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
        MenuItem checkForUpdates = new MenuItem("Check for Updates");
        MenuItem about = new MenuItem("About Drifty");
        contactUs.setOnAction(e -> openWebsite("https://saptarshisarkar12.github.io/Drifty/contact"));
        contribute.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty"));
        bug.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=bug+%F0%9F%90%9B%2CApp+%F0%9F%92%BB&projects=&template=Bug-for-application.yaml&title=%5BBUG%5D+"));
        securityVulnerability.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/security/advisories/new"));
        feature.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=feature+%E2%9C%A8%2CApp+%F0%9F%92%BB&projects=&template=feature-request-application.yaml&title=%5BFEATURE%5D+"));
        checkForUpdates.setOnAction(e -> new Thread(() -> {
            if (Utility.isOffline()) {
                ConfirmationDialog noInternet = new ConfirmationDialog("No Internet Connection", "You are currently offline! Please check your internet connection and try again.", true, false);
                noInternet.getResponse();
            } else {
                checkForUpdates();
            }
        }).start());
        about.setOnAction(event -> {
            if (aboutInstance == null) {
                aboutInstance = new About();
            }
            aboutInstance.show();
        });
        menu.getItems().setAll(contactUs, contribute, bug, securityVulnerability, feature, checkForUpdates, about);
        return menu;
    }

    private void checkForUpdates() {
        if (UpdateChecker.isUpdateAvailable()) {
            UIController.INSTANCE.showUpdateDialog();
        } else {
            ConfirmationDialog noUpdate = new ConfirmationDialog("No Updates Available", "You are already using the latest version of Drifty!", true, false);
            noUpdate.getResponse();
        }
    }

    private Menu getEditMenu() {
        Menu menu = new Menu("Edit");
        MenuItem wipeHistory = new MenuItem("Clear Download History");
        MenuItem settings = new MenuItem("Settings");
        wipeHistory.setOnAction(e -> {
            ConfirmationDialog ask = new ConfirmationDialog("Clear Download History", "Are you sure you wish to wipe out all of your download history?\n(This will NOT delete any downloaded files)", false, false);
            if (ask.getResponse().isYes()) {
                UIController.clearJobHistory();
            }
        });
        settings.setOnAction(e -> settingsInstance.show());
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

    public void openWebsite(String websiteURL) {
        getHostServices().showDocument(websiteURL);
    }

    public void toggleFullScreen() {
        primaryStage.setFullScreen(!primaryStage.isFullScreen());
    }
}
