import java.io.File;
import javax.swing.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Random;

public class ColorFilm extends JPanel {

  private int width, height;
  private BufferedImage image = null;
  private byte[] pixels;
  private int[][] bb = {
    {255, 0, 0}, {255, 255, 0}, {255, 0, 255}
  };
  private int[][] bg = {
    {0, 255, 0}, {255, 255, 0}, {0, 255, 255}
  };
  private int[][] br = {
    {0, 0, 255}, {255, 0, 255}, {0, 255, 255}
  };
  private int[][] db = {
    {0, 255, 0}, {0, 0, 255}, {0, 255, 255}
  };
  private int[][] dg = {
    {255, 0, 0}, {0, 0, 255}, {255, 0, 255}
  };
  private int[][] dr = {
    {255, 0, 0}, {0, 255, 0}, {255, 255, 0}
  };

  private void addOutlines() {
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int color = image.getRGB(x, y);

        if (color == 0xFFFF0000) {
          if (image.getRGB(x, y-1) != color)
            image.setRGB(x, y-1, 0xFF00FFFF);
          if (image.getRGB(x, y+1) != color)
            image.setRGB(x, y+1, 0xFF00FFFF);
          if (image.getRGB(x-1, y) != color)
            image.setRGB(x-1, y, 0xFF00FFFF);
          if (image.getRGB(x+1, y) != color)
            image.setRGB(x+1, y, 0xFF00FFFF);
        } else if (color == 0xFF00FF00) {
          if (image.getRGB(x, y-1) != color)
            image.setRGB(x, y-1, 0xFFFF00FF);
          if (image.getRGB(x, y+1) != color)
            image.setRGB(x, y+1, 0xFFFF00FF);
          if (image.getRGB(x-1, y) != color)
            image.setRGB(x-1, y, 0xFFFF00FF);
          if (image.getRGB(x+1, y) != color)
            image.setRGB(x+1, y, 0xFFFF00FF);
        } else if (color == 0xFF0000FF) {
          if (image.getRGB(x, y-1) != color)
            image.setRGB(x, y-1, 0xFFFFFF00);
          if (image.getRGB(x, y+1) != color)
            image.setRGB(x, y+1, 0xFFFFFF00);
          if (image.getRGB(x-1, y) != color)
            image.setRGB(x-1, y, 0xFFFFFF00);
          if (image.getRGB(x+1, y) != color)
            image.setRGB(x+1, y, 0xFFFFFF00);
        }
      }
    }
  }

  private void generate() {
    for (int i = 0; i < width * height * 3; i+=3) {

      int[][] cset = null;

      int color = image.getRGB((i / 3) % width, (i / 3) / height);
      
      switch(color) {
        case 0xFFFF0000:
          cset = br;
          break;
        case 0xFF00FFFF:
          cset = dr;
          break;
        case 0xFF00FF00:
          cset = bg;
          break;
        case 0xFFFF00FF:
          cset = dg;
          break;
        case 0xFF0000FF:
          cset = bb;
          break;
        default:
          int c = (int)(Math.random() * 6);
          int[][][] csets = {bb, bg, br, db, dg, dr};
          cset = csets[c];
          break;
      }
      int ci = (int)(Math.random() * 3);
      pixels[i + 2] = (byte)cset[ci][2];
      pixels[i + 1] = (byte)cset[ci][1];
      pixels[i + 0] = (byte)cset[ci][0];
    }
  }

  private ColorFilm(File file) {
    try {
      image = ImageIO.read(file);

      width = image.getWidth();
      height = image.getHeight();

    } catch (Exception e) {
      System.err.println("Couldn't read file");
      return;
    }
    
    pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

    addOutlines();
    generate();

    setMinimumSize(new Dimension(width*4, height*4));
    setPreferredSize(new Dimension(width*4, height*4));
    setMaximumSize(new Dimension(width*4, height*4));

    JFrame frame = new JFrame("Color Film Puzzle");
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(this);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  public void paintComponent(Graphics g) {


    g.drawImage(image, 0, 0, width*4, height*4, null);
  }

  public static void main(String[] args) {
    File file = null;
    try {
      file = new File(args[0]);
    } catch (Exception e) {
      System.err.println("Couldn't open " + args[0]);
    }
    new ColorFilm(file);
  }
}
