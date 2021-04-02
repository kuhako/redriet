package com.riskrieg.bot.core.commands.stateless;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.constant.Colors;
import com.riskrieg.constant.Constants;
import com.riskrieg.gamemode.Game;
import com.riskrieg.gamemode.creative.Creative;
import com.riskrieg.player.Player;
import com.riskrieg.response.Response;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;

public class Leave extends Command {

  public Leave() {
    this.settings.setAliases("leave", "surrender", "forfeit");
    this.settings.setDescription("Removes the user from the game.");
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
      Response response = game.kick(input.event().getMember().getId());
      if (response.success()) {
        EmbedBuilder embedBuilder = settings.embedBuilder();
        embedBuilder.setTitle("Leave Successful");
        embedBuilder.setDescription(input.event().getMember().getEffectiveName() + " has left the game.");
        embedBuilder.setFooter("Version: " + Constants.VERSION);

        Response updateResponse = game.update();
        if (updateResponse.success()) {
          embedBuilder.setDescription(input.event().getMember().getEffectiveName() + " has left the game.\n" + updateResponse.getMessage().orElse(""));
          if (game.isEnded() || (game instanceof Creative && game.getPlayers().getFirst().isComputer()) || game.getPlayers().stream().allMatch(Player::isComputer)) {
            api.delete(input.event().getGuild().getId(), input.event().getChannel().getId());
          } else {
            if (game instanceof Creative) {
              embedBuilder.setDescription(input.event().getMember().getEffectiveName() + " has left the game.\n"
                  + game.getPlayers().getFirst().getName() + " is the new Dungeon Master.");
            }
            api.save(game, input.event().getGuild().getId(), input.event().getChannel().getId());
          }
          input.event().getChannel().sendMessage(embedBuilder.build()).queue();
        } else {
          input.event().getChannel().sendMessage(Error.create(updateResponse, this.settings)).queue();
        }
      } else {
        input.event().getChannel().sendMessage(Error.create(response, this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
    }


    /*
    Save save = Save.load(input.getEvent().getGuild().getId(), input.getEvent().getChannel().getId());
    Leader leader = RiskriegUtil.getLeader(save, input.getEvent().getMember().getId());
    Response<Boolean> response = save.getGame().remove(leader);
    if (response.get()) {
      Response<Update> updateResponse = save.getGame().update(false);
      Country current;
      EmbedBuilder embedBuilder;
      switch (updateResponse.get()) {
        case SETUP_NORMAL -> {
          embedBuilder = new EmbedBuilder();
          embedBuilder.setTitle("Removed");
          embedBuilder.setColor(this.settings.getEmbedColor());
          embedBuilder.setDescription(input.getEvent().getMember().getEffectiveName() + " has left the game.");
          embedBuilder.setTimestamp(Instant.now());
          input.getEvent().getChannel().sendMessage(embedBuilder.build()).queue();
          save.save();
        }
        case NO_PLAYERS -> {
          embedBuilder = new EmbedBuilder();
          embedBuilder.setTitle("Removed & Game Ended");
          embedBuilder.setColor(this.settings.getEmbedColor());
          embedBuilder.setDescription(input.getEvent().getMember().getEffectiveName() + " has left the game.\n\nNobody is left in the game, so the game has been ended.");
          embedBuilder.setTimestamp(Instant.now());
          input.getEvent().getChannel().sendMessage(embedBuilder.build()).queue();
          save.delete();
        }
        case NORMAL -> {
          current = save.getGame().getCountry(save.getGame().getLeaders().getFirst());
          int claims = current.getClaims(save.getGame().getMap(), save.getGame().getCountries());
          String territoryStr = claims == 1 ? "territory" : "territories";
          RiskriegUtil.sendMap(save, input, this.settings, save.getGame().getMap().getDisplayName(), "**" + leader.getName() + "** has left the game!",
              "It is " + current.getLeader().getName() + "'s turn. They may claim " + claims + " " + territoryStr + " this turn.");
          if (current.getLeader().isComputer()) {
            RiskriegUtil.updateTurn(input, settings, save, current.getLeader(), ((Computer) current.getLeader()).getTerritoriesToClaim(save.getGame()).toArray(new Territory[0]));
          }
          save.save();
        }
        case WINNER -> {
          current = save.getGame().getCountry(save.getGame().getLeaders().getFirst());
          RiskriegUtil.sendMap(save, input, this.settings, save.getGame().getMap().getDisplayName(),
              "**" + leader.getName() + "** has left the game!\n\n" + current.getLeader().getName() + " has won the game!", "The game has now ended.");
          save.delete();
        }
        default -> save.save();
      }
    } else {
      input.getEvent().getChannel().sendMessage(Error.create(response.reason(), this.settings)).queue();
    }*/
  }

}
