package com.riskrieg.bot.core.commands.stateless;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.constant.Colors;
import com.riskrieg.response.Response;
import java.time.Instant;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class End extends Command {

  public End() {
    this.settings.setAliases("end");
    this.settings.setDescription("Ends a game.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setGuildOnly(true);
    this.settings.setAuthorPerms(Permission.MANAGE_CHANNEL);
  }

  protected void execute(MessageInput input) {
    Riskrieg api = new Riskrieg();
    Response response = api.delete(input.event().getGuild().getId(), input.event().getChannel().getId());
    if (response.success()) {
      EmbedBuilder embedBuilder = settings.embedBuilder();
      embedBuilder.setTitle("Game Ended");
      embedBuilder.setDescription("The game has been ended, and the save file has been deleted.");
      embedBuilder.setFooter("If you have any issues, please report them in the official Discord server.");
      embedBuilder.setTimestamp(Instant.now());
      input.event().getChannel().sendMessage(embedBuilder.build()).queue();
    } else {
      input.event().getChannel().sendMessage(Error.create(response, this.settings)).queue();
    }
  }

}
