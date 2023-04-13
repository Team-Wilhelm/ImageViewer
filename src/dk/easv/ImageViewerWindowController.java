package dk.easv;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.input.DragEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImageViewerWindowController implements Initializable {
    private final List<Image> images = new ArrayList<>();
    private HashMap<Image, String> imageMap = new HashMap<>();
    private int currentImageIndex = 0;

    @FXML
    private Parent root;

    @FXML
    private ImageView imageView;

    @FXML
    private Slider sliderDelay;
    @FXML
    private Button btnStartSlideshow, btnStopSlideshow;
    @FXML
    private Label lblFileName;
    private int delay;
    private boolean isRunning = true;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Task<Object[]> task;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        sliderDelay.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            delay = newValue.intValue();
            System.out.println("Delay: " + delay);
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

        if (!files.isEmpty())
        {
            files.forEach((File f) ->
            {
                Image image = new Image(f.toURI().toString());
                imageMap.put(image, f.getName());
                images.add(image);
            });
            displayImage();
        }
    }

    @FXML
    private void handleBtnPreviousAction()
    {
        if (!images.isEmpty())
        {
            currentImageIndex =
                    (currentImageIndex - 1 + images.size()) % images.size();
            displayImage();
        }
    }

    @FXML
    private void handleBtnNextAction()
    {
        if (!images.isEmpty())
        {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            displayImage();
        }
    }

    private void displayImage()
    {
        if (!images.isEmpty())
        {
            imageView.setImage(images.get(currentImageIndex));
        }
    }

    public void handleBtnStartSlideshow(ActionEvent actionEvent) {
        btnStartSlideshow.setDisable(true);
        btnStopSlideshow.setDisable(false);
        isRunning = true;
        task = new SlideshowTask(this);
        task.setOnSucceeded(event -> {
            Image image = (Image) task.getValue()[0];
            imageView.setImage(image);
            currentImageIndex = (int) task.getValue()[1];
            lblFileName.setText(imageMap.get(image));
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

    public List<Image> getImages() {
        return images;
    }

    public int getCurrentImageIndex() {
        return currentImageIndex;
    }

    public int getDelay() {
        return delay;
    }

    public HashMap<Image, String> getImageMap() {
        return imageMap;
    }

    //Start slideshow: All the images, which the user has selected from a folder,
    // should be displayed as a slideshow.
    // Each image should be displayed for a customizable number of seconds (between 1 and 5).
    // Then the last image has been displayed,
    // the slideshow should go back to the first image and continue the slideshow presentation.
    // The slideshow presentation must be implemented using threads.
    // Stop slideshow: This should stop the slideshow presentation.
}