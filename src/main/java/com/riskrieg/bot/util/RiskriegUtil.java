package com.riskrieg.bot.util;

import com.riskrieg.bot.core.CommandSettings;
import com.riskrieg.bot.fill.BasicFill;
import com.riskrieg.bot.fill.Fill;
import com.riskrieg.bot.fill.MilazzoFill;
import com.riskrieg.bot.ui.GameUI;
import com.riskrieg.constant.Colors;
import com.riskrieg.gamemode.Game;
import com.riskrieg.map.GameMap;
import com.riskrieg.map.graph.Territory;
import com.riskrieg.nation.ConquestNation;
import com.riskrieg.nation.Nation;
import com.riskrieg.player.Player;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.utils.AttachmentOption;

public class RiskriegUtil {

  public static void sendConquestTurn(Game game, MessageChannel channel, CommandSettings settings, String description) { // TODO: SPEED THIS UP
    Player current = game.getPlayers().getFirst();
    Optional<Nation> optCurrentNation = game.getNation(current.getID());
    if (optCurrentNation.isPresent()) {
      if (game.isEnded()) {
        RiskriegUtil.sendMap(game, channel, settings.getEmbedColor(),
            game.getMap().get().displayName(), // TODO: Have a better alternative to this for getting the map name.
            description, "The game is now over.");
      } else {
        int claims = ((ConquestNation) optCurrentNation.get()).getClaimAmount(game);
        String territoryStr = claims == 1 ? "territory" : "territories";
        String footer = game.getPlayers().size() <= 1 ? "The game has ended." : "It is " + current.getName() + "'s turn. They may claim " + claims
            + " " + territoryStr + " this turn.";
        RiskriegUtil.sendMap(game, channel, settings.getEmbedColor(),
            game.getMap().get().displayName(), // TODO: Have a better alternative to this for getting the map name.
            description, footer);
      }
    } else {
      channel.sendMessage(Error.create("Error starting game.", settings)).queue();
    }
  }

  public static void sendConquestTurn(Game game, MessageChannel channel, CommandSettings settings) {
    sendConquestTurn(game, channel, settings, null);
  }

  public static void sendMap(Game game, MessageChannel channel, Color embedColor, String author, String title, String description, String footer) {
    String fileName = game.getMap().get().name(); // TODO: Maybe change file name to something different.
    MessageBuilder messageBuilder = new MessageBuilder();
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(embedColor);
    embedBuilder.setTitle(title);
    if (author != null) {
      embedBuilder.setAuthor(author);
    }
    if (description != null) {
      embedBuilder.setDescription(description);
    }
    if (footer != null) {
      embedBuilder.setFooter(footer);
    }
    embedBuilder.setImage("attachment://" + fileName + ".png");
    messageBuilder.setEmbed(embedBuilder.build());
    channel.sendMessage(messageBuilder.build()).addFile(ImageUtil.convertToByteArray(constructMap(game)), fileName + ".png", new AttachmentOption[0]).queue();
  }

  public static void sendMap(Game game, MessageChannel channel, Color embedColor, String title, String description, String footer) {
    sendMap(game, channel, embedColor, null, title, description, footer);
  }

  public static void sendMap(Game game, MessageChannel channel, Color embedColor, String title, String footer) {
    sendMap(game, channel, embedColor, title, null, footer);
  }

  private static BufferedImage constructMap(Game game) {
    GameMap map = game.getMap().get();

    File mapFile = new File(map.getBaseLayerPath());

    try {
      BufferedImage mapImage = ImageUtil.convert(mapFile, BufferedImage.TYPE_INT_ARGB);

      for (Nation nation : game.getNations()) { // TODO: Replace this
        for (Territory territory : nation.getTerritories()) {
          for (Point point : territory.seedPoints()) {
            if (territory.equals(nation.getCapital())) {
              mapImage = colorCapitalTerritory(mapImage, point, game.getPlayer(nation.getLeaderIdentifier().id()).get().getColor().value());
            } else {
              mapImage = colorTerritory(mapImage, point, game.getPlayer(nation.getLeaderIdentifier().id()).get().getColor().value());
            }
          }
        }
      }

      GameUI.drawTerritoryNames(map, mapImage);
      GameUI.drawPlayerUI(game.getPlayers(), map, mapImage);

      Point mapTitleLocation = new Point(5, 5); // TODO: Replace this
      String mapTitle = map.displayName();
      if (map.name().equals("san-francisco")) {
        mapTitleLocation = new Point(5, 380);
      }
      mapImage = addMapTitle(mapImage, mapTitle.toUpperCase(), mapTitleLocation);

      return mapImage;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static BufferedImage addMapTitle(BufferedImage image, String title, Point pos) {
    Graphics2D g = (Graphics2D) image.getGraphics();
    Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
    if (desktopHints != null) {
      g.setRenderingHints(desktopHints);
    }
    float initFontSize = 21.0F;
    Font primaryFont = new Font("Spectral", Font.PLAIN, (int) initFontSize);

    g.setFont(primaryFont);
    FontMetrics metrics = g.getFontMetrics(g.getFont());

    g.setPaint(Colors.TEXT_COLOR);
    g.drawString("\u2014" + " " + title + " " + "\u2014", pos.x, pos.y + metrics.getAscent()); // —
    g.dispose();
    return image;
  }

  private static BufferedImage colorTerritory(BufferedImage image, Point point, Color newColor) {
    Fill bucket = new MilazzoFill(image, new Color(image.getRGB(point.x, point.y)), newColor);
    bucket.fill(point);
    return bucket.getImage();
  }

  private static BufferedImage colorCapitalTerritory(BufferedImage image, Point point, Color newColor) {
    BasicFill bucket = new BasicFill(image, new Color(image.getRGB(point.x, point.y)), newColor);
    bucket.fillPattern(point);
    return bucket.getImage();
  }

}
