package com.riskrieg.bot.core.commands.running;

import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import net.dv8tion.jda.api.Permission;

public class ForceSkip extends Command { // TODO: Write this command

  public ForceSkip() {
    this.settings.setAliases("forceskip", "force-skip");
    this.settings.setDescription("Force-skips a player.");
    this.settings.setEmbedColor(BotConstants.MOD_CMD_COLOR);
    this.settings.setAuthorPerms(Permission.MANAGE_CHANNEL);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {

  }

}
