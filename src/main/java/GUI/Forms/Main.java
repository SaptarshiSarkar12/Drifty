package GUI.Forms;

import Backend.FileDownloader;
import Enums.Mode;
import Enums.OS;
import Updater.Updater;
import Utils.DriftyConstants;
import Utils.Environment;
import Utils.MessageBroker;
import Utils.Utility;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static Utils.DriftyConstants.DRIFTY_WEBSITE_URL;
import static Utils.DriftyConstants.VERSION_NUMBER;
import static javafx.scene.layout.AnchorPane.*;

public class Main extends Application {
    private static Main INSTANCE;
    private static MessageBroker M;
    private Stage primaryStage;
    private Scene scene;
    private boolean firstRun = true;

    public static void main(String[] args) throws URISyntaxException {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        Mode.setGUIMode();
        M = new MessageBroker();
        Environment.setMessageBroker(M);
        M.msgLogInfo(DriftyConstants.GUI_APPLICATION_STARTED);
        Environment.initializeEnvironment();
        if(isUpdateAvailable()){
            return;
        }
        //Utility.setStartTime();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage = Constants.getStage("Drifty GUI", true);
        this.primaryStage = primaryStage;
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
        primaryStage.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (firstRun) {
                firstRun = false;
                return;
            }
            if (GUI_Logic.isAutoPaste()) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                if (clipboard.hasString()) {
                    String clipboardText = clipboard.getString();
                    if (Utility.isURL(clipboardText)) {
                        GUI_Logic.pasteFromClipboard(clipboardText);
                    }
                }
            }
        }));
        scene = Constants.getScene(ap);
        scene.setOnContextMenuRequested(e -> getRightClickContextMenu().show(scene.getWindow(), e.getScreenX(), e.getScreenY()));
        primaryStage.setScene(scene);
        primaryStage.show();
        menu.setUseSystemMenuBar(true);
        GUI_Logic.initLogic(gridPane);
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
            M.msgLogInfo(DriftyConstants.GUI_APPLICATION_TERMINATED);
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
                GUI_Logic.clearJobHistory();
            }
        });
        menu.getItems().addAll(wipeHistory);
        return menu;
    }

    private ContextMenu getRightClickContextMenu() {
        MenuItem miAdd = new MenuItem("Add Directory");
        MenuItem miDir = new MenuItem("Manage Directories");
        miAdd.setOnAction(e -> GUI_Logic.getDirectory());
        miDir.setOnAction(e -> {
            ManageFolders manage = new ManageFolders();
            manage.showScene();
            GUI_Logic.resetDownloadFoldersToActiveList();
        });
        ContextMenu contextMenu = new ContextMenu(miAdd, miDir);
        contextMenu.getStyleClass().add("rightClick");
        return contextMenu;
    }

    protected static void openWebsite(String websiteURL) {
        INSTANCE.getHostServices().showDocument(websiteURL);
    }

    public static void toggleFullScreen() {
        INSTANCE.primaryStage.setFullScreen(!INSTANCE.primaryStage.isFullScreen());
    }

    public static boolean isUpdateAvailable() throws URISyntaxException {
        String latestVersion = getLatestVersion();
        if(isNewerVersion(latestVersion , VERSION_NUMBER)){
            String[] osNames = {"win" , "mac", "linux"};
            String[] executableNames = {"Drifty-GUI.exe" , "Drifty-GUI.pkg" , "Drifty-GUI_linux"};
            String[] updaterNames = {"updater.exe", "updater_macos" , "updater_linux"};
            String[] dirPaths = {String.valueOf(Paths.get(System.getenv("LOCALAPPDATA"), "Drifty", "updates")), ".drifty/updates", ".drifty/updates"};
            String oldFilePath = "";
            try {
                oldFilePath = String.valueOf(CLI.Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            } catch (URISyntaxException e) {
                System.out.println("Unable to fetch OldFlePath");
            }
            String decodedOldFilePath = URLDecoder.decode(oldFilePath, StandardCharsets.UTF_8);
            String currentOSName = OS.getOSName();
            String newFilePath = "";
            String updaterExecutablePath = "";
            for (int i = 0; i < osNames.length; i++) {
                if (currentOSName.contains(osNames[i])) {
                    String executableUrl = "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/" + executableNames[i];
                    String updaterUrl = "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/" + updaterNames[i];
                    String fileName = executableNames[i];
                    String dirPath = dirPaths[i];
                    String updaterName = updaterNames[i];
                    FileDownloader executableDownloader = new FileDownloader(executableUrl, fileName, dirPath);
                    executableDownloader.run();
                    FileDownloader updaterDownloader = new FileDownloader(updaterUrl, updaterName, dirPath);
                    updaterDownloader.run();
                    newFilePath = Path.of(dirPath, fileName).toString();
                    updaterExecutablePath = Path.of(dirPath, updaterName).toString();
                    break;
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ProcessBuilder processBuilder = new ProcessBuilder(updaterExecutablePath, decodedOldFilePath, newFilePath , "GUI");
            try {
                processBuilder.start();
            } catch (IOException e) {
                System.out.println("Failed to run Updater !");
            }
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
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
            String tagValue = jsonObject.get("tag_name").getAsString();
            System.out.println(tagValue);
            return tagValue;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static boolean isNewerVersion(String newVersion, String currentVersion) {
        newVersion = newVersion.replaceFirst("^v", "");
        currentVersion = currentVersion.replaceFirst("^v", "");
        String[] newVersionParts = newVersion.split("\\.");
        String[] currentVersionParts = currentVersion.split("\\.");
        int minLength = Math.min(newVersionParts.length, currentVersionParts.length);
        for (int i = 0; i < minLength; i++) {
            int newPart = Integer.parseInt(newVersionParts[i]);
            int currentPart = Integer.parseInt(currentVersionParts[i]);
            if (newPart > currentPart) {
                return true; // New version is greater
            } else if (newPart < currentPart) {
                return false; // Current version is greater
            }
        }
        // If all compared parts are equal, consider the longer version as newer
        return newVersionParts.length > currentVersionParts.length;
    }


}
