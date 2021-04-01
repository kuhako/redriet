package com.riskrieg.bot.core.commands.general;

import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import net.dv8tion.jda.api.EmbedBuilder;

public class Invite extends Command {

  public Invite() {
    this.settings.setAliases("invite", "invites", "server", "discord", "discord-server", "discordserver", "helpserver", "officialserver");
    this.settings.setDescription("Sends all of the different kinds of invites.");
    this.settings.setEmbedColor(BotConstants.GENERIC_CMD_COLOR);
  }

  protected void execute(MessageInput input) {
    StringBuilder botInv = new StringBuilder();
    botInv.append("You can invite the bot directly by using [this invite link](").append(BotConstants.botInviteStr).append(").").append("\n")
        .append("If you would like to help the bot reach more people, you can [vote for it on the Discord Server List](").append(BotConstants.dslBotStr).append(").").append("\n");
    StringBuilder serverInv = new StringBuilder();
    serverInv.append("You can join the official Riskrieg Discord server [at this link](").append(BotConstants.serverInviteStr).append(").").append("\n")
        .append("If you would like to help the official server grow, you can [vote for it on the Discord Server List](").append(BotConstants.dslServerStr).append(").")
        .append("\n");
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setTitle("Invites");
    embedBuilder.setDescription("All the invites you need are right here.");
    embedBuilder.addField("Bot Invite", botInv.toString(), false);
    embedBuilder.addField("Server Invite", serverInv.toString(), false);
    embedBuilder.setColor(this.settings.getEmbedColor());
    input.event().getChannel().sendMessage(embedBuilder.build()).queue();
  }

}
