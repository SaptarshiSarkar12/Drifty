package GUI.Forms;

import Backend.FileDownloader;
import Enums.Mode;
import Enums.OS;
import Updater.Updater;
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
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static Utils.DriftyConstants.DRIFTY_WEBSITE_URL;
import static Utils.DriftyConstants.VERSION_NUMBER;
import static javafx.scene.layout.AnchorPane.*;

public class Main extends Application {
    private static Main guiInstance;
    private static MessageBroker msgBroker;
    private Stage primaryStage;
    private Scene scene;
    private boolean firstRun = true;

    public static void main(String[] args) throws URISyntaxException {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        Mode.setGUIMode();
        msgBroker = new MessageBroker();
        Environment.setMessageBroker(msgBroker);
        msgBroker.msgLogInfo(DriftyConstants.GUI_APPLICATION_STARTED);
        Environment.initializeEnvironment();
        if (checkUpdate()) {
            return;
        }
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
            if (FormsController.isAutoPaste()) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                if (clipboard.hasString()) {
                    String clipboardText = clipboard.getString();
                    if (Utility.isURL(clipboardText)) {
                        FormsController.pasteFromClipboard(clipboardText);
                    }
                }
            }
        }));
        scene = Constants.getScene(ap);
        scene.setOnContextMenuRequested(e -> getRightClickContextMenu().show(scene.getWindow(), e.getScreenX(), e.getScreenY()));
        primaryStage.setScene(scene);
        primaryStage.show();
        menu.setUseSystemMenuBar(true);
        FormsController.initLogic(gridPane);
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
        contactUs.setOnAction(e -> openWebsite("https://saptarshisarkar12.github.io/Drifty/contact.html"));
        contribute.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty"));
        bug.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=bug%2CApp&template=Bug-for-application.yaml&title=%5BBUG%5D+"));
        securityVulnerability.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/security/advisories/new"));
        feature.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=enhancement%2CApp&template=feature-request-application.yaml&title=%5BFEATURE%5D+"));
        menu.getItems().setAll(contactUs, contribute, bug, securityVulnerability, feature);
        return menu;
    }

    private Menu getEditMenu() {
        Menu menu = new Menu("Edit");
        MenuItem wipeHistory = new MenuItem("Clear Download History");
        wipeHistory.setOnAction(e -> {
            AskYesNo ask = new AskYesNo("Clear Download History", "Are you sure you wish to wipe out all of your download history?\n(This will NOT delete any downloaded files)", false);
            if (ask.getResponse().isYes()) {
                FormsController.clearJobHistory();
            }
        });
        menu.getItems().addAll(wipeHistory);
        return menu;
    }

    private ContextMenu getRightClickContextMenu() {
        MenuItem miAdd = new MenuItem("Add Directory");
        MenuItem miDir = new MenuItem("Manage Directories");
        miAdd.setOnAction(e -> FormsController.getDirectory());
        miDir.setOnAction(e -> {
            ManageFolders manage = new ManageFolders();
            manage.showScene();
            FormsController.resetDownloadFoldersToActiveList();
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

    public static boolean checkUpdate() throws URISyntaxException {
        String latestVersion = getLatestVersion();
        if(isNewerVersion(latestVersion , VERSION_NUMBER)){
            String Link;
            String oldFilePath = String.valueOf(CLI.Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            String newFilePath = "";
            String OS_NAME = OS.getOSName();
            if (OS_NAME.contains("win")) {
                Link = "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty-GUI.exe";
                String fileName = "Drifty_GUI.exe";
                String dirPath = String.valueOf(Paths.get(System.getenv("LOCALAPPDATA"), "Drifty", "updates"));
                FileDownloader downloader =  new FileDownloader(Link , fileName , dirPath);
                downloader.run();
                newFilePath = dirPath +'\\' + fileName;
            } else if (OS_NAME.contains("mac")) {
                Link = "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty-GUI.pkg";
                String fileName = "Drifty-GUI.pkg";
                String dirPath = ".drifty/updates";
                FileDownloader downloader =  new FileDownloader(Link , fileName , dirPath);
                downloader.run();
                newFilePath = dirPath +'\\' + fileName;
            } else if (OS_NAME.contains("linux")) {
                Link = "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty-GUI_linux";
                String fileName = "Drifty-GUI_linux";
                String dirPath = ".drifty/updates";
                FileDownloader downloader = new FileDownloader(Link, fileName, dirPath);
                downloader.run();
                newFilePath = dirPath +'\\' + fileName;
            }
            Updater.replaceUpdate(oldFilePath , newFilePath);
            return true;
        }
        return false;
    }

    private static String getLatestVersion() {
        try {
            URL url = new URI("https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases/latest").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.readLine();
            reader.close();

            // Parse JSON response to get the "tag_name"
            // For simplicity, we're assuming the tag_name is a string enclosed in double quotes
            int start = response.indexOf("\"tag_name\":\"") + 12;
            int end = response.indexOf("\"", start);
            System.out.println( response.substring(start, end));
            return response.substring(start, end);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static boolean isNewerVersion(String newVersion, String currentVersion) {
        // Split version strings into arrays of integers
        String[] newVersionParts = newVersion.replaceAll("[^\\d.]", "").split("\\.");
        String[] currentVersionParts = currentVersion.replaceAll("[^\\d.]", "").split("\\.");

        // Convert parts to integers
        int[] newVersionNumbers = new int[newVersionParts.length];
        int[] currentVersionNumbers = new int[currentVersionParts.length];

        for (int i = 0; i < newVersionParts.length; i++) {
            newVersionNumbers[i] = Integer.parseInt(newVersionParts[i]);
        }

        for (int i = 0; i < currentVersionParts.length; i++) {
            currentVersionNumbers[i] = Integer.parseInt(currentVersionParts[i]);
        }

        // Compare version numbers
        for (int i = 0; i < Math.min(newVersionNumbers.length, currentVersionNumbers.length); i++) {
            if (newVersionNumbers[i] > currentVersionNumbers[i]) {
                return true; // New version is greater
            } else if (newVersionNumbers[i] < currentVersionNumbers[i]) {
                return false; // Current version is greater
            }
        }

        // If all compared parts are equal, consider the longer version as newer
        return newVersionNumbers.length > currentVersionNumbers.length;
    }

}
