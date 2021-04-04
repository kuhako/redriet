package com.riskrieg.bot.core.input;

import java.util.Objects;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public final class MessageInput implements Input {

  private final MessageReceivedEvent event;
  private final String alias;
  private final String[] args;

  public MessageInput(MessageReceivedEvent event, String alias, String[] args) {
    this.event = event;
    this.alias = alias;
    this.args = args;
  }

  @Override
  public InputType type() {
    return InputType.MESSAGE;
  }

  public String arg(int i) {
    return args[i];
  }

  public String argString() {
    StringBuilder sb = new StringBuilder();
    for (String s : args) {
      sb.append(s).append(" ");
    }
    return sb.toString().trim();
  }

  public MessageReceivedEvent event() {
    return event;
  }

  public String alias() {
    return alias;
  }

  public String[] args() {
    return args;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != this.getClass())
      return false;
    var that = (MessageInput) obj;
    return Objects.equals(this.event, that.event) &&
        Objects.equals(this.alias, that.alias) &&
        Objects.equals(this.args, that.args);
  }

  @Override
  public int hashCode() {
    return Objects.hash(event, alias, args);
  }

  @Override
  public String toString() {
    return "MessageInput[" +
        "event=" + event + ", " +
        "alias=" + alias + ", " +
        "args=" + args + ']';
  }


}
