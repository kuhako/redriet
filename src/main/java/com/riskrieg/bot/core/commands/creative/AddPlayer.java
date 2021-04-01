package com.riskrieg.bot.core.commands.creative;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.MessageUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.gamemode.Game;
import com.riskrieg.gamemode.creative.Creative;
import com.riskrieg.player.ComputerPlayer;
import com.riskrieg.player.Player;
import com.riskrieg.player.PlayerColor;
import com.riskrieg.response.Response;
import java.util.Arrays;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;

public class AddPlayer extends Command {

  public AddPlayer() {
    this.settings.setAliases("add", "addp", "addplayer", "add-player");
    this.settings.setDescription("Adds a player to the game.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setGuildOnly(true);
  }

  protected void execute(MessageInput input) {
    if (input.args().length >= 2) {
      Riskrieg api = new Riskrieg();
      Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());
      if (optGame.isPresent()) {
        Game game = optGame.get();
        if (game instanceof Creative) {
          if (game.getPlayers().size() > 0) {
            Optional<Player> optDungeonMaster = game.getPlayer(input.event().getMember().getId());
            if (optDungeonMaster.isPresent() && optDungeonMaster.get().equals(game.getPlayers().getFirst())) {
              Optional<PlayerColor> chosenColor = MessageUtil.parseColor(input.arg(0).trim());
              if (chosenColor.isPresent()) {
                Player player = new ComputerPlayer(String.join(" ", Arrays.copyOfRange(input.args(), 1, input.args().length)).trim(), chosenColor.get());
                Response response = game.join(player);
                if (response.success()) {
                  EmbedBuilder embedBuilder = new EmbedBuilder();
                  embedBuilder.setColor(player.getColor().value());
                  embedBuilder.setTitle("Player Added");
                  embedBuilder.setDescription("**" + player.getName() + "** has been added to the game as **" + player.getColor().getName() + "**.");
                  input.event().getChannel().sendMessage(embedBuilder.build()).queue();
                  api.save(game, input.event().getGuild().getId(), input.event().getChannel().getId());
                } else {
                  input.event().getChannel().sendMessage(Error.create(response, this.settings)).queue();
                }
              } else {
                input.event().getChannel().sendMessage(Error.create("You must choose a valid color.", this.settings)).queue();
              }
            } else {
              input.event().getChannel().sendMessage(Error.create("You need to be the Dungeon Master to use this command.", this.settings)).queue();
            }
          } else {
            input.event().getChannel().sendMessage(Error.create("Players cannot be added until one person joins the game.", this.settings)).queue();
          }
        } else {
          input.event().getChannel().sendMessage(Error.create("Invalid game mode.", this.settings)).queue();
        }
      } else {
        input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("Invalid syntax. | Usage: `" + this.settings.getName() + " [color] [name]`", this.settings)).queue();
    }
  }

}
