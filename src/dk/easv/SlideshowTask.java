package dk.easv;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class SlideshowTask extends Task<Image> {
    private int delay;
    private int currentImageIndex = 0;
    private List<Image> images = new ArrayList<>();
    private ImageViewerWindowController controller;


    public SlideshowTask(ImageViewerWindowController controller) {
        this.delay = controller.getDelay();
        this.currentImageIndex = controller.getCurrentImageIndex();
        this.images = controller.getImages();
        this.controller = controller;
    }

    @Override
    protected Image call() throws Exception {
        try {
            Thread.sleep(delay * 1000);

            if (currentImageIndex < images.size() - 1) {
                currentImageIndex++;
            } else {
                currentImageIndex = 0;
            }
            updateMessage(String.valueOf(currentImageIndex));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return images.get(currentImageIndex);
    }
}
