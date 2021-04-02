package com.riskrieg.bot.core.commands.running;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.MessageUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.gamemode.Game;
import com.riskrieg.gamemode.IAlliances;
import com.riskrieg.nation.AllianceNation;
import com.riskrieg.nation.Nation;
import com.riskrieg.player.Player;
import com.riskrieg.player.PlayerColor;
import com.riskrieg.player.PlayerIdentifier;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.dv8tion.jda.api.EmbedBuilder;

public class ListAlliances extends Command {

  public ListAlliances() {
    this.settings.setAliases("allies", "alliances");
    this.settings.setDescription("Lists allies.");
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
      if (game instanceof IAlliances) {
        if (game.getGameRule("alliances").isPresent() && game.getGameRule("alliances").get().isEnabled()) { // TODO: Could split these
          Optional<Player> optPlayer = Optional.empty();
          if (input.args().length == 0) {
            optPlayer = game.getPlayer(input.event().getMember().getId());
          } else if (input.args().length == 1) {
            optPlayer = getPlayer(game, input);
          }
          if (optPlayer.isPresent()) {
            Optional<Nation> optNation = game.getNation(optPlayer.get());
            if (optNation.isPresent() && optNation.get() instanceof AllianceNation) {
              Player player = optPlayer.get();
              AllianceNation nation = (AllianceNation) optNation.get();

              EmbedBuilder embedBuilder = new EmbedBuilder();
              embedBuilder.setTitle(player.getName() + "'s Allies");
              embedBuilder.setColor(player.getColor().value());

              StringBuilder alliedSb = new StringBuilder();
              StringBuilder outgoingRequests = new StringBuilder();
              StringBuilder incomingRequests = new StringBuilder();

              // Allies & Outgoing Requests
              for (PlayerIdentifier pid : nation.getAllies()) {
                game.getPlayer(pid.color()).ifPresent(p -> {
                  if (((IAlliances) game).allied(player.getID(), p.getID())) { // Allies
                    alliedSb.append("**").append(p.getName()).append("**").append("\n");
                  } else { // Requests To
                    outgoingRequests.append("**").append(p.getName()).append("**").append("\n");
                  }
                });
              }

              // Incoming Requests
              for (PlayerIdentifier pid : getIncomingRequests(nation, game.getNations())) {
                game.getPlayer(pid.color()).ifPresent(p -> incomingRequests.append("**").append(p.getName()).append("**").append("\n"));
              }

              if (alliedSb.isEmpty()) {
                embedBuilder.addField("Allies", "*None.*", true);
              } else {
                embedBuilder.addField("Allies", alliedSb.toString(), true);
              }
              if (outgoingRequests.isEmpty()) {
                embedBuilder.addField("Outgoing Requests", "*No pending requests.*", true);
              } else {
                embedBuilder.addField("Outgoing Requests", outgoingRequests.toString(), true);
              }
              if (incomingRequests.isEmpty()) {
                embedBuilder.addField("Incoming Requests", "*No pending requests.*", true);
              } else {
                embedBuilder.addField("Incoming Requests", incomingRequests.toString(), true);
              }
              if (!embedBuilder.isEmpty()) {
                input.event().getChannel().sendMessage(embedBuilder.build()).queue();
              }
            } else {
              input.event().getChannel().sendMessage(Error.create("You can only check the allies of someone who has territory.", this.settings)).queue();
            }
          } else {
            input.event().getChannel().sendMessage(Error.create("Player is not in the game.", this.settings)).queue();
          }
        } else {
          input.event().getChannel().sendMessage(Error.create("The alliances game rule is disabled.", this.settings)).queue();
        }
      } else {
        input.event().getChannel().sendMessage(Error.create("Invalid game mode.", this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
    }
  }

  private Optional<Player> getPlayer(Game game, MessageInput input) {
    Optional<PlayerColor> optColor = MessageUtil.parseColor(input.argString());
    if (optColor.isPresent()) {
      return game.getPlayer(optColor.get());
    } else if (input.event().getMessage().getMentionedMembers().size() == 1) {
      return game.getPlayer(input.event().getMessage().getMentionedMembers().get(0).getId());
    }
    return Optional.empty();
  }

  private Set<PlayerIdentifier> getIncomingRequests(AllianceNation nation, Set<Nation> nations) {
    Set<PlayerIdentifier> result = new HashSet<>();
    nations.forEach(n -> {
      if (!nation.getAllies().contains(n.getLeaderIdentifier()) && ((AllianceNation) n).getAllies().contains(nation.getLeaderIdentifier())) {
        result.add(n.getLeaderIdentifier());
      }
    });
    return result;
  }

}
