package com.riskrieg.bot.core.listeners;

import com.riskrieg.bot.Main;
import com.riskrieg.bot.core.input.raw.RawInput;
import com.riskrieg.bot.core.input.raw.RawMessageInput;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class MessageListener implements EventListener {

  @Override
  public void onEvent(@NotNull GenericEvent event) { // TODO: Add slash command event parser when that is available.
    RawInput input;
    if (event instanceof MessageReceivedEvent) {
      MessageReceivedEvent mrEvent = (MessageReceivedEvent) event; // TODO: Use pattern matching in Java 16
      input = new RawMessageInput(mrEvent);
    } else {
      input = null;
    }
    if (input != null && input.isForBot()) {
      new Thread(() -> Main.bot.getCommandHandler().process(input)).start();
    }
  }

}
