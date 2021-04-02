package com.riskrieg.bot.core.commands.moderation;

import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;

public class GetSave extends Command {

  public GetSave() {
    this.settings.setAliases("get-save", "getsave");
    this.settings.setDescription("Gets a save and uploads the file to Discord.");
    this.settings.setEmbedColor(BotConstants.MOD_CMD_COLOR);
    this.settings.setOwnerCommand(true);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {

  }

}
