package com.riskrieg.bot.core.commands.stateless;

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
import net.dv8tion.jda.api.Permission;

public class Kick extends Command {

  public Kick() {
    this.settings.setAliases("kick");
    this.settings.setDescription("Removes a user from the game forcibly.");
    this.settings.setGuildOnly(true);
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setAuthorPerms(Permission.KICK_MEMBERS);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {
    Riskrieg api = new Riskrieg();
    Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());
    if (optGame.isPresent()) {
      Game game = optGame.get();

      Optional<Player> toKick = Optional.empty();
      Optional<PlayerColor> optColor = MessageUtil.parseColor(input.argString());
      if (optColor.isPresent()) {
        toKick = game.getPlayer(optColor.get());
      } else if (input.event().getMessage().getMentionedMembers().size() == 1) {
        toKick = game.getPlayer(input.event().getMessage().getMentionedMembers().get(0).getId());
      }

      if (toKick.isPresent()) {
        Response response = game.kick(toKick.get().getID());
        if (response.success()) {
          EmbedBuilder embedBuilder = settings.embedBuilder();
          embedBuilder.setTitle("Kick Successful");
          embedBuilder.setDescription(toKick.get().getName() + " has been kicked from the game.");
          embedBuilder.setFooter("Version: " + Constants.VERSION);

          Response updateResponse = game.update();
          if (updateResponse.success()) {
            embedBuilder.setDescription(toKick.get().getName() + " has been kicked from the game.\n" + updateResponse.getMessage().orElse(""));
            if (game.isEnded() || (game instanceof Creative && game.getPlayers().getFirst().isComputer()) || game.getPlayers().stream().allMatch(Player::isComputer)) {
              api.delete(input.event().getGuild().getId(), input.event().getChannel().getId());
            } else {
              api.save(game, input.event().getGuild().getId(), input.event().getChannel().getId());
            }
          }
          input.event().getChannel().sendMessage(embedBuilder.build()).queue();
        } else {
          input.event().getChannel().sendMessage(Error.create(response, this.settings)).queue();
        }
      } else {
        input.event().getChannel().sendMessage(Error.create("Could not find a player to kick.", this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
    }
  }

}
