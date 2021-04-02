package com.riskrieg.bot.core.commands.moderation;

import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import net.dv8tion.jda.api.EmbedBuilder;

public class Shutdown extends Command {

  public Shutdown() {
    this.settings.setAliases("shutdown");
    this.settings.setDescription("Shuts the bot down safely.");
    this.settings.setEmbedColor(BotConstants.MOD_CMD_COLOR);
    this.settings.setOwnerCommand(true);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setTitle("Shutting Down");
    embedBuilder.setDescription("Safely shutting down.");
    embedBuilder.setColor(this.settings.getEmbedColor());
    input.event().getChannel().sendMessage(embedBuilder.build()).complete();
    System.exit(0);
  }

}
