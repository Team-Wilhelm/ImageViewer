package dk.easv;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.DragEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

public class ImageViewerWindowController implements Initializable {
    @FXML
    private Parent root;
    @FXML
    private ImageView imageView;
    @FXML
    private Slider sliderDelay;
    @FXML
    private Button btnStartSlideshow, btnStopSlideshow;
    @FXML
    private Label lblFileName, lblRed, lblGreen, lblBlue, lblMixed;

    private int delay;
    private boolean isRunning = true;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Task<Integer> task;
    private final List<ImageWrapper> images = new ArrayList<>();
    private int currentImageIndex = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        sliderDelay.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            delay = newValue.intValue();
        });
        delay = 1;
    }

    @FXML
    private void handleBtnLoadAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());

        if (!files.isEmpty()) {
            files.forEach((File f) ->
            {
                Image image = new Image(f.toURI().toString());
                ImageWrapper imageWrapper = new ImageWrapper(image, f.getAbsolutePath());
                Task<ImageWrapper> task = new ColourCounterTask(imageWrapper);

                task.setOnSucceeded(event -> {
                    displayImage();
                });

                executorService.submit(task);
                images.add(imageWrapper);
            });
        }
    }

    @FXML
    private void handleBtnPreviousAction() {
        if (!images.isEmpty()) {
            currentImageIndex =
                    (currentImageIndex - 1 + images.size()) % images.size();
            displayImage();
        }
    }

    @FXML
    private void handleBtnNextAction() {
        if (!images.isEmpty()) {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            displayImage();
        }
    }

    private void displayImage() {
        ImageWrapper image;
        if (!images.isEmpty()) {
            image = images.get(currentImageIndex);
            imageView.setImage(image.getImage());
            lblFileName.setText(image.getFileName());

            lblRed.setText("R: " + image.getRed());
            lblGreen.setText("G: " + image.getGreen());
            lblBlue.setText("B: " + image.getBlue());
            lblMixed.setText("Mixed: " + image.getMixed());
        }
    }

    public void handleBtnStartSlideshow(ActionEvent actionEvent) {
        btnStartSlideshow.setDisable(true);
        btnStopSlideshow.setDisable(false);
        isRunning = true;
        task = new SlideshowTask(this);
        task.setOnSucceeded(event -> {
            currentImageIndex = task.getValue();
            displayImage();
            if (isRunning) {
                handleBtnStartSlideshow(actionEvent);
            }
        });
        executorService.submit(task);
    }

    public void handleBtnStopSlideshow(ActionEvent actionEvent) {
        isRunning = false;
        btnStartSlideshow.setDisable(false);
        btnStopSlideshow.setDisable(true);
    }

    public void handleSliderDelay(DragEvent dragEvent) {
    }

    public List<ImageWrapper> getImages() {
        return images;
    }

    public int getCurrentImageIndex() {
        return currentImageIndex;
    }

    public int getDelay() {
        return delay;
    }


        //Start slideshow: All the images, which the user has selected from a folder,
        // should be displayed as a slideshow.
        // Each image should be displayed for a customizable number of seconds (between 1 and 5).
        // Then the last image has been displayed,
        // the slideshow should go back to the first image and continue the slideshow presentation.
        // The slideshow presentation must be implemented using threads.
        // Stop slideshow: This should stop the slideshow presentation.
}