package com.riskrieg.bot.core.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReadyListener extends ListenerAdapter {

  @Override
  public void onReady(@NotNull ReadyEvent event) {
    System.out.println("[ReadyEvent] All systems ready.");
  }

}
