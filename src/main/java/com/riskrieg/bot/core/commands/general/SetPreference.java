package com.riskrieg.bot.core.commands.general;

import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.preference.Preferences;
import com.riskrieg.bot.util.Error;
import com.riskrieg.bot.util.MessageUtil;
import com.riskrieg.constant.Constants;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;

public class SetPreference extends Command {

  public SetPreference() {
    this.settings.setAliases("setpref", "setprefs", "setpreference", "setpreferences");
    this.settings.setDescription("Sets bot preferences.");
    this.settings.setGuildOnly(true);
    this.settings.setEmbedColor(BotConstants.GENERIC_CMD_COLOR);
  }

  protected void execute(MessageInput input) {
    if (input.args().length == 2) {
      Preferences newPrefs = Preferences.load(input.event().getGuild().getId());
      Optional<Boolean> optEnable = MessageUtil.parseEnable(input.arg(1).toLowerCase().trim());
      if (optEnable.isPresent()) {
        boolean success = newPrefs.setPreference(input.arg(0).trim(), optEnable.get());
        if (success) {
          EmbedBuilder embedBuilder = new EmbedBuilder();
          embedBuilder.setColor(this.settings.getEmbedColor());
          embedBuilder.setTitle(Constants.NAME + " Bot Preferences Updated");
          embedBuilder.setDescription(MessageUtil.listPreferences(newPrefs));
          embedBuilder.setFooter(Constants.NAME + " " + Constants.VERSION);
          input.event().getChannel().sendMessage(embedBuilder.build()).queue();
          newPrefs.save();
        } else {
          input.event().getChannel().sendMessage(Error.create("Invalid preference. You must type the preference name correctly.", this.settings)).queue();
        }
      } else {
        input.event().getChannel().sendMessage(Error.create("Invalid syntax. | Usage: `" + this.settings.getName() + " [preference] [y/n]`", this.settings)).queue();
      }
    } else {
      input.event().getChannel().sendMessage(Error.create("Invalid syntax. | Usage: `" + this.settings.getName() + " [preference] [y/n]`", this.settings)).queue();
    }
  }

}