package ui;

import gui.preferences.AppSettings;
import gui.support.Constants;
import gui.support.Folders;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ManageFolders {
    private Stage stage;
    private final double width = 400;
    private final double height = 550;
    private final Folders folders;
    private VBox vBox;
    private ListView<String> lvFolders;
    private javafx.scene.control.Button btnRemove;
    private javafx.scene.control.Button btnClose;

    public ManageFolders() {
        this.folders = AppSettings.GET.folders();
        createControls();
        setControls();
    }

    private void createControls() {
        lvFolders = listView(folders.getFolders());
        btnRemove = new javafx.scene.control.Button("Remove");
        btnClose = new javafx.scene.control.Button("Close");
        lvFolders.setMinWidth(width * .9);
        lvFolders.setMaxWidth(width * .9);
        lvFolders.setPrefWidth(width * .9);
        lvFolders.setMinHeight(height * .85);
        lvFolders.setMaxHeight(height * .85);
        lvFolders.setPrefHeight(height * .85);
        btnRemove.setMinWidth(65);
        btnRemove.setMaxWidth(65);
        btnRemove.setPrefWidth(65);
        btnRemove.setMinHeight(35);
        btnRemove.setMaxHeight(35);
        btnRemove.setPrefHeight(35);
        btnClose.setMinWidth(65);
        btnClose.setMaxWidth(65);
        btnClose.setPrefWidth(65);
        btnClose.setMinHeight(35);
        btnClose.setMaxHeight(35);
        btnClose.setPrefHeight(35);
        HBox hBox = new HBox(20, btnRemove, btnClose);
        hBox.setAlignment(Pos.CENTER);
        vBox = new VBox(15, lvFolders, hBox);
        vBox.setAlignment(Pos.CENTER);
    }

    private ListView<String> listView(ObservableList<String> folderList) {
        ListView<String> listView = new ListView<>(folderList);
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    // Make empty cells not selectable
                    setDisable(true);
                    setText("");
                } else {
                    // Set the text for non-empty cells
                    setDisable(false);
                    setText(item);
                }
            }
        });
        return listView;
    }

    private void setControls() {
        btnRemove.setOnAction(e -> remove());
        btnClose.setOnAction(e -> stage.close());
        BooleanBinding selected = lvFolders.getSelectionModel().selectedIndexProperty().greaterThan(-1);
        btnRemove.visibleProperty().bind(selected);
    }

    public void showScene() {
        stage = new Stage();
        Scene scene = Constants.getScene(vBox);
        stage.setScene(scene);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.showAndWait();
    }

    private void remove() {
        folders.removeFolder(lvFolders.getSelectionModel().getSelectedItem());
        lvFolders.getItems().setAll(folders.getFolders());
    }
}
