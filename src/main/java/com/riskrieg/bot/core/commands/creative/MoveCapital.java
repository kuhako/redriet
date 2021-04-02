package com.riskrieg.bot.core.commands.creative;

import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.constant.Colors;

public class MoveCapital extends Command {

  public MoveCapital() {
    this.settings.setAliases("movecapital", "move-capital", "newcapital", "new-capital");
    this.settings.setDescription("Moves a player's capital.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
    this.settings.setGuildOnly(true);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {
    // TODO: Write this command
  }


}
