package gui.utils;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ui.MainGridPane;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class ResizableImageViewTest {
    private static final double TOLERANCE = .01;

    @BeforeClass
    public static void startToolkit() throws Exception {
        assumeTrue("JavaFX tests need a display; use xvfb-run on headless Linux",
                !System.getProperty("os.name").startsWith("Linux") || System.getenv("DISPLAY") != null);
        FutureTask<Void> startup = new FutureTask<>(() -> {
            Platform.setImplicitExit(false);
            return null;
        });
        Platform.startup(startup);
        startup.get(10, TimeUnit.SECONDS);
    }

    @AfterClass
    public static void stopToolkit() {
        Platform.exit();
    }

    @Test
    public void fitsWideAndTallContainersAndCanGrowAgain() throws Exception {
        onFxThread(() -> {
            ImageView image = image(400, 200);
            StackPane container = new StackPane(image);
            container.setPadding(new Insets(10));
            new Scene(container);
            for (double[] size : new double[][]{{220, 220}, {820, 120}, {60, 100}, {820, 620}}) {
                layout(container, size[0], size[1]);
                assertFits(image, size[0] - 20, size[1] - 20);
                assertEquals(Math.min(size[0] - 20, (size[1] - 20) * 2),
                        image.getLayoutBounds().getWidth(), TOLERANCE);
            }
        });
    }

    @Test
    public void zeroSizedContainersDoNotRestoreTheOriginalImageSize() throws Exception {
        onFxThread(() -> {
            ImageView image = image(400, 200);
            StackPane container = new StackPane(image);
            for (double[] size : new double[][]{{0, 0}, {0, 100}, {100, 0}, {100, 100}}) {
                layout(container, size[0], size[1]);
                assertFits(image, size[0], size[1]);
            }
        });
    }

    @Test
    public void preferredSizeSurvivesResizingAndTracksReplacementImages() throws Exception {
        onFxThread(() -> {
            ImageView image = image(400, 200);
            StackPane container = new StackPane(image);
            layout(container, 20, 20);
            assertEquals(200, image.prefWidth(-1), TOLERANCE);
            assertEquals(100, image.prefHeight(-1), TOLERANCE);
            image.setImage(new WritableImage(100, 400));
            layout(container, 300, 200);
            assertFits(image, 300, 200);
            assertEquals(50, image.prefWidth(-1), TOLERANCE);
            assertEquals(200, image.prefHeight(-1), TOLERANCE);
            assertEquals(50, image.getLayoutBounds().getWidth(), TOLERANCE);
        });
    }

    @Test
    public void boxLayoutsResizeImagesWithoutSizingFeedback() throws Exception {
        onFxThread(() -> {
            ImageView image = image(400, 200);
            HBox row = new HBox(image);
            HBox.setHgrow(image, Priority.ALWAYS);
            for (int width : new int[]{80, 600, 200, 600}) {
                layout(row, width, 300);
                assertFits(image, width, 300);
                assertEquals(width, image.getLayoutBounds().getWidth(), TOLERANCE);
            }
            row.getChildren().clear();
            VBox column = new VBox(image);
            VBox.setVgrow(image, Priority.ALWAYS);
            layout(column, 100, 25);
            assertFits(image, 100, 25);
            layout(column, 600, 400);
            assertFits(image, 600, 400);
            assertEquals(600, image.getLayoutBounds().getWidth(), TOLERANCE);
        });
    }

    @Test
    public void buttonGraphicsFitAfterResizingAndStateChanges() throws Exception {
        onFxThread(() -> {
            StackPane released = graphic();
            StackPane pressed = graphic();
            Button button = new Button();
            button.setMinSize(0, 0);
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            StackPane container = new StackPane(button);
            new Scene(container);
            for (StackPane graphic : new StackPane[]{released, pressed, released, graphic()}) {
                assertEquals(171, graphic.prefWidth(-1), TOLERANCE);
                assertEquals(61, graphic.prefHeight(-1), TOLERANCE);
                button.setGraphic(graphic);
                for (int width : new int[]{60, 300, 90}) {
                    layout(container, width, 50);
                    assertFitsButtonGraphic(button);
                }
            }
        });
    }

    @Test
    public void mainGridImagesFitSmallWindowsAndGrowWithTheBanner() throws Exception {
        onFxThread(() -> {
            MainGridPane grid = new MainGridPane();
            Scene scene = gui.support.Constants.getScene(grid);
            Stage stage = new Stage();
            stage.setOpacity(0);
            stage.setScene(scene);
            stage.show();
            String requestedScale = System.getProperty("glass.win.uiScale");
            if (requestedScale != null) {
                try {
                    double expectedScale = parseUiScale(requestedScale);
                    assertEquals(expectedScale, stage.getOutputScaleX(), TOLERANCE);
                    assertEquals(expectedScale, stage.getOutputScaleY(), TOLERANCE);
                } catch (NumberFormatException e) {
                    throw new AssertionError("Unsupported glass.win.uiScale value: " + requestedScale, e);
                }
            }
            System.out.println("Verified window output scale: " + stage.getOutputScaleX() + "x" + stage.getOutputScaleY());
            try {
                double smallBannerWidth = 0;
                for (int width : new int[]{600, 1200, 600}) {
                    layout(grid, width, 650);
                    assertFitsCell(MainGridPane.ivLogo, MainGridPane.boxLogo.getWidth(), MainGridPane.boxLogo.getHeight());
                    assertFitsCell(grid.ivLink, grid.getCellBounds(1, 3).getWidth(), grid.getCellBounds(1, 3).getHeight());
                    assertFitsCell(grid.ivDir, width - grid.getCellBounds(1, 6).getMinX(), grid.getCellBounds(1, 6).getHeight());
                    assertFitsCell(grid.ivFilename, width - grid.getCellBounds(1, 9).getMinX(), grid.getCellBounds(1, 9).getHeight());
                    assertFitsCell(grid.ivAutoPaste, grid.ivAutoPaste.getParent().getLayoutBounds().getWidth(),
                            grid.ivAutoPaste.getParent().getLayoutBounds().getHeight());
                    assertFitsButtonGraphic(grid.btnStart);
                    assertFitsButtonGraphic(grid.btnSave);
                    assertTrue("Button should retain its preferred height when space is available",
                            grid.btnStart.getHeight() >= grid.btnStart.prefHeight(-1) - TOLERANCE);
                    grid.btnStart.getOnMousePressed().handle(null);
                    grid.btnStart.layout();
                    assertFitsButtonGraphic(grid.btnStart);
                    grid.btnStart.getOnMouseReleased().handle(null);
                    grid.btnStart.layout();
                    assertFitsButtonGraphic(grid.btnStart);
                    if (width == 600) {
                        smallBannerWidth = MainGridPane.ivLogo.getLayoutBounds().getWidth();
                    } else {
                        assertTrue("Banner should grow with the window",
                                MainGridPane.ivLogo.getLayoutBounds().getWidth() > smallBannerWidth);
                    }
                }
            } finally {
                stage.close();
            }
        });
    }

    private static double parseUiScale(String scale) {
        String normalized = scale.trim();
        if (normalized.endsWith("%")) {
            return Double.parseDouble(normalized.substring(0, normalized.length() - 1)) / 100.0;
        }
        if (normalized.toLowerCase().endsWith("dpi")) {
            return Double.parseDouble(normalized.substring(0, normalized.length() - 3).trim()) / 96.0;
        }
        return Double.parseDouble(normalized);
    }

    private static ImageView image(int width, int height) {
        return UIComponentBuilder.getInstance().buildImageView(new WritableImage(width, height), .5);
    }

    private static StackPane graphic() {
        return UIComponentBuilder.getInstance().buildImageGraphic(new WritableImage(342, 122), .5);
    }

    private static void layout(Region container, double width, double height) {
        container.resize(width, height);
        container.applyCss();
        container.layout();
    }

    private static void assertFitsButtonGraphic(Button button) {
        StackPane graphic = (StackPane) button.getGraphic();
        ImageView image = (ImageView) graphic.getChildren().getFirst();
        assertFits(image, graphic.getWidth(), graphic.getHeight());
        Bounds bounds = image.getBoundsInParent();
        // Layout containers snap positions to the pixel grid, including at fractional display scales.
        assertEquals((graphic.getWidth() - bounds.getWidth()) / 2, bounds.getMinX(), 1);
        assertEquals((graphic.getHeight() - bounds.getHeight()) / 2, bounds.getMinY(), 1);
    }

    private static void assertFits(ImageView image, double width, double height) {
        assertFits(image, width, height, TOLERANCE);
    }

    private static void assertFits(ImageView image, double width, double height, double layoutTolerance) {
        Bounds bounds = image.getLayoutBounds();
        assertTrue("Image width " + bounds.getWidth() + " exceeds available width " + width,
                bounds.getWidth() <= width + layoutTolerance);
        assertTrue("Image height " + bounds.getHeight() + " exceeds available height " + height,
                bounds.getHeight() <= height + layoutTolerance);
        Image source = image.getImage();
        if (bounds.getWidth() > TOLERANCE && bounds.getHeight() > TOLERANCE) {
            assertEquals(source.getWidth() / source.getHeight(), bounds.getWidth() / bounds.getHeight(), TOLERANCE);
        }
    }

    private static void assertFitsCell(ImageView image, double width, double height) {
        // GridPane rounds cell metrics and child allocations separately at fractional display scales.
        double scale = Math.min(image.getScene().getWindow().getRenderScaleX(), image.getScene().getWindow().getRenderScaleY());
        assertFits(image, width, height, 1 / scale + TOLERANCE);
    }

    private static void onFxThread(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}
