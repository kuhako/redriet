package com.riskrieg.bot.core.commands.general;

import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import java.time.Instant;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Feedback extends Command {

  public Feedback() {
    this.settings.setAliases("feedback");
    this.settings.setDescription("Sends feedback to the Riskrieg server.");
    this.settings.setEmbedColor(BotConstants.GENERIC_CMD_COLOR);
    this.settings.setGuildOnly(true);
  }

  @Override
  protected void execute(MessageInput input) {
    if (input.argString() != null && !input.argString().isEmpty()) {
      Member member = input.event().getMember();
      EmbedBuilder embedBuilder = new EmbedBuilder();
      embedBuilder.setAuthor(member.getEffectiveName(), null, member.getUser().getEffectiveAvatarUrl());
      embedBuilder.setColor(member.getColor());
      embedBuilder.setDescription(input.argString());
      embedBuilder.setTimestamp(Instant.now());
      StringBuilder infoSb = new StringBuilder();
      infoSb.append("User ID: ").append(member.getId());
      embedBuilder.addField("Information", infoSb.toString(), false);

      EmbedBuilder response = new EmbedBuilder();
      response.setColor(settings.getEmbedColor());
      response.setTitle("Feedback Response");
      response.setTimestamp(Instant.now());

      Guild riskrieg = input.event().getJDA().getGuildById("699410244857757696");
      if (riskrieg != null) {
        TextChannel feedback = riskrieg.getTextChannelById("812899431199932427");
        if (feedback != null) {
          feedback.sendMessage(embedBuilder.build()).queue(
              success -> {
                response.setDescription("Your feedback has been received successfully.");
                input.event().getChannel().sendMessage(response.build()).queue();
              },
              failure -> {
                response.setDescription("Your feedback could not be received due to a technical error.");
                input.event().getChannel().sendMessage(response.build()).queue();
              });
        }
      }
    }
  }

}
