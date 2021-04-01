package com.riskrieg.bot.core.commands.moderation;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.gamemode.Game;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import net.dv8tion.jda.api.EmbedBuilder;

public class PruneSaves extends Command {

  private static int NOT_UPDATED_CUTOFF_DAYS = 30;

  public PruneSaves() {
    this.settings.setAliases("prune", "prunesaves", "prune-saves");
    this.settings.setDescription("Deletes any saves where the game has ended.");
    this.settings.setEmbedColor(BotConstants.MOD_CMD_COLOR);
    this.settings.setOwnerCommand(true);
  }

  @Override
  protected void execute(MessageInput input) {
    Riskrieg api = new Riskrieg();

    Set<Game> allSaves = api.loadAllSaves();

    Set<Game> ended = new HashSet<>();
    Set<Game> notUpdated = new HashSet<>();
    for (Game game : allSaves) {
      if (game.isEnded()) {
        ended.add(game);
      } else if (Duration.between(game.getCreationTime(), game.getLastUpdated()).toDays() > NOT_UPDATED_CUTOFF_DAYS) {
        notUpdated.add(game);
      }
    }

    EmbedBuilder embedBuilder = settings.embedBuilder();
    embedBuilder.setTitle("Saves To Be Deleted");
    if (ended.size() > 0 || notUpdated.size() > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append("Ended games: ").append(ended.size()).append("\n");
      sb.append("Not updated in more than ").append(NOT_UPDATED_CUTOFF_DAYS).append(" days: ").append(notUpdated.size()).append("\n");
      embedBuilder.setDescription(sb.toString());
      if (input.args().length > 0 && input.arg(0).equals("-d")) {
        for (Game game : ended) {
          api.delete(game.getId());
        }
        embedBuilder.setFooter("All ended games have been deleted.");
        embedBuilder.setTimestamp(Instant.now());
      }
    } else {
      embedBuilder.setDescription("There are no ended games to delete, and no games have not been updated in over " + NOT_UPDATED_CUTOFF_DAYS + " days.");
    }

    input.event().getChannel().sendMessage(embedBuilder.build()).queue();
  }

}
