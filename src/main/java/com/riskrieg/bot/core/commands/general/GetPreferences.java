package com.riskrieg.bot.core.commands.general;

import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.bot.core.preference.Preferences;
import com.riskrieg.bot.util.MessageUtil;
import com.riskrieg.constant.Constants;
import net.dv8tion.jda.api.EmbedBuilder;

public class GetPreferences extends Command {

  public GetPreferences() {
    this.settings.setAliases("prefs", "pref", "getprefs", "getpref", "getpreferences", "getpreference");
    this.settings.setDescription("Gets bot preferences.");
    this.settings.setGuildOnly(true);
    this.settings.setEmbedColor(BotConstants.GENERIC_CMD_COLOR);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) { // TODO: Create a command that lets me deprecate a preference and have it affect all preference saves.
    Preferences prefs = Preferences.load(input.event().getGuild().getId());
    EmbedBuilder embedBuilder = settings.embedBuilder();
    embedBuilder.setTitle(Constants.NAME + " Bot Preferences");
    embedBuilder.setDescription(MessageUtil.listPreferences(prefs));
    embedBuilder.setFooter(Constants.NAME + " " + Constants.VERSION);
    input.event().getChannel().sendMessage(embedBuilder.build()).queue();
    prefs.save();
  }

}
