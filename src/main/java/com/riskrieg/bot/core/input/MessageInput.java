package com.riskrieg.bot.core.input;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageInput implements Input {

  private final MessageReceivedEvent event;
  private final String commandAlias;
  private final String[] arguments;

  public MessageInput(MessageReceivedEvent event, String commandAlias, String[] arguments) {
    this.event = event;
    this.commandAlias = commandAlias;
    this.arguments = arguments;
  }

  @Override
  public InputType type() {
    return InputType.MESSAGE;
  }

  @Override
  public String alias() {
    return commandAlias;
  }

  public MessageReceivedEvent event() {
    return event;
  }

  public String[] args() {
    return arguments;
  }

  public String arg(int i) {
    return arguments[i];
  }

  public String argString() {
    StringBuilder sb = new StringBuilder();
    for (String s : arguments) {
      sb.append(s).append(" ");
    }
    return sb.toString().trim();
  }

}
