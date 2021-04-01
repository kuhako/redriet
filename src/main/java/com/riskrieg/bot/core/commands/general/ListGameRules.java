package com.riskrieg.bot.core.commands.general;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.util.MessageUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.constant.Constants;
import com.riskrieg.gamemode.Game;
import com.riskrieg.gamemode.GameMode;
import com.riskrieg.gamemode.conquest.Conquest;
import com.riskrieg.gamemode.creative.Creative;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;

public class ListGameRules extends Command {

  public ListGameRules() {
    this.settings.setAliases("gamerules");
    this.settings.setDescription("Lists available game rules.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setGuildOnly(true);
  }

  protected void execute(MessageInput input) {
    Riskrieg api = new Riskrieg();
    Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(this.settings.getEmbedColor());
    embedBuilder.setTitle(Constants.NAME + " Game Rules");
    embedBuilder.setFooter(Constants.NAME + " " + Constants.VERSION);
    if (optGame.isPresent()) {
      Game game = optGame.get();
      embedBuilder.setDescription(MessageUtil.listGameRules(game, true));
    } else {
      embedBuilder.setDescription("Showing game rules for all game modes.");
      for (GameMode gameMode : GameMode.values()) {
        Game tempGame = switch (gameMode) {
          case CONQUEST -> new Conquest();
          case CREATIVE -> new Creative();
        };
        embedBuilder.addField(tempGame.getName(), MessageUtil.listGameRules(tempGame, false), false);
      }
    }
    input.event().getChannel().sendMessage(embedBuilder.build()).queue();
  }

}
