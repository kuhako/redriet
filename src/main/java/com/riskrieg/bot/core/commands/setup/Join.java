package com.riskrieg.bot.core.commands.setup;


import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.MessageUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.gamemode.Game;
import com.riskrieg.gamemode.creative.Creative;
import com.riskrieg.player.HumanPlayer;
import com.riskrieg.player.Player;
import com.riskrieg.player.PlayerColor;
import com.riskrieg.response.Response;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;

public class Join extends Command {

  public Join() {
    this.settings.setAliases("join");
    this.settings.setDescription("Join a game.");
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
      Optional<PlayerColor> chosenColor = MessageUtil.parseColor(input.argString().trim());
      if (chosenColor.isPresent()) {
        Player player = new HumanPlayer(input.event().getMember().getId(), input.event().getMember().getEffectiveName(), chosenColor.get());
        Response response = game.join(player);
        if (response.success()) {
          EmbedBuilder embedBuilder = new EmbedBuilder();
          embedBuilder.setColor(player.getColor().value());
          embedBuilder.setTitle("Joined");
          if (game instanceof Creative && game.getPlayers().size() == 1) {
            embedBuilder.setDescription("**" + player.getName() + "** has joined the game as **" + player.getColor().getName() + "**."
                + "\n\n**" + player.getName() + "** is the Dungeon Master.");
          } else {
            embedBuilder.setDescription("**" + player.getName() + "** has joined the game as **" + player.getColor().getName() + "**.");
          }
          input.event().getChannel().sendMessage(embedBuilder.build()).queue();
          api.save(game, input.event().getGuild().getId(), input.event().getChannel().getId());
        } else {
          input.event().getChannel().sendMessage(Error.create(response, this.settings)).queue();
        }
      } else {
        input.event().getChannel().sendMessage(Error.create("You must choose a valid color.", this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
    }
  }

}
