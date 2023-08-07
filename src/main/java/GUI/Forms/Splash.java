package GUI.Forms;

import Enums.Mode;
import Utils.Utility;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static javafx.scene.layout.AnchorPane.*;

public class Splash extends Application {

    private double width;
    private double height;
    private static Splash INSTANCE;
    private Stage stage;
    private final ProgressBar pb = new ProgressBar();
    private boolean animationDone = false;
    private boolean loading = true;
    private boolean runProgress = true;
    boolean switchedKeys = false;
    long start = System.currentTimeMillis();


    public static void main(String[] args) {
        launch(args);
    }

    public static void almostDone() {
        if(INSTANCE != null) {
            INSTANCE.loading = false;
        }
    }

    public static void close() {
        if(INSTANCE != null) {
            INSTANCE.stage.close();
            INSTANCE.timeline.stop();
            INSTANCE.runProgress = false;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if(Mode.devMode()){
            startMain();
            return;
        }
        INSTANCE = this;
        stage = Constants.getStage(primaryStage);
        stage = Constants.getStage();
        Image image = new Image(Constants.SPLASH.toExternalForm());
        width = image.getWidth();
        height = image.getHeight();
        double fitWidth = Constants.screenSize.getWidth() * .45;
        ImageView ivSplash = new ImageView(image);
        ivSplash.setPreserveRatio(true);
        ivSplash.setFitWidth(fitWidth);
        AnchorPane pane = new AnchorPane();
        pane.setStyle("-fx-background-color: transparent");
        pane.getChildren().addAll(ivSplash, pb);
        double top = 285;
        double bottom = 200;
        if (height != 1120) {
            top = Utility.reMap(top, 1120, 0, 0, height) * .85;
            bottom = Utility.reMap(bottom, 1120, 0, 0, height) * .9;
        }

        placeControl(pb, 250, 145, top, bottom);
        pb.getStylesheets().add(Constants.progressBarCSS.toExternalForm());
        Scene scene = new Scene(pane);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
        startMain();
        doProgress();
    }

    private void startMain() {
        new Thread(() -> new Main().start()).start();
    }

    public static boolean animationNotDone() {
        if(INSTANCE != null) {
            return !INSTANCE.animationDone;
        }
        return true;
    }

    private KeyFrame[] getKeyframes(long startingKeyframe) {
        long totalTime = 10000;
        long timeAdd = totalTime / 100;
        long time = 0;
        if (startingKeyframe > 1) {
            double m = 100 - startingKeyframe;
            timeAdd = (long) (2000 / m);
        }

        double progress = .01 * startingKeyframe;
        LinkedList<KeyFrame> list = new LinkedList<>();
        int start = (int) startingKeyframe;
        while (progress < 1) {
            progress += .01;
            double finprog = progress;
            list.addLast(new KeyFrame(Duration.millis(time), e -> pb.setProgress(finprog)));
            time += timeAdd;
        }

        return list.toArray(new KeyFrame[]{});
    }

    private void doProgress() {
        new Thread(() -> {
            while (runProgress) {
                timeline = new Timeline(getKeyframes(1));
                timeline.setCycleCount(1);
                timeline.play();
                while (loading) {
                    sleep(100);
                }

                timeline.stop();
                double progress = pb.getProgress();
                double remaining = 1 - progress;
                long steps = (long) (remaining * 100);
                double inc = remaining / steps;
                long delay = 1500 / steps;
                for (int i = 0; i < steps; i++) {
                    pb.setProgress(progress);
                    progress += inc;
                    sleep(delay);
                }

                animationDone = true;
            }

        }).start();
    }

    private Timeline timeline;

    private long getKeyFrame() {
        double tTime = timeline.getCycleDuration().toMillis();
        double cTime = timeline.getCurrentTime().toMillis();
        double tKeyFrames = timeline.getKeyFrames().size();
        double cKeyFrame = (cTime / tTime) * tKeyFrames;
        return (long) cKeyFrame;
    }

    private void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

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
}
