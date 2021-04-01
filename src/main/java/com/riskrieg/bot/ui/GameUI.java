package com.riskrieg.bot.ui;

import com.riskrieg.bot.util.ImageUtil;
import com.riskrieg.constant.Constants;
import com.riskrieg.map.GameMap;
import com.riskrieg.map.alignment.HorizontalAlignment;
import com.riskrieg.player.Player;
import com.riskrieg.player.PlayerColor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Deque;
import javax.imageio.ImageIO;

public class GameUI {

  public static void drawTerritoryNames(GameMap map, BufferedImage mapImage) throws IOException {
    final BufferedImage imageTerritoryNames = ImageIO.read(new File(map.getTextLayerPath()));
    Graphics g = mapImage.getGraphics();
    g.drawImage(imageTerritoryNames, 0, 0, null);
    g.dispose();
  }

  public static void drawPlayerUI(Deque<Player> players, GameMap map, BufferedImage mapImage) throws IOException {
    final int marginSide = 10;
    final int marginTopBottom = 10;
    final int marginBetween = 2;
    int borderThickness = 3;
    int marginNameSide = 4;

    final BufferedImage imageColorList = ImageIO.read(new File(Constants.COLOR_CHOICES_VERTICAL));
    final BufferedImage imagePlayerNameRegion = ImageIO.read(new File(Constants.PLAYER_NAME_BACKGROUND));

    Graphics g = mapImage.getGraphics();
    Point pointColorList = new Point(0, 0);
    Point pointPlayerNameRegion = new Point(0, 0);

    Point pointPlayerRectTopLeft = new Point(0, 0);
    Point pointPlayerRectBottomRight = new Point(0, 0);
    switch (map.alignment().horizontal()) {
      case LEFT -> {
        pointColorList.setLocation(marginSide, pointColorList.y);
        pointPlayerNameRegion.setLocation(pointColorList.x + imageColorList.getWidth() + marginBetween, pointPlayerNameRegion.y);
      }
      case RIGHT -> {
        pointColorList.setLocation(mapImage.getWidth() - imageColorList.getWidth() - marginSide, pointColorList.y);
        pointPlayerNameRegion.setLocation(pointColorList.x - marginBetween - imagePlayerNameRegion.getWidth(), pointPlayerNameRegion.y);
      }
    }

    pointPlayerRectTopLeft.setLocation(pointPlayerNameRegion.x + borderThickness + marginNameSide, pointPlayerRectTopLeft.y);
    pointPlayerRectBottomRight.setLocation(pointPlayerRectTopLeft.x + (imagePlayerNameRegion.getWidth() - marginNameSide * 2 - borderThickness * 2 - 1),
        pointPlayerRectBottomRight.y); // Have to subtract 1 because one edge is exclusive

    switch (map.alignment().vertical()) {
      case TOP -> {
        pointColorList.setLocation(pointColorList.x, marginTopBottom);
        pointPlayerNameRegion.setLocation(pointPlayerNameRegion.x, marginTopBottom);
      }
      case MIDDLE -> {
        pointColorList.setLocation(pointColorList.x, mapImage.getHeight() / 2 - imageColorList.getHeight() / 2);
        pointPlayerNameRegion.setLocation(pointPlayerNameRegion.x, mapImage.getHeight() / 2 - imagePlayerNameRegion.getHeight() / 2);
      }
      case BOTTOM -> {
        pointColorList.setLocation(pointColorList.x, mapImage.getHeight() - imageColorList.getHeight() - marginTopBottom);
        pointPlayerNameRegion.setLocation(pointPlayerNameRegion.x, mapImage.getHeight() - imagePlayerNameRegion.getHeight() - marginTopBottom);
      }
    }

    int colorCellHeight = (imageColorList.getHeight() - (PlayerColor.values().length + 1) * borderThickness) / PlayerColor.values().length;
    pointPlayerRectTopLeft.setLocation(pointPlayerRectTopLeft.x, pointPlayerNameRegion.y + borderThickness);
    pointPlayerRectBottomRight.setLocation(pointPlayerRectBottomRight.x, pointPlayerRectTopLeft.y + colorCellHeight - 1); // Subtract 1 because one edge is exclusive

    g.drawImage(imageColorList, pointColorList.x, pointColorList.y, null);
    g.drawImage(imagePlayerNameRegion, pointPlayerNameRegion.x, pointPlayerNameRegion.y, null);
    g.dispose();

    int nameRectHeight = pointPlayerRectBottomRight.y - pointPlayerRectTopLeft.y;

    for (Player player : players) {
      Graphics pGraphics = mapImage.getGraphics();
      int currentTopLeftY = pointPlayerRectTopLeft.y + ((colorCellHeight + borderThickness) * player.getColor().ordinal());
      ImageUtil.paintTextWithBounds((Graphics2D) pGraphics, player.getName(), player.getColor().value(), pointPlayerRectTopLeft.x, currentTopLeftY,
          pointPlayerRectBottomRight.x, currentTopLeftY + (nameRectHeight), map.alignment().horizontal().equals(HorizontalAlignment.RIGHT));
      pGraphics.dispose();
    }

    g.dispose();
  }

  public static void drawTitle(String title, BufferedImage mapImage) throws IOException {

  }

}
