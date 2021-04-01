package com.riskrieg.bot.core.commands.general;

import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import net.dv8tion.jda.api.EmbedBuilder;

public class Ping extends Command {

  public Ping() {
    this.settings.setAliases("ping", "pong");
    this.settings.setDescription("Tests to see if the bot is online and functional.");
    this.settings.setEmbedColor(BotConstants.GENERIC_CMD_COLOR);
  }

  protected void execute(MessageInput input) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setTitle("Ping");
    embedBuilder.setDescription("Pong! :table_tennis: | Response in `" + input.event().getJDA().getRestPing().complete() + "ms`");
    embedBuilder.setColor(this.settings.getEmbedColor());
    input.event().getChannel().sendMessage(embedBuilder.build()).queue();
  }

}
