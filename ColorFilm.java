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

  public BufferedImage getImage() {
    return image;
  }

  private void addOutlines() {
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int color = image.getRGB(x, y);

        if (color != 0xFFFFFFFF) {
          int red = (color >> 16) & 0xff;
          int green = (color >> 8) & 0xff;
          int blue = color & 0xff;
          int newColor = (0xff << 24) + ((255 - red) << 16) + ((255 - green) << 8) + (255 - blue);
          if (y > 0 && image.getRGB(x, y-1) != color)
            image.setRGB(x, y-1, newColor);
          if (x > 0 && image.getRGB(x-1, y) != color)
            image.setRGB(x-1, y, newColor);
        }
      }
    }
    for (int y = height - 1; y > 0; y--) {
      for (int x = width - 1; x > 0; x--) {
        int color = image.getRGB(x, y);

        if (color != 0xFFFFFFFF) {
          int red = (color >> 16) & 0xff;
          int green = (color >> 8) & 0xff;
          int blue = color & 0xff;
          int newColor = (0xff << 24) + ((255 - red) << 16) + ((255 - green) << 8) + (255 - blue);
          if (y < height - 1 && image.getRGB(x, y+1) != color)
            image.setRGB(x, y+1, newColor);
          if (x < width - 1 && image.getRGB(x+1, y) != color)
            image.setRGB(x+1, y, newColor);
        }
      }
    }
  }

  private void generate() {
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int[][] cset = null;

        int color = image.getRGB(x, y);

        int red = (color >> 16) & 0xff;
        int green = (color >> 8) & 0xff;
        int blue = color & 0xff;

        double total = red + green + blue;

        double cr = (double)red / total;
        double cg = cr + (double)green / total;
        double cb = cg + (double)blue / total;

        double choice = Math.random();
        float hue;
        if (choice < cr)
          hue = (float)(Math.random() * 100 - 60);
        else if (choice < cg)
          hue = (float)(Math.random() * 100 + 80);
        else
          hue = (float)(Math.random() * 100 + 200);

        hue /= 360.0f;

        color = Color.HSBtoRGB(hue, 1.0f, 1.0f);
        //switch(color) {
        //  case 0xFFFF0000:
        //    cset = br;
        //    break;
        //  case 0xFF00FFFF:
        //    cset = dr;
        //    break;
        //  case 0xFF00FF00:
        //    cset = bg;
        //    break;
        //  case 0xFFFF00FF:
        //    cset = dg;
        //    break;
        //  case 0xFF0000FF:
        //    cset = bb;
        //    break;
        //  default:
        //    int c = (int)(Math.random() * 6);
        //    int[][][] csets = {bb, bg, br, db, dg, dr};
        //    cset = csets[c];
        //    break;
        //}
        int i = (y * width + x) * 3;
        pixels[i + 2] = (byte)((color >> 16) & 0xff);
        pixels[i + 1] = (byte)((color >> 8) & 0xff);
        pixels[i + 0] = (byte)(color & 0xff);
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
    ColorFilm cf = new ColorFilm(file);

    try {
      if (args.length == 2) {
        File outputFile = new File(args[1]);
        ImageIO.write(cf.getImage(), "png", outputFile);
        System.out.println("Output saved in '" + args[1] + "'");
      }
    } catch (Exception e) {
      System.err.println("Couldn't write output to '" + args[1] + "'");
    }
  }
}
