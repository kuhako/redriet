package com.riskrieg.bot.core.input.raw;

import com.riskrieg.bot.Main;
import com.riskrieg.bot.core.input.InputType;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public record RawMessageInput(MessageReceivedEvent event) implements RawInput {

  @Override
  public boolean isForBot() {
    String msg = event.getMessage().getContentRaw();
    boolean isBot = event.getAuthor().isBot();
    boolean isSelf = event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId());
    boolean hasPrefix = msg.startsWith(Main.bot.auth().prefix());
    // TODO: Change hasMention to be a bit more future-proof
    boolean hasMention = event.getMessage().isMentioned(event.getJDA().getSelfUser(), Message.MentionType.USER) && event.getMessage().getContentRaw().startsWith("<@!");
    boolean isPrivate = event.isFromType(ChannelType.PRIVATE);

    return (hasPrefix || hasMention || isPrivate) && !isBot && !isSelf;
  }

  @Override
  public InputType type() {
    return InputType.MESSAGE;
  }

}
