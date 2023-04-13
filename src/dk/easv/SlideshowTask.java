package dk.easv;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SlideshowTask extends Task<Integer> {
    private int delay;
    private int currentImageIndex = 0;
    private List<ImageWrapper> images = new ArrayList<>();
    private ImageViewerWindowController controller;


    public SlideshowTask(ImageViewerWindowController controller) {
        this.delay = controller.getDelay();
        this.currentImageIndex = controller.getCurrentImageIndex();
        this.controller = controller;
        this.images = controller.getImages();
    }

    @Override
    protected Integer call() throws Exception {
        try {
            Thread.sleep(delay * 1000);

            if (currentImageIndex < images.size() - 1) {
                currentImageIndex++;
            } else {
                currentImageIndex = 0;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentImageIndex;
    }
}
