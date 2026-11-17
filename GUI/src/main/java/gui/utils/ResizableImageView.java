package gui.utils;

import javafx.geometry.Orientation;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/** An image that participates in its parent's layout without imposing a minimum size. */
final class ResizableImageView extends ImageView {
    private final double preferredScale;

    ResizableImageView(Image image, double preferredScale) {
        super(image);
        this.preferredScale = preferredScale;
        setPreserveRatio(true);
        setSmooth(true);
        resize(prefWidth(-1), prefHeight(-1));
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

    @Override
    public double minWidth(double height) {
        return 0;
    }

    @Override
    public double minHeight(double width) {
        return 0;
    }

    @Override
    public double prefWidth(double height) {
        Image image = getImage();
        return image == null ? 0 : image.getWidth() * this.preferredScale;
    }

    @Override
    public double prefHeight(double width) {
        Image image = getImage();
        if (image == null || image.getWidth() <= 0) {
            return 0;
        }
        return width < 0 ? image.getHeight() * this.preferredScale : width * image.getHeight() / image.getWidth();
    }

    @Override
    public double maxWidth(double height) {
        return Double.MAX_VALUE;
    }

    @Override
    public double maxHeight(double width) {
        return Double.MAX_VALUE;
    }

    @Override
    public void resize(double width, double height) {
        // ImageView treats zero as unconstrained, which would restore the image's original size.
        setFitWidth(Math.max(width, Double.MIN_NORMAL));
        setFitHeight(Math.max(height, Double.MIN_NORMAL));
    }
}
