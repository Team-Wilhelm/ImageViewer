package dk.easv;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SlideshowTask extends Task<Object[]> {
    private int delay;
    private int currentImageIndex = 0;
    private List<Image> images = new ArrayList<>();
    private ImageViewerWindowController controller;
    private HashMap<Image, String> imageMap = new HashMap<>();


    public SlideshowTask(ImageViewerWindowController controller) {
        this.delay = controller.getDelay();
        this.currentImageIndex = controller.getCurrentImageIndex();
        this.images = controller.getImages();
        this.controller = controller;
        this.imageMap = controller.getImageMap();
    }

    @Override
    protected Object[] call() throws Exception {
        Image image;
        try {
            Thread.sleep(delay * 1000);

            if (currentImageIndex < images.size() - 1) {
                currentImageIndex++;
            } else {
                currentImageIndex = 0;
            }
            image = images.get(currentImageIndex);
            updateMessage(imageMap.get(image));
        } catch (InterruptedException e) {
            e.printStackTrace();
            image = null;
        }
        return new Object[]{image, currentImageIndex};
    }
}
