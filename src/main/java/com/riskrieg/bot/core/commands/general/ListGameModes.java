package com.riskrieg.bot.core.commands.general;

import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.constant.Colors;
import com.riskrieg.constant.Constants;
import com.riskrieg.gamemode.GameMode;
import net.dv8tion.jda.api.EmbedBuilder;

public class ListGameModes extends Command {

  public ListGameModes() {
    this.settings.setAliases("gamemodes");
    this.settings.setDescription("Lists available game modes.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(this.settings.getEmbedColor());
    embedBuilder.setTitle(Constants.NAME + " Game Modes");
    StringBuilder desc = new StringBuilder();
    for (GameMode gameMode : GameMode.values()) {
      desc.append("**").append(gameMode.getName()).append("** | *").append(gameMode.getDescription()).append("*").append("\n");
    }
    embedBuilder.setDescription(desc.toString());
    embedBuilder.setFooter(Constants.NAME + " " + Constants.VERSION);
    input.event().getChannel().sendMessage(embedBuilder.build()).queue();
  }

}
