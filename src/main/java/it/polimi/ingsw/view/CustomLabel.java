package it.polimi.ingsw.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

class CustomLabel extends JLabel {
    private Image image;
    private final int width;
    private final int height;
    private boolean empty;

    /**
     * Constructor of a new custom label
     * @param imagePath the image path
     * @param width the width
     * @param height the height
     */
    public CustomLabel(String imagePath, int width, int height) {
        this.width = width;
        this.height = height;
        image = loadImage(imagePath);
    }

    /**
     * Method that loads an image from the input path and adapts its dimension and sets it as the image
     * for the current element
     * @param imagePath the image path
     * @return the image
     */
    private Image loadImage(String imagePath){
        if(imagePath.equals("empty")){
            empty = true;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
            g.setComposite(alpha);
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(scaledImage));
            return scaledImage;
        }else{
            URI uri = null;
            InputStream inputStream = getClass().getResourceAsStream("/MyShelfie_BGA/" + imagePath);
            try {
                uri = ClassLoader.getSystemResource("MyShelfie_BGA/" + imagePath).toURI();
            } catch (URISyntaxException e) {
                System.err.println("Custom Label Error: "+ e.getMessage());
            }
            FileSystem fs = null;
            if("jar".equals(uri.getScheme()))
            {
                for(FileSystemProvider provider : FileSystemProvider.installedProviders())
                {
                    if(provider.getScheme().equalsIgnoreCase("jar"))
                    {
                        try {
                            fs = provider.getFileSystem(uri);
                        }catch (FileSystemNotFoundException e)
                        {
                            try {
                                fs = provider.newFileSystem(uri, Collections.emptyMap());
                            } catch (IOException ex) {
                                System.err.println("Custom label error: " + e.getMessage());
                            }
                        }
                    }
                }
            }
            try {
                BufferedImage image = ImageIO.read(inputStream);
                Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                setIcon(new ImageIcon(scaledImage));
                return scaledImage;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if(fs != null)
            {
                try {
                    fs.close();
                } catch (IOException e) {
                    System.err.println("Custom label error: " + e.getMessage());
                }
            }
            return null;
        }
    }

    /**
     * Method that draws the image in the current element if it's not null
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * Method that checks if the image is empty
     * @return true if it is, false otherwise
     */
    public boolean isEmpty(){
        return empty;
    }
}