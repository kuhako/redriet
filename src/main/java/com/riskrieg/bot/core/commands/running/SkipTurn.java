package com.riskrieg.bot.core.commands.running;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.RiskriegUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.gamemode.Game;
import com.riskrieg.player.Player;
import com.riskrieg.response.Response;
import java.util.Optional;

public class SkipTurn extends Command {

  public SkipTurn() {
    this.settings.setAliases("skip", "skip-turn", "turn-skip", "skipturn", "turnskip");
    this.settings.setDescription("Skips your turn.");
    this.settings.setGuildOnly(true);
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
  }

  protected void execute(MessageInput input) {
    Riskrieg api = new Riskrieg();
    Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());
    if (optGame.isPresent()) {
      Game game = optGame.get();

      /* // TODO: Figure out permission structure, then add this for color selection.
      Optional<Player> toSkip = Optional.empty();
      Optional<PlayerColor> optColor = MessageUtil.parseColor(input.getArgString());
      if (optColor.isPresent()) {
        toSkip = game.getPlayer(optColor.get());
      } else if (input.getEvent().getMessage().getMentionedMembers().size() == 1) {
        toSkip = game.getPlayer(new PlayerID(input.getEvent().getMessage().getMentionedMembers().get(0).getId()));
      }
       */

      Optional<Player> toSkip = game.getPlayer(input.event().getMember().getId());
      if (toSkip.isPresent()) {
        Response response = game.skip(toSkip.get());
        if (response.success()) {
          if (response.getMessage().isPresent()) {
            RiskriegUtil.sendConquestTurn(game, input.event().getChannel(), settings, response.getMessage().get());
          } else {
            RiskriegUtil.sendConquestTurn(game, input.event().getChannel(), settings);
          }
          api.save(game, input.event().getGuild().getId(), input.event().getChannel().getId());
        } else {
          input.event().getChannel().sendMessage(Error.create(response, this.settings)).queue();
        }
      } else {
        input.event().getChannel().sendMessage(Error.create("Player is not in the game.", this.settings)).queue();
      }

    } else {
      input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
    }
  }

}
