package com.riskrieg.bot.core.input.raw;

import com.riskrieg.bot.Main;
import com.riskrieg.bot.core.input.InputType;
import java.util.Objects;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public final class RawMessageInput implements RawInput {

  private final MessageReceivedEvent event;

  public RawMessageInput(MessageReceivedEvent event) {
    this.event = event;
  }

  @Override
  public boolean isForBot() {
    String msg = event.getMessage().getContentRaw();
    boolean isBot = event.getAuthor().isBot();
    boolean isSelf = event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId());
    boolean hasPrefix = msg.startsWith(Main.bot.auth().prefix());
    // TODO: Change hasMention to be a bit more future-proof
    boolean hasMention = event.getMessage().isMentioned(event.getJDA().getSelfUser(), MentionType.USER) && event.getMessage().getContentRaw().startsWith("<@!");
    boolean isPrivate = event.isFromType(ChannelType.PRIVATE);

    return (hasPrefix || hasMention || isPrivate) && !isBot && !isSelf;
  }

  @Override
  public InputType type() {
    return InputType.MESSAGE;
  }

  public MessageReceivedEvent event() {
    return event;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != this.getClass())
      return false;
    var that = (RawMessageInput) obj;
    return Objects.equals(this.event, that.event);
  }

  @Override
  public int hashCode() {
    return Objects.hash(event);
  }

  @Override
  public String toString() {
    return "RawMessageInput[" +
        "event=" + event + ']';
  }


}
