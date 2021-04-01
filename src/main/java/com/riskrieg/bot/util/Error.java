package com.riskrieg.bot.util;

import com.riskrieg.bot.core.CommandSettings;
import com.riskrieg.constant.Colors;
import com.riskrieg.constant.Constants;
import com.riskrieg.response.Response;
import java.time.Instant;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Error {

  public static MessageEmbed create(Response response, CommandSettings settings) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(Colors.ERROR_COLOR);
    embedBuilder.setTitle("Error: " + settings.getName());
    embedBuilder.setDescription(response.getMessage().orElse("Unknown error."));
    embedBuilder.setFooter("Version: " + Constants.VERSION);
    embedBuilder.setTimestamp(Instant.now());
    return embedBuilder.build();
  }

  public static MessageEmbed create(Response response, String orElse, CommandSettings settings) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(Colors.ERROR_COLOR);
    embedBuilder.setTitle("Error: " + settings.getName());
    embedBuilder.setDescription(response.getMessage().orElse(orElse));
    embedBuilder.setFooter("Version: " + Constants.VERSION);
    embedBuilder.setTimestamp(Instant.now());
    return embedBuilder.build();
  }

  public static MessageEmbed create(String desc, CommandSettings settings) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(Colors.ERROR_COLOR);
    embedBuilder.setTitle("Error: " + settings.getName());
    embedBuilder.setDescription(desc);
    embedBuilder.setFooter("Version: " + Constants.VERSION);
    embedBuilder.setTimestamp(Instant.now());
    return embedBuilder.build();
  }

  public static MessageEmbed create(String desc, String errorStr) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(Colors.ERROR_COLOR);
    embedBuilder.setTitle("Error: " + errorStr);
    embedBuilder.setDescription(desc);
    embedBuilder.setFooter("Version: " + Constants.VERSION);
    embedBuilder.setTimestamp(Instant.now());
    return embedBuilder.build();
  }

}
