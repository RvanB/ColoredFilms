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

  private void generate() {
    System.out.println("painted");
    for (int i = 0; i < width * height * 3; i+=3) {

      int[] options = {0, 1, 2};

      if (pixels[i + 0] == 0) { // No blue
        options[0] = 1;
      }
      if (pixels[i + 1] == 0) { // No green
        if (options[0] == 0) {
          options[1] = 0;
        } else {
          options[0] = 2;
          options[1] = 2;
        }
      }
      if (pixels[i + 2] == 0) { // No red
        if (options[0] == 0) {
          options[2] = 0;
        } else { // options[0] = 1 or 2
          if (options[1] == 1) {
            options[2] = 1;
          } else { // 
            options[2] = 2;
          }
        }
      }

      int chosen = options[(int)(Math.random() * 3)];
      int[][] b = null;

      int ci = (int)(Math.random() * 3);
      switch(chosen) {
        case 0:
          pixels[i + 2] = (byte)bb[ci][2];
          pixels[i + 1] = (byte)bb[ci][1];
          pixels[i + 0] = (byte)bb[ci][0];
          break;
        case 1:
          pixels[i + 2] = (byte)bg[ci][2];
          pixels[i + 1] = (byte)bg[ci][1];
          pixels[i + 0] = (byte)bg[ci][0];
          break;
        default:
          pixels[i + 2] = (byte)br[ci][2];
          pixels[i + 1] = (byte)br[ci][1];
          pixels[i + 0] = (byte)br[ci][0];
          break;
      }
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
