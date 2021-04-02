package com.riskrieg.bot.core.commands.stateless;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.constant.Colors;
import com.riskrieg.gamemode.Game;
import com.riskrieg.player.Player;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;

public class Players extends Command {

  public Players() {
    this.settings.setAliases("players");
    this.settings.setDescription("Shows all of the players in the game.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setGuildOnly(true);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {
    Riskrieg api = new Riskrieg();
    Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());
    if (optGame.isPresent()) {
      Game game = optGame.get();
      game.getPlayers();
      StringBuilder sb = new StringBuilder();
      if (game.getPlayers().size() == 0) {
        sb.append("*There are no players in the game.*");
      } else {
        for (Player player : game.getPlayers()) {
          sb.append(player.getName()).append(" -- ").append(player.getColor().getName()).append("\n");
        }
      }
      EmbedBuilder embedBuilder = settings.embedBuilder();
      embedBuilder.setTitle("Players");
      embedBuilder.setDescription(sb.toString());
      input.event().getChannel().sendMessage(embedBuilder.build()).queue();
    } else {
      input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
    }
  }

}
