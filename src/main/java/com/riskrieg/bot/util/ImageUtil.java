package com.riskrieg.bot.util;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.AttributedString;
import java.util.Map;
import javax.imageio.ImageIO;

public class ImageUtil {

  public static void paintTextWithBounds(Graphics2D g2, String text, Color fillColor, int x1, int y1, int x2, int y2, boolean rightHanded) {
    Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
    if (desktopHints != null) {
      g2.setRenderingHints(desktopHints);
    }

    // Emojis are unsupported in Spectral so have to use fallback method with AttributedString
    float initFontSize = 26.0F;
    Font primaryFont = new Font("Spectral", Font.PLAIN, (int) initFontSize);
    Font fallbackFont = new Font("Noto Serif", Font.PLAIN, (int) initFontSize);
    Font colorEmojiFont = new Font("Noto Color Emoji", Font.PLAIN, (int) initFontSize); // Doesn't work :/

    Rectangle boundingRect = new Rectangle(x1, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));
    float optimalFontSize = FontUtil.getOptimalSize(new String(text.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), boundingRect, g2, 14.0F, 26.0F);

    primaryFont = primaryFont.deriveFont(optimalFontSize);
    fallbackFont = fallbackFont.deriveFont(optimalFontSize);
    colorEmojiFont = colorEmojiFont.deriveFont(optimalFontSize);
    FontMetrics metrics = g2.getFontMetrics(primaryFont); // Use primary font for this to keep it simple.
    int x = boundingRect.x;
    int y = boundingRect.y + ((boundingRect.height - metrics.getHeight()) / 2) + metrics.getAscent();

    AttributedString textToDraw = createFallbackString(new String(text.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), primaryFont, fallbackFont, colorEmojiFont);
//    g2.setPaint(Color.PINK);
//    g2.drawRect(boundingRect.x, boundingRect.y, boundingRect.width, boundingRect.height);
    g2.setPaint(fillColor);
    // No need to set g2.setFont() since we provide it to the AttributedString.
    if (rightHanded) {
      g2.drawString(textToDraw.getIterator(), (x + boundingRect.width) - metrics.stringWidth(text), y);
    } else {
      g2.drawString(textToDraw.getIterator(), x, y);
    }
    g2.dispose();
  }

  private static AttributedString createFallbackString(String text, Font primaryFont, Font fallbackFont, Font colorEmojiFont) {
    AttributedString result = new AttributedString(text);
    result.addAttribute(TextAttribute.FONT, primaryFont, 0, text.length());

    for (int i = 0; i < text.length(); i++) {
      if (!primaryFont.canDisplay(text.charAt(i))) { // Can't display the char
        if (colorEmojiFont.canDisplay(text.charAt(i))) {
          result.addAttribute(TextAttribute.FONT, colorEmojiFont, i, i + 1);
        } else if (fallbackFont.canDisplay(text.charAt(i))) {
          result.addAttribute(TextAttribute.FONT, fallbackFont, i, i + 1);
        } else { // Use OS serif font
          result.addAttribute(TextAttribute.FONT, new Font("Serif", Font.PLAIN, primaryFont.getSize()), i, i + 1);
        }
      }
    }
    return result;
  }

  public static int brightness(Color c) {
    return (int) Math.sqrt(c.getRed() * c.getRed() * 0.241 + c.getGreen() * c.getGreen() * 0.691 + c.getBlue() * c.getBlue() * 0.068);
  }

  public static boolean isColorDark(Color color) {
    if (brightness(color) <= 130) {
      return true;
    }
    return false;
//    double darkness = 1.0D - (0.299D * (double) color.getRed() + 0.587D * (double) color.getGreen() + 0.114D * (double) color.getBlue()) / 255.0D;
//    return !(darkness < 0.5D);
  }

  public static byte[] convertToByteArray(BufferedImage image) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] result = null;

    try {
      ImageIO.write(image, "png", baos);
      baos.flush();
      result = baos.toByteArray();
      baos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  public static BufferedImage convert(BufferedImage image, int imageType) {
    BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), imageType);
    Graphics g = result.getGraphics();
    g.drawImage(image, 0, 0, null);
    return result;
  }

  public static BufferedImage convert(File file, int imageType) throws IOException {
    BufferedImage image = ImageIO.read(file);
    BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), imageType);
    Graphics g = result.getGraphics();
    g.drawImage(image, 0, 0, null);
    return result;
  }

  public static BufferedImage convert(String imagePath, int imageType) throws IOException {
    BufferedImage image = ImageIO.read(new File(imagePath));
    BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), imageType);
    Graphics g = result.getGraphics();
    g.drawImage(image, 0, 0, null);
    return result;
  }

}
