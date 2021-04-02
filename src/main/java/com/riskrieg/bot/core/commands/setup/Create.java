package com.riskrieg.bot.core.commands.setup;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.Main;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.MessageUtil;
import com.riskrieg.constant.Colors;
import com.riskrieg.constant.Constants;
import com.riskrieg.gamemode.Game;
import com.riskrieg.gamemode.GameMode;
import com.riskrieg.response.Response;
import java.io.File;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.utils.AttachmentOption;

public class Create extends Command {

  public Create() {
    this.settings.setAliases("create");
    this.settings.setDescription("Creates a new " + Constants.NAME + " game.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setGuildOnly(true);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {
    Riskrieg api = new Riskrieg();
    Optional<GameMode> chosenGameMode = MessageUtil.getClosestGameMode(input.argString().trim());

    if (chosenGameMode.isPresent()) {
      Response response = api.create(chosenGameMode.get(), input.event().getGuild().getId(), input.event().getChannel().getId());
      if (response.success()) {
        Optional<Game> optGame = api.load(input.event().getGuild().getId(), input.event().getChannel().getId());
        if (optGame.isPresent()) {
          Game game = optGame.get();
          sendMessage(game, chosenGameMode.get(), input);
        }
      } else {
        input.event().getChannel().sendMessage(Error.create(response, this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("You need to supply a valid game mode when creating a game.\nUse the `"
          + Main.bot.auth().prefix() + "gamemodes` command to see a list of game modes.", this.settings)).queue();
    }
  }

  private void sendMessage(Game game, GameMode gameMode, MessageInput input) {
    StringBuilder desc = new StringBuilder();
    desc.append("Game Mode Selected: **").append(gameMode.getName()).append("**").append("\n");

    // TODO: Send different messages based on game mode selection. As of this todo, there is only Conquest and Creative, so not a terribly big deal for now.

    MessageBuilder messageBuilder = new MessageBuilder();
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(this.settings.getEmbedColor());
    embedBuilder.setTitle("Join Game & Set Game Rules");
    embedBuilder.setImage("attachment://color-choices.png");
    embedBuilder.addField("Game Rules", MessageUtil.listGameRules(game, true), false);
    embedBuilder.setDescription(desc.toString());
    embedBuilder.setFooter("Please select a color to join the game. You may also set the game rules. After you are done, you may choose a map.");
    messageBuilder.setEmbed(embedBuilder.build());
    input.event().getChannel().sendMessage(messageBuilder.build()).addFile(new File(Constants.COLOR_CHOICES), "color-choices.png", new AttachmentOption[0]).queue();
  }

}
