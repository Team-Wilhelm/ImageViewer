package dk.easv;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ColourCounterTask extends Task<ImageWrapper> {
    private ImageWrapper image;

    public ColourCounterTask(ImageWrapper image) {
        this.image = image;
    }

    @Override
    protected ImageWrapper call() throws Exception {
        colourCounter(image);
        return image;
    }

    private void colourCounter(ImageWrapper image) {
        File file = new File(image.getFileName());
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int width = (int) image.getImage().getWidth();
        int height = (int) image.getImage().getHeight();
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

                if (comparator.compare(red, green) == 0
                        || comparator.compare(red, blue) == 0
                        || comparator.compare(green, blue) == 0) {
                    highest = "mixed";
                }

                switch (highest) {
                    case "red" -> {
                        image.setRed(image.getRed() + 1);
                    }
                    case "green" -> {
                        image.setGreen(image.getGreen() + 1);
                        }
                    case "blue" -> {
                        image.setBlue(image.getBlue() + 1);
                    }
                    case "mixed" -> {
                        image.setMixed(image.getMixed() + 1);
                    }
                }
            }
        }
    }

    Comparator<Integer> comparator = (Comparator) (o1, o2) -> (Integer) o1 - (Integer) o2;
}
