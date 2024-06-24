package ui;

import gui.preferences.AppSettings;
import gui.support.Constants;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import support.Job;

public class MainGridPane extends GridPane {
    public static ImageView ivLogo = Constants.UI_COMPONENT_BUILDER_INSTANCE.newImageView(Constants.imgMainGuiBanner, .45).build();
    public final ProgressBar pBar = pBar();
    public final ListView<Job> listView = listView();
    public final ImageView ivLink = Constants.UI_COMPONENT_BUILDER_INSTANCE.newImageView(Constants.IMG_LINK_LABEL, .7).build();
    public final ImageView ivDir = Constants.UI_COMPONENT_BUILDER_INSTANCE.newImageView(Constants.IMG_DIR_LABEL, .7).build();
    public final ImageView ivFilename = Constants.UI_COMPONENT_BUILDER_INSTANCE.newImageView(Constants.IMG_FILENAME_LABEL, .7).build();
    public final ImageView ivAutoPaste = Constants.UI_COMPONENT_BUILDER_INSTANCE.newImageView(Constants.IMG_AUTO_PASTE_LABEL, .7).build();
    public final Button btnStart = AppSettings.GET.mainTheme().equals("Dark") ? newButton(Constants.IMG_START_UP_DARK, Constants.IMG_START_DOWN_DARK) : newButton(Constants.IMG_START_UP_LIGHT, Constants.IMG_START_DOWN_LIGHT);
    public final Button btnSave = AppSettings.GET.mainTheme().equals("Dark") ? newButton(Constants.IMG_SAVE_UP_DARK, Constants.IMG_SAVE_DOWN_DARK) : newButton(Constants.IMG_SAVE_UP_LIGHT, Constants.IMG_SAVE_DOWN_LIGHT);
    public final CheckBox cbAutoPaste = new CheckBox();
    private final HBox boxAutoPaste = boxAutoPaste();
    public static HBox boxLogo = Constants.UI_COMPONENT_BUILDER_INSTANCE.newHBox(ivLogo).build();
    public final Label lblLinkOut = Constants.UI_COMPONENT_BUILDER_INSTANCE.newLabel();
    public final Label lblDirOut = Constants.UI_COMPONENT_BUILDER_INSTANCE.newLabel();
    public final Label lblFilenameOut = Constants.UI_COMPONENT_BUILDER_INSTANCE.newLabel();
    public final Label lblDownloadInfo = Constants.UI_COMPONENT_BUILDER_INSTANCE.newLabel();
    public final TextField tfLink = Constants.UI_COMPONENT_BUILDER_INSTANCE.newTextField();
    public final TextField tfDir = Constants.UI_COMPONENT_BUILDER_INSTANCE.newTextField();
    public final TextField tfFilename = Constants.UI_COMPONENT_BUILDER_INSTANCE.newTextField();

    public MainGridPane() {
        super();
        addGUI();
    }

    private void addGUI() {
        this.setHgap(20);
        this.setVgap(10);
        setColumnSpan(boxLogo, 4);
        setColumnSpan(pBar, 4);
        setColumnSpan(boxAutoPaste, 2);
        setColumnSpan(ivDir, 3);
        setColumnSpan(ivFilename, 3);
        setColumnSpan(tfLink, 3);
        setColumnSpan(tfDir, 3);
        setColumnSpan(tfFilename, 3);
        setColumnSpan(lblLinkOut, 3);
        setColumnSpan(lblDirOut, 3);
        setColumnSpan(lblFilenameOut, 3);
        setColumnSpan(lblDownloadInfo, 3);
        setRowSpan(listView, 11);
        add(boxLogo, 0, 0);
        add(pBar, 0, 1);

        add(listView, 0, 3);

        add(ivLink, 1, 3);
        add(boxAutoPaste, 2, 3);
        add(tfLink, 1, 4);
        add(lblLinkOut, 1, 5);

        add(ivDir, 1, 6);
        add(tfDir, 1, 7);
        add(lblDirOut, 1, 8);

        add(ivFilename, 1, 9);
        add(tfFilename, 1, 10);
        add(lblFilenameOut, 1, 11);
        add(lblDownloadInfo, 1, 12);

        add(btnSave, 1, 13);
        add(btnStart, 3, 13);

        setPrefWidth(Constants.SCREEN_WIDTH * .4);
        setPrefHeight(Constants.SCREEN_HEIGHT * .4);
        setVgrow(pBar, Priority.ALWAYS);
        setVgrow(lblDirOut, Priority.ALWAYS);
        setVgrow(lblFilenameOut, Priority.ALWAYS);
        setVgrow(lblLinkOut, Priority.ALWAYS);
        setVgrow(btnStart, Priority.ALWAYS);
        setVgrow(btnSave, Priority.ALWAYS);
        setVgap(5);
        setHgap(15);
        for (int colIndex = 0; colIndex < 3; colIndex++) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setHgrow(Priority.ALWAYS);
            getColumnConstraints().add(constraints);
        }
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setMaxWidth(250);
        getColumnConstraints().set(0, constraints);
        GridPane.setMargin(boxLogo, new Insets(0, 0, 50, 0));
        GridPane.setMargin(pBar, new Insets(0, 0, 25, 0));
    }

    private ListView<Job> listView() {
        ListView<Job> listView = new ListView<>();
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Job item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    // Make empty cells not selectable
                    setDisable(true);
                    Platform.runLater(() -> setText(""));
                } else {
                    // Set the text for non-empty cells
                    setDisable(false);
                    Platform.runLater(() -> setText(item.toString()));
                }
            }
        });

        listView.setMaxWidth(250);
        return listView;
    }

    private ProgressBar pBar() {
        ProgressBar pb = new ProgressBar(0.0);
        pb.setPrefWidth(Double.MAX_VALUE);
        pb.setMinHeight(25);
        return pb;
    }

    private HBox boxAutoPaste() {
        HBox box = new HBox(10, ivAutoPaste, cbAutoPaste);
        box.setAlignment(Pos.CENTER_RIGHT);
        return box;
    }

    private Button newButton(Image imageUp, Image imageDown) {
        Button button = new Button();
        ImageView imageViewUp = Constants.UI_COMPONENT_BUILDER_INSTANCE.newImageView(imageUp, 0.45).build();
        ImageView imageViewDn = Constants.UI_COMPONENT_BUILDER_INSTANCE.newImageView(imageDown, 0.45).build();
        button.setOnMousePressed(e -> button.setGraphic(imageViewDn));
        button.setOnMouseReleased(e -> button.setGraphic(imageViewUp));
        button.setGraphic(imageViewUp);
        button.getStyleClass().add("glassButton");
        return button;
    }
}
