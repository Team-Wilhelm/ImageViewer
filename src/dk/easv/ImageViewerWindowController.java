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
    private Label lblFileName, lblRed, lblGreen, lblBlue, lblMixed;
    private int delay;
    private boolean isRunning = true;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Task<Integer> task;

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
                imageMap.put(image, f.getAbsolutePath());
                images.add(image);
            });
            displayImage();
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
        Image image;
        if (!images.isEmpty()) {
            image = images.get(currentImageIndex);
            imageView.setImage(image);
            lblFileName.setText(imageMap.get(image));

            lblRed.setText("R: ");
            lblGreen.setText("G: ");
            lblBlue.setText("B: ");
            lblMixed.setText("Mixed: ");
            colourCounter(image);

            lblFileName.setText(imageMap.get(image));
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

    private void colourCounter(Image image) {
        File file = new File(imageMap.get(image));
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int clr = bufferedImage.getRGB(i, j);
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;

                HashMap<String, Integer> colourMap = new HashMap<>();
                colourMap.put("red", red);
                colourMap.put("green", green);
                colourMap.put("blue", blue);

                String highest = Collections.max(colourMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
                Comparator<Integer> comparator = new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        return (Integer) o1 - (Integer) o2;
                    }
                };

                if (comparator.compare(red, green) == 0
                        || comparator.compare(red, blue) == 0
                        || comparator.compare(green, blue) == 0) {
                    highest = "mixed";
                }

                switch (highest) {
                    case "red" -> {
                        if (lblRed.getText().length() > 3) {
                            int value = Integer.parseInt(lblRed.getText().substring(3)) + 1;
                            lblRed.setText("R: " + value);
                        } else {
                            lblRed.setText("R: 1");
                        }
                    }
                    case "green" -> {
                        if (lblGreen.getText().length() > 3) {
                            int value = Integer.parseInt(lblGreen.getText().substring(3)) + 1;
                            lblGreen.setText("G: " + value);
                        } else {
                            lblGreen.setText("G: 1");
                        }
                    }
                    case "blue" -> {
                        if (lblBlue.getText().length() > 3) {
                            int value = Integer.parseInt(lblBlue.getText().substring(3)) + 1;
                            lblBlue.setText("B: " + value);
                        } else {
                            lblBlue.setText("B: 1");
                        }
                    }
                    case "mixed" -> {
                        if (lblMixed.getText().length() > 7) {
                            int value = Integer.parseInt(lblMixed.getText().substring(7)) + 1;
                            lblMixed.setText("Mixed: " + value);
                        } else {
                            lblMixed.setText("Mixed: 1");
                        }
                    }
                }
            }
        }
    }

        //Start slideshow: All the images, which the user has selected from a folder,
        // should be displayed as a slideshow.
        // Each image should be displayed for a customizable number of seconds (between 1 and 5).
        // Then the last image has been displayed,
        // the slideshow should go back to the first image and continue the slideshow presentation.
        // The slideshow presentation must be implemented using threads.
        // Stop slideshow: This should stop the slideshow presentation.
}