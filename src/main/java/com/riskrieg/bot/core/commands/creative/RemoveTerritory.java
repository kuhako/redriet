package com.riskrieg.bot.core.commands.creative;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.MessageUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.gamemode.Game;
import com.riskrieg.gamemode.creative.Creative;
import com.riskrieg.player.Player;
import com.riskrieg.player.PlayerColor;
import java.util.Optional;

public class RemoveTerritory extends Command {

  public RemoveTerritory() {
    this.settings.setAliases("revoke");
    this.settings.setDescription("Removes the specified territories from any player.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setGuildOnly(true);
  }

  protected void execute(MessageInput input) {
    Riskrieg api = new Riskrieg();
    Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());
    if (optGame.isPresent()) {
      Game game = optGame.get();
      if (game instanceof Creative) {
        Optional<Player> optDungeonMaster = game.getPlayer(input.event().getMember().getId());
        if (optDungeonMaster.isPresent() && optDungeonMaster.get().equals(game.getPlayers().getFirst())) {
          Optional<PlayerColor> chosenColor = MessageUtil.parseColor(input.arg(0).trim());
          if (chosenColor.isPresent()) {
            // TODO: Do this
          } else {
            input.event().getChannel().sendMessage(Error.create("You must supply a valid color.", this.settings)).queue();
          }
        } else {
          input.event().getChannel().sendMessage(Error.create("You need to be the Dungeon Master to use this command.", this.settings)).queue();
        }
      } else {
        input.event().getChannel().sendMessage(Error.create("Invalid game mode.", this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
    }
  }

}
