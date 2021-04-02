package com.riskrieg.bot.core.listeners;

import com.riskrieg.api.Riskrieg;
import com.riskrieg.bot.core.preference.Preference;
import com.riskrieg.bot.core.preference.Preferences;
import com.riskrieg.gamemode.Game;
import com.riskrieg.player.Player;
import java.util.Optional;
import java.util.Set;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildListener extends ListenerAdapter {

  @Override
  public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
    Guild guild = event.getGuild();
    Optional<Preference> optPref = Preferences.retrievePreference("kickOnGuildExit", guild.getId());
    if (optPref.isPresent() && optPref.get().isEnabled()) {
      System.out.println(event.getUser().getId()); // Testing
      Riskrieg api = new Riskrieg();
      Set<Game> saves = api.loadSaves(guild.getId());
      for (Game game : saves) { // TODO: Notify in the appropriate channel if at all possible.
        Optional<Player> optPlayer = game.getPlayer(event.getUser().getId());
        System.out.println(optPlayer.isPresent()); // Testing
        optPlayer.ifPresent(player -> game.kick(player.getID()));
      }
    }
  }

  @Override
  public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
    Guild guild = event.getGuild();
    Riskrieg api = new Riskrieg();
    Set<Game> saves = api.loadSaves(guild.getId());
    for (Game game : saves) { // Update their name in all games they are a player in.
      event.getGuild().retrieveMember(event.getUser()).queue(member -> {
        Optional<Player> optPlayer = game.getPlayer(member.getId());
        if (optPlayer.isPresent()) {
          game.updatePlayerName(member.getId(), event.getNewNickname() == null ? member.getEffectiveName() : event.getNewNickname());
          api.save(game, guild.getId());
        }
      });
    }
  }

  @Override
  public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) { // Give @Updates role
    Guild guild = event.getGuild();
    if (isRiskriegUpdateRole(guild, event.getChannel(), event.getMessageId(), event.getReactionEmote())) {
      event.retrieveMember().queue(member -> {
        Role updateRole = guild.getRoleById("758772899585720360");
        if (updateRole != null && !member.getRoles().contains(updateRole)) {
          guild.addRoleToMember(member.getId(), updateRole).queue();
        }
      });
    }
  }

  @Override
  public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) { // Remove @Updates role
    Guild guild = event.getGuild();
    if (isRiskriegUpdateRole(guild, event.getChannel(), event.getMessageId(), event.getReactionEmote())) {
      event.retrieveMember().queue(member -> {
        Role updateRole = guild.getRoleById("758772899585720360");
        if (updateRole != null && member.getRoles().contains(updateRole)) {
          guild.removeRoleFromMember(member.getId(), updateRole).queue();
        }
      });
    }
  }

  private boolean isRiskriegUpdateRole(Guild guild, TextChannel channel, String messageID, ReactionEmote reactionEmote) {
    // Riskrieg server, #rules channel, message in #rules.
    if (guild.getId().equals("699410244857757696") && channel.getId().equals("722231317567111189") && messageID.equals("827207360593264690")) {
      if (reactionEmote.isEmoji() && reactionEmote.getAsCodepoints().equals("U+1f514")) { // Notification bell
        return true;
      }
    }
    return false;
  }

}
