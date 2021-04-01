package com.riskrieg.bot.core.commands.setup;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.MessageUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.gamemode.Game;
import com.riskrieg.response.Response;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;

public class SetGameRule extends Command {

  public SetGameRule() {
    this.settings.setAliases("gamerule", "gr", "rule");
    this.settings.setDescription("Set game rules.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setGuildOnly(true);
  }

  protected void execute(MessageInput input) { // TODO: Maybe make syntax error send after create game error
    if (input.args().length == 2) {
      Riskrieg api = new Riskrieg();
      Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());
      if (optGame.isPresent()) {
        Game game = optGame.get();
        Optional<Boolean> optEnable = MessageUtil.parseEnable(input.arg(1).toLowerCase().trim());
        if (optEnable.isPresent()) {
          Response response = game.setGameRule(input.arg(0).toLowerCase().trim(), optEnable.get());
          if (response.success()) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Game Rule Updated");
            embedBuilder.addField("Game Rules", MessageUtil.listGameRules(game, false), false);
            embedBuilder.setColor(this.settings.getEmbedColor());
            input.event().getChannel().sendMessage(embedBuilder.build()).queue();
            api.save(game, input.event().getGuild().getId(), input.event().getChannel().getId());
          } else {
            input.event().getChannel().sendMessage(Error.create(response, settings)).queue();
          }
        } else {
          input.event().getChannel().sendMessage(Error.create("Invalid syntax. | Usage: `" + this.settings.getName() + " [gamerule] [y/n]`", this.settings)).queue();
        }
      } else {
        input.event().getChannel().sendMessage(Error.create("You need to create a game before using this command.", this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("Invalid syntax. | Usage: `" + this.settings.getName() + " [gamerule] [y/n]`", this.settings)).queue();
    }
  }

}