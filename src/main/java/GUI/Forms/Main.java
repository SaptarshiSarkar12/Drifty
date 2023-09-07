package GUI.Forms;

import Backend.Drifty;
import Enums.MessageCategory;
import Enums.MessageType;
import Enums.Mode;
import Preferences.AppSettings;
import Utils.DriftyConstants;
import Utils.Environment;
import Utils.MessageBroker;
import Utils.Utility;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

import static javafx.scene.layout.AnchorPane.*;

public class Main extends Application {
    private static Main INSTANCE;
    private Stage primaryStage;
    private Scene scene;
    private boolean firstRun = true;
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        Mode.setGUIMode();
        Environment.setMessageBroker(new MessageBroker());
        Environment.getMessageBroker().sendMessage(DriftyConstants.GUI_APPLICATION_STARTED, MessageType.INFO, MessageCategory.LOG);
        Environment.initializeEnvironment();
        Utility.setStartTime();
        for (String arg : args) {
            if (arg.toLowerCase().contains("--devmode")) {
                Mode.setDev();
            }
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Drifty");
        createScene();
        INSTANCE = this;
    }

    private void createScene() {
        AnchorPane ap = new AnchorPane();
        MainGridPane gridPane = new MainGridPane();
        MenuBar menu = menuBar(getMenuItemsOfMenu(), getEditMenu(), getWindowMenu(), getHelpMenu());
        ap.getChildren().add(gridPane);
        ap.getChildren().add(menu);
        placeControl(gridPane, 40, 40, 40, 40);
        placeControl(menu, 0, 0, 0, -1);
        primaryStage = Constants.getStage(primaryStage);
        primaryStage.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if(firstRun) {
                firstRun = false;
                return;
            }
            if (FormLogic.isAutoPaste()) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                if (clipboard.hasString()) {
                    String clipboardText = clipboard.getString();
                    if(Utility.isURL(clipboardText))
                        FormLogic.form.tfLink.setText(clipboardText);
                }
            }
        }));
        scene = Constants.getScene(ap);
        scene.setOnContextMenuRequested(e -> getRightClickContextMenu().show(scene.getWindow(), e.getScreenX(), e.getScreenY()));
        primaryStage.setScene(scene);
        primaryStage.show();
        menu.setUseSystemMenuBar(true);
        FormLogic.initLogic(gridPane);
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
        website.setOnAction(e -> openWebsite(Drifty.projectWebsite, "project website"));
        MenuItem about = new MenuItem("About");
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> {
            Environment.getMessageBroker().sendMessage(DriftyConstants.GUI_APPLICATION_TERMINATED, MessageType.INFO, MessageCategory.LOG);
            System.exit(0);
        });
        menu.getItems().setAll(website, about, exit);
        return menu;
    }

    private MenuBar menuBar(Menu... menus) {
        MenuBar menuBar = new MenuBar(menus);
        return menuBar;
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
        contactUs.setOnAction(e -> openWebsite("https://saptarshisarkar12.github.io/Drifty/contact.html", "contact us webpage"));
        contribute.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty", "repository website for contribution"));
        bug.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=bug%2CApp&template=Bug-for-application.yaml&title=%5BBUG%5D+", "issue webpage to file a Bug"));
        securityVulnerability.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/security/advisories/new", "Security Vulnerability webpage"));
        feature.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=enhancement%2CApp&template=feature-request-application.yaml&title=%5BFEATURE%5D+", "issue webpage to suggest feature"));
        menu.getItems().setAll(contactUs, contribute, bug, securityVulnerability, feature);
        return menu;
    }

    private Menu getEditMenu() {
        Menu menu = new Menu("Edit");
        MenuItem wipeHistory = new MenuItem("Clear Job History");
        MenuItem settings = new MenuItem("Settings");
        settings.setOnAction(e -> new Settings().show());
        wipeHistory.setOnAction(e->{
            AskYesNo ask = new AskYesNo("Are you sure you wish to wipe out all of your download job history?\n(This will NOT delete any downloaded files)");
            if(ask.getResponse().isYes()) {
                FormLogic.clearJobHistory();
            }
        });
        menu.getItems().addAll(wipeHistory, settings);
        return menu;
    }

    private ContextMenu getRightClickContextMenu() {
        MenuItem miAdd = new MenuItem("Add Directory");
        MenuItem miDir = new MenuItem("Manage Directories");
        miAdd.setOnAction(e -> getDirectory());
        miDir.setOnAction(e -> {
            ManageFolders manage = new ManageFolders();
            manage.showScene();
            FormLogic.resetDownloadFoldersToActiveList();
        });
        ContextMenu contextMenu = new ContextMenu(miAdd, miDir);
        contextMenu.getStyleClass().add("rightClick");
        return contextMenu;
    }

    private void getDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String lastFolder = AppSettings.get.folders().getDownloadFolder();
        String initFolder = lastFolder.isEmpty() ? System.getProperty("user.home") : lastFolder;
        directoryChooser.setInitialDirectory(new File(initFolder));
        File directory = directoryChooser.showDialog(null);
        if (directory != null) {
            FormLogic.setDir(directory.getAbsolutePath());
        }
    }

    protected static void openWebsite(String websiteURL, String websiteType) {
        INSTANCE.getHostServices().showDocument(websiteURL);
    }

    public static void toggleFullScreen() {
        INSTANCE.primaryStage.setFullScreen(!INSTANCE.primaryStage.isFullScreen());
    }
}
