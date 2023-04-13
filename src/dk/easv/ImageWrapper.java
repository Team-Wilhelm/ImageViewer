package dk.easv;

import javafx.scene.image.Image;

public class ImageWrapper {
    private Image image;
    private String fileName;
    private int red, green, blue, mixed;

    public ImageWrapper(Image image, String fileName) {
        this.image = image;
        this.fileName = fileName;
    }

    public Image getImage() {
        return image;
    }

    public String getFileName() {
        return fileName;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public int getMixed() {
        return mixed;
    }

    public void setMixed(int mixed) {
        this.mixed = mixed;
    }
}
