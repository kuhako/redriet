package com.riskrieg.bot.core.commands.general;

import com.aaronjyoder.util.json.gson.GsonUtil;
import com.google.gson.reflect.TypeToken;
import com.riskrieg.bot.Main;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.constant.Colors;
import com.riskrieg.constant.Constants;
import com.riskrieg.map.GameMap;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ListMaps extends Command {

  public ListMaps() {
    this.settings.setAliases("maps", "maplist");
    this.settings.setDescription("Returns a list of currently available maps.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setGuildOnly(true);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {
    Type type = (new TypeToken<HashSet<String>>() {
    }).getType();
    HashSet<String> maps = GsonUtil.read(Constants.AVAILABLE_MAPS, type);

    Set<GameMap> mapSet = new TreeSet<>();
    maps.forEach(mapName -> {
      try {
        mapSet.add(new GameMap(mapName));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(this.settings.getEmbedColor());
    embedBuilder.setTitle(Constants.NAME + " Maps | v" + Constants.VERSION);

    StringBuilder sb = new StringBuilder();
    sb.append("Map names are in bold and the number of map territories appears in parentheses after the map name.").append("\n\n");
    Set<MessageEmbed.Field> fields = getFields(mapSet);
    if (!fields.isEmpty()) {
      fields.forEach(embedBuilder::addField);
    } else {
      embedBuilder.addField("Maps Unavailable", "*There are currently no maps available.*", false);
    }
    embedBuilder.setDescription(sb.toString());
    embedBuilder.setFooter("If you would like to aid development and keep updates steady, use the " + Main.bot.auth().prefix() + "donate command.");
    input.event().getChannel().sendMessage(embedBuilder.build()).queue();
  }

  private Set<MessageEmbed.Field> getFields(Set<GameMap> maps) {
    Set<MessageEmbed.Field> result = new LinkedHashSet<>();
    StringBuilder epicSb = new StringBuilder();
    StringBuilder largeSb = new StringBuilder();
    StringBuilder mediumSb = new StringBuilder();
    StringBuilder smallSb = new StringBuilder();
    StringBuilder comingSoonSb = new StringBuilder();

    maps.forEach(map -> {
      switch (map.status()) {
        case AVAILABLE -> {
          int size = map.getTerritories().size();
          if (size > 0 && size < 65) {
            smallSb.append("**").append(map.displayName()).append("** (").append(map.getTerritories().size()).append(")").append("\n");
          } else if (size >= 65 && size < 125) {
            mediumSb.append("**").append(map.displayName()).append("** (").append(map.getTerritories().size()).append(")").append("\n");
          } else if (size >= 125 && size < 200) {
            largeSb.append("**").append(map.displayName()).append("** (").append(map.getTerritories().size()).append(")").append("\n");
          } else if (size >= 200) {
            epicSb.append("**").append(map.displayName()).append("** (").append(map.getTerritories().size()).append(")").append("\n");
          }
        }
        case COMING_SOON -> comingSoonSb.append("**").append(map.displayName()).append("**").append("\n");
      }
    });

    if (!epicSb.isEmpty()) {
      result.add(new MessageEmbed.Field("Epic", epicSb.toString(), false));
    }
    if (!largeSb.isEmpty()) {
      result.add(new MessageEmbed.Field("Large", largeSb.toString(), true));
    }
    if (!mediumSb.isEmpty()) {
      result.add(new MessageEmbed.Field("Medium", mediumSb.toString(), true));
    }
    if (!smallSb.isEmpty()) {
      result.add(new MessageEmbed.Field("Small", smallSb.toString(), true));
    }
    if (!comingSoonSb.isEmpty()) {
      result.add(new MessageEmbed.Field("Coming Soon", comingSoonSb.toString(), false));
    }
    return result;
  }

}
