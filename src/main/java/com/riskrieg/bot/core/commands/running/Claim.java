package com.riskrieg.bot.core.commands.running;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.PreferenceUtil;
import com.riskrieg.bot.util.RiskriegUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.gamemode.Game;
import com.riskrieg.map.GameMap;
import com.riskrieg.map.graph.Territory;
import com.riskrieg.nation.AllianceNation;
import com.riskrieg.nation.ConquestNation;
import com.riskrieg.nation.Nation;
import com.riskrieg.player.Player;
import com.riskrieg.response.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.dv8tion.jda.api.entities.Member;

public class Claim extends Command {

  public Claim() {
    this.settings.setAliases("claim", "attack", "take");
    this.settings.setDescription("Tests to see if the bot is online and functional.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setGuildOnly(true);
  }

  protected void execute(MessageInput input) {
    Riskrieg api = new Riskrieg();
    Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());

    Member member = input.event().getMember();
    if (member == null) {
      input.event().getChannel().sendMessage(Error.create("Could not find member.", this.settings)).queue();
      return;
    }

    if (optGame.isPresent()) {
      Game game = optGame.get();
      if (game.getMap().isPresent()) {

        TerritoryParser parser = new TerritoryParser();
        parser.parse(game, game.getMap().get(), member, input);

        if (parser.errors().isEmpty()) {
          Response claimResponse = game.claim(member.getId(), parser.territories().toArray(new Territory[0]));
          if (claimResponse.success()) {
            Response updateResponse = game.update(); // Checks for defeated/skips/etc.
            if (updateResponse.success()) {
              game.nextTurn(); // Updates turn
              if (claimResponse.getMessage().isPresent() && updateResponse.getMessage().isPresent()) {
                RiskriegUtil.sendConquestTurn(game, input.event().getChannel(), settings, claimResponse.getMessage().get() + "\n" + updateResponse.getMessage().get());
              } else if (claimResponse.getMessage().isPresent()) {
                RiskriegUtil.sendConquestTurn(game, input.event().getChannel(), settings, claimResponse.getMessage().get());
              } else if (updateResponse.getMessage().isPresent()) {
                RiskriegUtil.sendConquestTurn(game, input.event().getChannel(), settings, updateResponse.getMessage().get());
              } else {
                RiskriegUtil.sendConquestTurn(game, input.event().getChannel(), settings);
              }
              if (game.isEnded()) {
                api.delete(input.event().getGuild().getId(), input.event().getChannel().getId());
              } else {
                Player current = game.getPlayers().getFirst();
                if (current.isComputer()) {
                  // TODO: Handle case where player is computer.
                }
                PreferenceUtil.sendPingIfEnabled(input.event().getGuild(), current, input.event().getChannel());
                api.save(game, input.event().getGuild().getId(), input.event().getChannel().getId());
              }

            } else {
              input.event().getChannel().sendMessage(Error.create(updateResponse, this.settings)).queue();
            }
          } else {
            input.event().getChannel().sendMessage(Error.create(claimResponse, this.settings)).queue();
          }
        } else {
          input.event().getChannel().sendMessage(Error.create("The following inputs could not be handled: " + String.join(", ", parser.errors()), this.settings)).queue();
        }
      } else {
        input.event().getChannel().sendMessage(Error.create("No map found.", this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
    }

  }

}

class TerritoryParser { // TODO: Turn into Record in Java 16

  private final Set<Territory> territories;
  private final List<String> errors;

  public TerritoryParser() {
    this.territories = new HashSet<>();
    this.errors = new ArrayList<>();
  }

  public Set<Territory> territories() {
    return territories;
  }

  public List<String> errors() {
    return errors;
  }

  public void parse(Game game, GameMap map, Member member, MessageInput input) {
    if (input.argString().toLowerCase().trim().equals("auto")) {
      Optional<Nation> optNation = game.getNation(member.getId());
      if (optNation.isPresent()) {
        Nation nation = optNation.get();
        if (nation instanceof ConquestNation) {
          LinkedList<Territory> neighbors = new LinkedList<>(nation.getNeighbors(map));
          Collections.shuffle(neighbors);
          int claims = ((ConquestNation) nation).getClaimAmount(game);
          neighbors.removeIf(territory -> {
            Optional<Nation> optOwner = game.getNation(territory);
            return optOwner.isPresent() && ((ConquestNation) nation).isAlly((AllianceNation) optOwner.get());
          });
          if (claims <= neighbors.size()) {
            for (int i = 0; i < claims; i++) {
              territories.add(neighbors.removeFirst());
            }
          }
        }
      }
    } else {
      for (String s : input.args()) {
        Optional<Territory> optTerritory = map.getTerritory(s);
        if (optTerritory.isPresent()) {
          territories.add(optTerritory.get());
        } else {
          errors.add(s);
        }
      }
    }
  }

}