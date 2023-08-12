package GUI.Forms;

import Backend.Drifty;
import GUI.Support.ManageFolders;
import Preferences.AppSettings;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

import static GUI.Forms.Constants.*;
import static javafx.scene.layout.AnchorPane.*;

public class Main extends Application {
    private static Main INSTANCE;
    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        createScene();
        INSTANCE = this;
    }

    private void createScene() {
        AnchorPane ap = new AnchorPane();
        MainGridPane gridPane = new MainGridPane();
        MenuBar menu = menuBar(getMenuItemsOfMenu(), getWindowMenu(), getHelpMenuItems());
        placeControl(gridPane,40,40,40,40);
        placeControl(menu,0,0,0,-1);
        ap.getChildren().add(gridPane);
        ap.getChildren().add(menu);
        stage = new Stage();
        Scene scene = new Scene(ap);
        scene.getStylesheets().add(Objects.requireNonNull(contextMenuCSS).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(labelCSS).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(menuCSS).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(checkBoxCSS).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(textFieldCSS).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(vBoxCSS).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(sceneCSS).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(progressBarCSS).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(listViewCSS).toExternalForm());
        stage.setScene(scene);
        stage.show();
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
        website.setOnAction(e -> FormLogic.openWeb(Drifty.projectWebsite, "project website"));
        MenuItem about = new MenuItem("About");
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> System.exit(0));
        menu.getItems().setAll(website, about, exit);
        return menu;
    }

    private MenuBar menuBar(Menu... menus) {
        MenuBar menuBar = new MenuBar(menus);
        //menuBar.setPrefWidth(screenSize.getWidth());
        menuBar.setUseSystemMenuBar(true);
        return menuBar;
    }

    private Menu getWindowMenu() {
        Menu menu = new Menu("Window");
        MenuItem fullScreen = new MenuItem("Toggle Full Screen");
        fullScreen.setOnAction(e -> Main.toggleFullScreen());
        menu.getItems().setAll(fullScreen);
        return menu;
    }

    private Menu getHelpMenuItems() {
        Menu menu = new Menu("Help");
        MenuItem contactUs = new MenuItem("Contact Us");
        MenuItem contribute = new MenuItem("Contribute");
        MenuItem bug = new MenuItem("Report a Bug");
        MenuItem securityVulnerability = new MenuItem("Report a Security Vulnerability");
        MenuItem feature = new MenuItem("Suggest a Feature");
        contactUs.setOnAction(e -> FormLogic.openWeb("https://saptarshisarkar12.github.io/Drifty/contact.html", "contact us webpage"));
        contribute.setOnAction(e -> FormLogic.openWeb("https://github.com/SaptarshiSarkar12/Drifty", "repository website for contribution"));
        bug.setOnAction(e -> FormLogic.openWeb("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=bug%2CApp&template=Bug-for-application.yaml&title=%5BBUG%5D+", "issue webpage to file a Bug"));
        securityVulnerability.setOnAction(e -> FormLogic.openWeb("https://github.com/SaptarshiSarkar12/Drifty/security/advisories/new", "Security Vulnerability webpage"));
        feature.setOnAction(e -> FormLogic.openWeb("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=enhancement%2CApp&template=feature-request-application.yaml&title=%5BFEATURE%5D+", "issue webpage to suggest feature"));
        menu.getItems().setAll(contactUs, contribute, bug, securityVulnerability, feature);
        return menu;
    }

    private ContextMenu getRightClickContextMenu() {
        MenuItem miAdd = new MenuItem("Add Directory");
        MenuItem miDir = new MenuItem("Manage Directories");
        miAdd.setOnAction(e -> getDirectory());
        miDir.setOnAction(e -> {
            ManageFolders manage = new ManageFolders();
            manage.showScene();
            FormLogic.bumpFolders();
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
        //firstRun = true;
        if (directory != null) {
            FormLogic.setDir(directory.getAbsolutePath());
        }
    }

    protected static void openWebsite(String websiteURL, String websiteType) {
        HostServices hostServices = INSTANCE.getHostServices();
        hostServices.showDocument(websiteURL);
    }

    public static void toggleFullScreen() {
        INSTANCE.stage.setFullScreen(!INSTANCE.stage.isFullScreen());
    }
}