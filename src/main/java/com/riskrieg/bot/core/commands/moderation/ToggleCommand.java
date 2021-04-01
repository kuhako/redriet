package com.riskrieg.bot.core.commands.moderation;

import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.CommandHandler;
import com.riskrieg.bot.core.input.MessageInput;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class ToggleCommand extends Command {

  public ToggleCommand() {
    settings.setAliases("toggle-command", "toggle-cmd");
    settings.setEmbedColor(BotConstants.MOD_CMD_COLOR);
    settings.setOwnerCommand(true);
  }

  @Override
  protected void execute(MessageInput input) {
    if (input.args().length == 1) {
      Command command = getCommand(input.arg(0));
      if (command != null && command != this) {
        command.getSettings().setDisabled(!command.getSettings().isDisabled());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command Status");
        embedBuilder.setDescription("Command `" + command.getSettings().getName() + "` is now **" + (command.getSettings().isDisabled() ? "disabled" : "enabled") + "**.");
        embedBuilder.setColor(this.settings.getEmbedColor());
        input.event().getChannel().sendMessage(embedBuilder.build()).complete();
      }
    }
  }

  private Command getCommand(String alias) {
    for (Command command : CommandHandler.commands) {
      for (String cmdAlias : command.getSettings().getAliases()) {
        if (LevenshteinDistance.getDefaultInstance().apply(alias, cmdAlias) == 0) {
          return command;
        }
      }
    }
    return null;
  }

}
