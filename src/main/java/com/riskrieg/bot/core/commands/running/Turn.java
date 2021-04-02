package com.riskrieg.bot.core.commands.running;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.PreferenceUtil;
import com.riskrieg.bot.util.RiskriegUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.gamemode.Game;
import com.riskrieg.player.Player;
import com.riskrieg.response.Response;
import java.util.Optional;

public class Turn extends Command {

  public Turn() {
    this.settings.setAliases("turn");
    this.settings.setDescription("Checks whose turn it is.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setGuildOnly(true);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {
    // TODO: Incorporate response.getMessage() probably at some point instead of using static utility methods
    Riskrieg api = new Riskrieg();
    Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());
    if (optGame.isPresent()) {
      Game game = optGame.get();
      Response response = game.turn();
      if (response.success()) {
        RiskriegUtil.sendConquestTurn(game, input.event().getChannel(), settings);
//        Player current = game.getPlayers().getFirst();
//        if (!current.isComputer()) {
//          PreferenceUtil.sendPingIfEnabled(input.event().getGuild(), current, input.event().getChannel());
//        }
      } else {
        input.event().getChannel().sendMessage(Error.create(response, this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
    }
  }

}
