package com.riskrieg.bot.core.commands.creative;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.MessageUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.constant.Constants;
import com.riskrieg.gamemode.Game;
import com.riskrieg.gamemode.creative.Creative;
import com.riskrieg.player.Player;
import com.riskrieg.player.PlayerColor;
import com.riskrieg.response.Response;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;

public class RemovePlayer extends Command {

  public RemovePlayer() {
    this.settings.setAliases("remove");
    this.settings.setDescription("Removes a user from the game forcibly.");
    this.settings.setGuildOnly(true);
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {
    Riskrieg api = new Riskrieg();
    Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());
    if (optGame.isPresent()) {
      Game game = optGame.get();
      if (game instanceof Creative) {
        if (game.getPlayers().size() > 0) {
          Optional<Player> optDungeonMaster = game.getPlayer(input.event().getMember().getId());
          if (optDungeonMaster.isPresent() && optDungeonMaster.get().equals(game.getPlayers().getFirst())) {
            boolean kickedDM = false;
            Optional<Player> toKick = Optional.empty();
            Optional<PlayerColor> optColor = MessageUtil.parseColor(input.argString());
            if (optColor.isPresent()) {
              toKick = game.getPlayer(optColor.get());
            }
            if (toKick.isPresent()) {
              if (toKick.get().getID().equals(game.getPlayers().getFirst().getID())) {
                kickedDM = true;
              }
              Response response = game.kick(toKick.get().getID());
              if (response.success()) {
                EmbedBuilder embedBuilder = settings.embedBuilder();
                embedBuilder.setTitle("Remove Successful");
                embedBuilder.setDescription(toKick.get().getName() + " has been removed from the game.");
                embedBuilder.setFooter("Version: " + Constants.VERSION);
                if (game.getPlayers().getFirst().isComputer() || game.getPlayers().stream().allMatch(Player::isComputer)) {
                  embedBuilder
                      .setDescription(toKick.get().getName() + " has been kicked from the game.\n" + "The game has been ended because dummy players cannot be Dungeon Masters.");
                  api.delete(input.event().getGuild().getId(), input.event().getChannel().getId());
                } else {
                  if (kickedDM) {
                    embedBuilder.setDescription(toKick.get().getName() + " has been kicked from the game.\n"
                        + game.getPlayers().getFirst().getName() + " is the new Dungeon Master.");
                  }
                  api.save(game, input.event().getGuild().getId(), input.event().getChannel().getId());
                }
                input.event().getChannel().sendMessage(embedBuilder.build()).queue();
              } else {
                input.event().getChannel().sendMessage(Error.create(response, this.settings)).queue();
              }
            } else {
              input.event().getChannel().sendMessage(Error.create("Could not find a player to kick.", this.settings)).queue();
            }
          } else {
            input.event().getChannel().sendMessage(Error.create("You need to be the Dungeon Master to use this command.", this.settings)).queue();
          }
        } else {
          input.event().getChannel().sendMessage(Error.create("Players cannot be removed until at least one person joins the game.", this.settings)).queue();
        }
      } else {
        input.event().getChannel().sendMessage(Error.create("Invalid game mode.", this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
    }
  }


}
