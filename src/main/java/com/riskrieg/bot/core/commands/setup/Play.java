package com.riskrieg.bot.core.commands.setup;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.PreferenceUtil;
import com.riskrieg.bot.util.RiskriegUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.constant.Constants;
import com.riskrieg.gamemode.Game;
import com.riskrieg.player.Player;
import com.riskrieg.response.Response;
import java.util.Optional;

public class Play extends Command {

  public Play() {
    this.settings.setAliases("play", "begin", "start");
    this.settings.setDescription("Starts the game.");
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
      Response response = game.start();
      if (response.success()) {
        RiskriegUtil.sendConquestTurn(game, input.event().getChannel(), settings, "A game of " + Constants.NAME + " has started!");
        Player current = game.getPlayers().getFirst();
        if (current.isComputer()) {
          // TODO: Handle case where player is computer.
        } else {
          PreferenceUtil.sendPingIfEnabled(input.event().getGuild(), current, input.event().getChannel());
        }
        api.save(game, input.event().getGuild().getId(), input.event().getChannel().getId());
      } else {
        input.event().getChannel().sendMessage(Error.create(response, this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
    }
  }

}
