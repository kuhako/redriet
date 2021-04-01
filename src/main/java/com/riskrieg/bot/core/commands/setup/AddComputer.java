package com.riskrieg.bot.core.commands.setup;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.MessageUtil;
import com.riskrieg.bot.util.RiskriegUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.gamemode.Game;
import com.riskrieg.map.graph.Territory;
import com.riskrieg.player.ComputerPlayer;
import com.riskrieg.player.PlayerColor;
import com.riskrieg.response.Response;
import java.util.Arrays;
import java.util.Optional;

public class AddComputer extends Command {

  public AddComputer() {
    this.settings.setAliases("joinc", "joincomputer");
    this.settings.setDescription("Add a computer to the game.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setGuildOnly(true);
    this.settings.setDisabled(true); // TODO: Temporary
  }


  protected void execute(MessageInput input) { // TODO: Maybe make syntax error send after create game error
    if (input.args().length >= 2) {
      Riskrieg api = new Riskrieg();
      Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());
      if (optGame.isPresent()) {
        Game game = optGame.get();
        if (game.getMap().isPresent()) {
          Optional<PlayerColor> chosenColor = MessageUtil.parseColor(input.arg(0).trim());
          if (chosenColor.isPresent()) {
            String name = String.join(" ", Arrays.copyOfRange(input.args(), 1, input.args().length)).trim();
            if (!name.isEmpty()) {
              ComputerPlayer computer = new ComputerPlayer(name, chosenColor.get());
              Response joinResponse = game.join(computer);
              if (joinResponse.success()) {
                StringBuilder sb = new StringBuilder();
                sb.append(computer.getName()).append(" has joined the game as ").append(computer.getColor().getName()).append(".").append("\n");
                Optional<Territory> optTerritory = computer.selectCapital(game.getMap().get(), game.getNations());
                if (optTerritory.isPresent()) {
                  Response formResponse = game.formNation(computer, optTerritory.get());
                  if (formResponse.success()) {
                    sb.append("**").append(computer.getName()).append("** has selected **").append(optTerritory.get().name()).append("** as their capital.");
                    RiskriegUtil.sendMap(game, input.event().getChannel(), computer.getColor().value(), "Joined", sb.toString(),
                        "The game may begin once every player has selected a capital.");
                    api.save(game, input.event().getGuild().getId(), input.event().getChannel().getId());
                  } else {
                    input.event().getChannel().sendMessage(Error.create(formResponse, this.settings)).queue();
                  }
                } else {
                  input.event().getChannel().sendMessage(Error.create("Error selecting capital territory.", this.settings)).queue();
                }
              } else {
                input.event().getChannel().sendMessage(Error.create(joinResponse, this.settings)).queue();
              }
            } else {
              input.event().getChannel().sendMessage(Error.create("You must supply a name.", this.settings)).queue();
            }
          } else {
            input.event().getChannel().sendMessage(Error.create("You must choose a valid color.", this.settings)).queue();
          }
        } else {
          input.event().getChannel().sendMessage(Error.create("You need to select a map first.", this.settings)).queue();
        }
      } else {
        input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("Invalid syntax. | Usage: `" + this.settings.getName() + " [color] [name]`", this.settings)).queue();
    }
  }

}
