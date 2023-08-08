package GUI.experiment;

import GUI.Forms.ConsoleOut;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class MainGridPane extends GridPane {

    private ConsoleOut consoleOut;
    private boolean consoleOpen = false;
    private final ImageView ivLogo = newImageView(Constants.imgMainGUIBanner, .45);
    private final ImageView ivLink = newImageView(Constants.imgLinkLabel, .5);
    private final ImageView ivDir = newImageView(Constants.imgDirLabel,.5);
    private final ImageView ivFilename = newImageView(Constants.imgFilenameLabel,.5);
    private final ImageView ivAutoPaste = newImageView(Constants.imgAutoPasteLabel,.5);
    private final ImageView ivBtnDownload = imageViewButton(Constants.imgDownloadUp, Constants.imgDownloadDown, .45);
    private final ImageView ivBtnBatch = imageViewButton(Constants.imgBatchUp, Constants.imgBatchDown, .45);
    private final ImageView ivBtnConsole = imageToggle(.45);

    private final CheckBox cbAutoPaste = new CheckBox();

    private final HBox boxAutoPaste = boxAutoPaste();
    private final HBox boxLogo = boxLogo();

    private final Label lblLinkOutput = newLabel();
    private final Label lblDirOutput = newLabel();
    private final Label lblFilenameOutput = newLabel();
    private final Label lblTaskOutput = newLabel();

    private final TextField tfLink = newTextField();
    private final TextField tfDir = newTextField();
    private final TextField tfFilename = newTextField();

    private final ProgressBar pBar = pBar();

    public MainGridPane() {
        addGUI();
    }

    private void addGUI() {
        this.setHgap(20);
        this.setVgap(10);
        setColumnSpan(boxLogo,3);
        setColumnSpan(pBar,3);
        setColumnSpan(boxAutoPaste,2);
        setColumnSpan(ivDir,3);
        setColumnSpan(ivFilename,3);
        setColumnSpan(tfLink,3);
        setColumnSpan(tfDir,3);
        setColumnSpan(tfFilename,3);
        setColumnSpan(lblLinkOutput,3);
        setColumnSpan(lblDirOutput,3);
        setColumnSpan(lblFilenameOutput,3);
        setColumnSpan(lblTaskOutput,3);
        setRowSpan(pBar,2);
        add(boxLogo,0,0);
        add(pBar,0,1);
        add(ivLink,0,3);
        add(boxAutoPaste,1,3);
        add(tfLink,0,4);
        add(lblLinkOutput,0,5);

        add(ivDir,0,6);
        add(tfDir,0,7);
        add(lblDirOutput,0,8);

        add(ivFilename,0,9);
        add(tfFilename,0,10);
        add(lblFilenameOutput,0,11);
        add(lblTaskOutput,0,12);

        add(ivBtnDownload,0,13);
        add(ivBtnBatch,2,13);

        setPrefWidth(Constants.screenSize.getWidth() * .4);
        setPrefHeight(Constants.screenSize.getHeight() * .4);
        setVgrow(pBar,Priority.ALWAYS);
        setVgrow(lblDirOutput,Priority.ALWAYS);
        setVgrow(lblFilenameOutput,Priority.ALWAYS);
        setVgrow(lblLinkOutput,Priority.ALWAYS);
        setVgrow(ivBtnBatch,Priority.ALWAYS);
        setVgap(5);
        for (int colIndex = 0; colIndex < 3; colIndex++) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setHgrow(Priority.ALWAYS);
            getColumnConstraints().add(constraints);
        }
        GridPane.setMargin(boxLogo, new Insets(0,0,50,0));
        GridPane.setMargin(pBar, new Insets(0,0,25,0));
    }

    public void bindToWorker(final Worker<ObservableList<Long>> worker) {
        lblTaskOutput.textProperty().bind(worker.messageProperty());
        pBar.progressProperty().bind(worker.progressProperty());
    }


    private Label newLabel() {
        Label label = new Label("");
        label.setPrefWidth(Double.MAX_VALUE);
        return label;
    }

    private TextField newTextField() {
        TextField tf = new TextField("");
        tf.setPrefWidth(Double.MAX_VALUE);
        return tf;
    }
    private ProgressBar pBar(){
        ProgressBar pb = new ProgressBar();
        pb.setPrefWidth(Double.MAX_VALUE);
        pb.setMinHeight(25);
        return pb;
    }

    private HBox boxLogo() {
        HBox box = new HBox(ivLogo);
        box.setAlignment(Pos.CENTER);
        return box;
    }
    private HBox boxAutoPaste() {
        HBox box = new HBox(ivAutoPaste, cbAutoPaste);
        box.setAlignment(Pos.CENTER_RIGHT);
        return box;
    }

    private ImageView newImageView(Image image, double scale) {
        ImageView iv = new ImageView(image);
        double width = image.getWidth();
        iv.setPreserveRatio(true);
        iv.setFitWidth(width * scale);
        return iv;
    }

    private ImageView imageViewButton(Image imageUp, Image imageDown, double scale) {
        ImageView imageView = new ImageView(imageUp);
        double width = imageUp.getWidth();
        imageView.setOnMouseReleased(e -> imageView.setImage(imageUp));
        imageView.setOnMousePressed(e -> imageView.setImage(imageDown));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(width * scale);
        return imageView;
    }

    private ImageView imageToggle(double scale) {
        ImageView imageView = new ImageView(Constants.imgUpUp);
        imageView.setOnMousePressed(e -> imageView.setImage(Constants.imgUpDown));
        imageView.setOnMouseReleased(e -> imageView.setImage(Constants.imgUpUp));
        imageView.setOnMouseClicked(e-> toggleConsole(false));
        double width = Constants.imgUpUp.getWidth();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(width * scale);
        return imageView;
    }

    private void toggleConsole(boolean close) {
        if (consoleOpen || close) {
            ivBtnConsole.setImage(Constants.imgUpUp);
            ivBtnConsole.setOnMousePressed(e -> ivBtnConsole.setImage(Constants.imgUpDown));
            ivBtnConsole.setOnMouseReleased(e -> ivBtnConsole.setImage(Constants.imgUpUp));
            consoleOut.hide();
            consoleOpen = false;
        }

        else {
            ivBtnConsole.setImage(Constants.imgDownUp);
            ivBtnConsole.setOnMousePressed(e -> ivBtnConsole.setImage(Constants.imgDownDown));
            ivBtnConsole.setOnMouseReleased(e -> ivBtnConsole.setImage(Constants.imgDownUp));
            consoleOut.show();
            consoleOpen = true;
        }
    }

}
