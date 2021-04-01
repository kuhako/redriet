package com.riskrieg.bot.util;

import com.riskrieg.bot.core.preference.Preference;
import com.riskrieg.bot.core.preference.Preferences;
import com.riskrieg.player.Player;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;

public class PreferenceUtil {

  public static void sendPingIfEnabled(Guild guild, Player player, MessageChannel channel) {
    if (!player.isComputer()) {
      Optional<Preference> optPref = Preferences.retrievePreference("pingOnTurn", guild.getId());
      if (optPref.isPresent() && optPref.get().isEnabled()) {
        guild.retrieveMemberById(player.getID()).queue(member -> channel.sendMessage(member.getAsMention() + " it is your turn.").queue());
      }
    }
  }

}
