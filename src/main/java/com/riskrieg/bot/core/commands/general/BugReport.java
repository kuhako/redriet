package com.riskrieg.bot.core.commands.general;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.constant.Constants;
import com.riskrieg.gamemode.Game;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;

public class BugReport extends Command {

  public BugReport() {
    this.settings.setAliases("bugreport", "br");
    this.settings.setDescription("Generates information for a bug report.");
    this.settings.setEmbedColor(BotConstants.GENERIC_CMD_COLOR);
    this.settings.setGuildOnly(true);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  @Override
  protected void execute(MessageInput input) {
    Riskrieg api = new Riskrieg();
    Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());
    if (optGame.isPresent()) {
      Game game = optGame.get();
      EmbedBuilder embedBuilder = settings.embedBuilder();
      embedBuilder.setTitle("Bug Report Information");
      StringBuilder sb = new StringBuilder();
      sb.append("**Bot version:** ").append(BotConstants.VERSION).append("\n");
      sb.append("**Game version:** ").append(Constants.VERSION).append("\n");
      sb.append("**Game ID:** ").append(game.getId()).append("\n");
      sb.append("**Guild ID:** ").append(input.event().getGuild().getId()).append("\n");
      sb.append("**Channel ID:** ").append(input.event().getChannel().getId()).append("\n");
      sb.append("**Game Mode:** ").append(game.getName()).append("\n");
      if (game.getMap().isPresent()) {
        sb.append("**Map:** ").append(game.getMap().get().displayName()).append("\n");
      }
      sb.append("**Players:** ").append(game.getPlayers().size()).append("\n");
      sb.append("**Nations:** ").append(game.getNations().size()).append("\n");
      sb.append("**Created on:** ").append(LocalDateTime.ofInstant(game.getCreationTime(), ZoneOffset.UTC)).append("\n");
      sb.append("**Last updated:** ").append(LocalDateTime.ofInstant(game.getLastUpdated(), ZoneOffset.UTC)).append("\n");
      sb.append("\n").append("[Official " + Constants.NAME + " Server Invite](").append(BotConstants.serverInviteStr).append(")").append("\n");
      embedBuilder.setDescription(sb.toString());
      embedBuilder.setTimestamp(Instant.now());
      input.event().getChannel().sendMessage(embedBuilder.build()).queue();
    } else {
      input.event().getChannel().sendMessage(Error.create("There is no game in this channel to get bug report information on.", this.settings)).queue();
    }
  }

}
