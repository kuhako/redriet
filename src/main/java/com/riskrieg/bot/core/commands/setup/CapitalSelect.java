package com.riskrieg.bot.core.commands.setup;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.RiskriegUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.gamemode.Game;
import com.riskrieg.map.graph.Territory;
import com.riskrieg.response.Response;
import java.util.Optional;

public class CapitalSelect extends Command {

  public CapitalSelect() {
    this.settings.setAliases("capital", "select");
    this.settings.setDescription("Selects a capital territory.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setGuildOnly(true);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) { // TODO: Probably put more of this checking logic into the Riskrieg portion?
    Riskrieg api = new Riskrieg();
    Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());
    if (optGame.isPresent()) {
      Game game = optGame.get();
      if (game.getMap().isPresent()) {
        Optional<Territory> optTerritory = game.getMap().get().getTerritory(input.argString().trim());
        if (optTerritory.isPresent()) {
          Response response = game.formNation(input.event().getMember().getId(), optTerritory.get());
          if (response.success()) {
            RiskriegUtil.sendMap(game, input.event().getChannel(), settings.getEmbedColor(), game.getMap().get().displayName(),
                "**" + input.event().getMember().getEffectiveName() + "** has selected **" + optTerritory.get().name() + "** as their capital.",
                "Your capital has been fortified.");
            api.save(game, input.event().getGuild().getId(), input.event().getChannel().getId());
          } else {
            input.event().getChannel().sendMessage(Error.create(response, this.settings)).queue();
          }
        } else {
          input.event().getChannel().sendMessage(Error.create("That territory does not exist on this map.", this.settings)).queue();
        }
      } else {
        input.event().getChannel().sendMessage(Error.create("You must choose a map before selecting a capital.", this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
    }
  }

}

