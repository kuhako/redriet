package com.riskrieg.bot.core.commands.creative;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.MessageUtil;
import com.riskrieg.bot.util.RiskriegUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.gamemode.Game;
import com.riskrieg.gamemode.creative.Creative;
import com.riskrieg.map.graph.Territory;
import com.riskrieg.nation.Nation;
import com.riskrieg.player.Player;
import com.riskrieg.player.PlayerColor;
import com.riskrieg.response.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GrantTerritory extends Command {

  public GrantTerritory() {
    this.settings.setAliases("grant");
    this.settings.setDescription("Grants the specified territories to the specified player.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setGuildOnly(true);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {
    if (input.args().length >= 2) {
      Riskrieg api = new Riskrieg();
      Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());
      if (optGame.isPresent()) {
        Game game = optGame.get();
        if (game instanceof Creative) {
          Optional<Player> optDungeonMaster = game.getPlayer(input.event().getMember().getId());
          if (optDungeonMaster.isPresent() && optDungeonMaster.get().equals(game.getPlayers().getFirst())) {
            Optional<PlayerColor> chosenColor = MessageUtil.parseColor(input.arg(0).trim());
            if (chosenColor.isPresent()) {

              // Get territory list
              List<Territory> territoryList = new ArrayList<>();
              List<String> errorList = new ArrayList<>();
              for (String s : Arrays.copyOfRange(input.args(), 1, input.args().length)) {
                Optional<Territory> optTerritory = game.getMap().get().getTerritory(s);
                if (optTerritory.isPresent()) {
                  territoryList.add(optTerritory.get());
                } else {
                  errorList.add(s);
                }
              }

              if (errorList.isEmpty()) {
                Optional<Player> optPlayer = game.getPlayer(chosenColor.get());
                if (optPlayer.isPresent()) {

                  Optional<Nation> optNation = game.getNation(chosenColor.get());
                  if (optNation.isEmpty()) {
                    game.formNation(optPlayer.get(), territoryList.get(0));
                  }

                  Response giveResponse = game.give(optPlayer.get(), territoryList.toArray(new Territory[0]));
                  if (giveResponse.success()) {
                    RiskriegUtil.sendMap(game, input.event().getChannel(), settings.getEmbedColor(), game.getMap().get().name(),
                        "Territories given successfully.");
                  } else {
                    input.event().getChannel().sendMessage(Error.create(giveResponse, this.settings)).queue();
                  }
                  api.save(game, input.event().getGuild().getId(), input.event().getChannel().getId());

                } else {
                  input.event().getChannel().sendMessage(Error.create("Could not find player.", this.settings)).queue();
                }
              } else {
                input.event().getChannel().sendMessage(Error.create("The following inputs could not be handled: " + String.join(", ", errorList), this.settings)).queue();
              }
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
    } else {
      input.event().getChannel().sendMessage(Error.create("Invalid syntax. | Usage: `" + this.settings.getName() + " [color] [territories]`", this.settings)).queue();
    }
  }

  private void giveAndSendMap(MessageInput input, Game game, Player player, List<Territory> territories) {
    Response giveResponse = game.give(player, territories.toArray(new Territory[0]));
    if (giveResponse.success()) {
      RiskriegUtil.sendMap(game, input.event().getChannel(), settings.getEmbedColor(), game.getMap().get().name(), "Territories given successfully.");
    } else {
      input.event().getChannel().sendMessage(Error.create(giveResponse, this.settings)).queue();
    }
  }

}
