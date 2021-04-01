package com.riskrieg.bot.fill;

import com.riskrieg.bot.util.ImageUtil;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import javax.imageio.ImageIO;

public class BasicFill implements Fill {

  private BufferedImage image;
  private final Color original;
  private final Color fill;
  private final int imgWidth;
  private final int imgHeight;

  public BasicFill(BufferedImage image, Color original, Color fill) {
    this.image = image;
    this.original = original;
    this.fill = fill;
    this.imgWidth = image.getWidth();
    this.imgHeight = image.getHeight();
  }

  public BufferedImage getImage() {
    return image;
  }

  public void fill(Point seed) {
    boolean[][] visited = new boolean[imgHeight][imgWidth];
    Queue<Point> queue = new LinkedList<>();
    queue.add(new Point(seed.x, seed.y));

    while (!queue.isEmpty()) {
      Point p = queue.remove();
      if (scan(visited, p.x, p.y)) {
        setPixel(p.x, p.y);
        queue.add(new Point(p.x - 1, p.y));
        queue.add(new Point(p.x + 1, p.y));
        queue.add(new Point(p.x, p.y - 1));
        queue.add(new Point(p.x, p.y + 1));
      }
    }
  }

  public void fillPattern(Point seed) {
    try {
      boolean[][] visited = new boolean[imgHeight][imgWidth];
      Queue<Point> queue = new LinkedList<>();
      queue.add(new Point(seed.x, seed.y));

      Color maskColor = new Color(128, 128, 128);
      BufferedImage mask = ImageIO.read(new File("res/images/capital-mask.png"));
      mask = mask.getSubimage(0, 0, this.image.getWidth(), this.image.getHeight());

      Color fillColor = new Color(this.fill.getRGB());
      if (ImageUtil.isColorDark(fillColor)) {
        fillColor = manipulateColor(fillColor, 1.5F);
      } else {
        fillColor = manipulateColor(fillColor, 0.75F);
      }

      while (!queue.isEmpty()) {
        Point p = queue.remove();
        if (scan(visited, p.x, p.y)) {
          if (mask.getRGB(p.x, p.y) == maskColor.getRGB()) {
            setPixel(p.x, p.y, fillColor);
          } else {
            setPixel(p.x, p.y);
          }
          queue.add(new Point(p.x - 1, p.y));
          queue.add(new Point(p.x + 1, p.y));
          queue.add(new Point(p.x, p.y - 1));
          queue.add(new Point(p.x, p.y + 1));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /* Private Methods */

  private boolean scan(boolean[][] visited, int x, int y) {
    if (x >= 0 && y >= 0 && x < imgWidth && y < imgHeight && image.getRGB(x, y) == original.getRGB() && !visited[y][x]) {
      visited[y][x] = true;
      return true;
    } else {
      return false;
    }
  }

  private static Color manipulateColor(Color color, float factor) {
    int a = color.getAlpha();
    int r = Math.round((float) color.getRed() * factor);
    int g = Math.round((float) color.getGreen() * factor);
    int b = Math.round((float) color.getBlue() * factor);
    return new Color(Math.min(r, 255), Math.min(g, 255), Math.min(b, 255), a);
  }

  private Color getColor(int x, int y) {
    return new Color(image.getRGB(x, y));
  }

  private int getPixel(int x, int y) {
    return image.getRGB(x, y);
  }

  private void setPixel(int x, int y) {
    image.setRGB(x, y, fill.getRGB());
  }

  private void setPixel(int x, int y, Color color) {
    image.setRGB(x, y, color.getRGB());
  }

}
