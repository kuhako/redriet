package com.riskrieg.bot.core.commands.setup;

import com.aaronjyoder.util.json.moshi.MoshiUtil;
import com.google.gson.reflect.TypeToken;
import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.RiskriegUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.constant.Constants;
import com.riskrieg.gamemode.Game;
import com.riskrieg.gamemode.creative.Creative;
import com.riskrieg.map.GameMap;
import com.riskrieg.map.MapStatus;
import com.riskrieg.map.graph.Territory;
import com.riskrieg.player.ComputerPlayer;
import com.riskrieg.player.Player;
import com.riskrieg.response.Response;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Optional;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class MapSelect extends Command {

  public MapSelect() {
    this.settings.setAliases("map");
    this.settings.setDescription("Selects a map.");
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
      Optional<String> optMapName = getClosestMapName(input.argString().trim());
      if (optMapName.isPresent()) {
        try {
          GameMap selectedMap = new GameMap(optMapName.get());
          if (selectedMap.status().equals(MapStatus.AVAILABLE)) {
            Response response = game.setMap(selectedMap);
            if (game instanceof Creative) { // TODO: Ideally move this into Riskrieg instead of here
              if (game.getPlayers().size() > 0 && !game.getPlayers().getFirst().getID().equals(input.event().getMember().getId())) {
                response = new Response(false, "Only the Dungeon Master can set the map.");
              }
            }
            if (response.success()) {
              String desc = selectCapitals(game, game.getMap().get());
              if (desc.isEmpty()) {
                RiskriegUtil.sendMap(game, input.event().getChannel(), settings.getEmbedColor(),
                    "Map created by " + game.getMap().get().author(),
                    "Map Selected: " + game.getMap().get().displayName(), null,
                    "If you do not have a capital, you may select one.");
              } else {
                RiskriegUtil.sendMap(game, input.event().getChannel(), settings.getEmbedColor(),
                    "Map created by " + game.getMap().get().author(),
                    "Map Selected: " + game.getMap().get().displayName(), desc,
                    "If you do not have a capital, you may select one.");
              }
              api.save(game, input.event().getGuild().getId(), input.event().getChannel().getId());
            } else {
              input.event().getChannel().sendMessage(Error.create(response, this.settings)).queue();
            }
          } else {
            input.event().getChannel().sendMessage(Error.create("You must choose a valid available map.", this.settings)).queue();
          }
        } catch (IllegalArgumentException | NullPointerException ex) {
          input.event().getChannel().sendMessage(Error.create("You must choose a valid map.", this.settings)).queue();
        }
      } else {
        input.event().getChannel().sendMessage(Error.create("You must choose a valid map.", this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
    }
  }

  private Optional<String> getClosestMapName(String requestedName) {
    if (requestedName == null || requestedName.isEmpty()) {
      return Optional.empty();
    }
    Type type = (new TypeToken<HashSet<String>>() {
    }).getType();
    HashSet<String> availableMaps = MoshiUtil.read(Constants.AVAILABLE_MAPS, type);

    String closestName = null;
    int lowestDistance = Integer.MAX_VALUE;
    for (String name : availableMaps) {
      int distance = LevenshteinDistance.getDefaultInstance().apply(requestedName, name);
      if (distance < 5 && distance < lowestDistance) {
        lowestDistance = distance;
        closestName = name;
      }
    }
    return Optional.ofNullable(closestName);
  }

  private String selectCapitals(Game game, GameMap gameMap) {
    StringBuilder sb = new StringBuilder();
    for (Player player : game.getPlayers()) {
      if (player.isComputer()) {
        Optional<Territory> optTerritory = ((ComputerPlayer) player).selectCapital(gameMap, game.getNations());
        if (optTerritory.isPresent()) {
          Response formResponse = game.formNation(player.getID(), optTerritory.get());
          if (formResponse.success()) {
            sb.append("**").append(player.getName()).append("** has selected **").append(optTerritory.get().name()).append("** as their capital territory.").append("\n");
          } else {
            sb.append("Error: ").append(player.getName()).append(": ").append(optTerritory.get().name()).append(" is already taken.").append("\n");
          }
        } else {
          sb.append("Error: Could not select capital territory for ").append(player.getName()).append("\n");
        }
      }
    }
    return sb.toString();
  }

}
