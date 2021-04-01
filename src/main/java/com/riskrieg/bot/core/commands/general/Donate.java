package com.riskrieg.bot.core.commands.general;

import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import net.dv8tion.jda.api.EmbedBuilder;

public class Donate extends Command {

  public Donate() {
    this.settings.setAliases("donate", "support", "patreon");
    this.settings.setDescription("Gives you a link to donate to the bot creator.");
    this.settings.setEmbedColor(BotConstants.GENERIC_CMD_COLOR);
  }

  protected void execute(MessageInput input) {
    StringBuilder sb = new StringBuilder();
    String donationLink = "https://www.paypal.me/aaronjyoder";
    sb.append("You can support me either by [donating directly right here](").append(donationLink).append("), or by following my [Patreon](https://www.patreon.com/svetroid).");
    sb.append("\n\n");
    sb.append("Donations help to keep updates stable by allowing me to dedicate more time to working on the game. It also contributes to hosting costs and hosting stability.");
    sb.append(" This game is only possible to keep going with small donations from people like you. Every little bit helps.");
    sb.append("\n\n");
    sb.append("Please consider donating (if you can) to show your support!");
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setTitle("Donations");
    embedBuilder.setDescription(sb.toString());
    embedBuilder.setColor(this.settings.getEmbedColor());
    input.event().getChannel().sendMessage(embedBuilder.build()).queue();
  }

}
