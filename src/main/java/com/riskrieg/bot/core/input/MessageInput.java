package com.riskrieg.bot.core.input;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public record MessageInput(MessageReceivedEvent event, String alias, String[] args) implements Input {

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

}
