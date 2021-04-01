package com.riskrieg.bot.core.input.raw;

import com.riskrieg.bot.core.input.InputType;
import net.dv8tion.jda.api.events.GenericEvent;

public class RawSlashInput implements RawInput {

  private final GenericEvent event; // TODO: SlashCommandEvent

  public RawSlashInput(GenericEvent event) {
    this.event = event;
  }

  @Override
  public boolean isForBot() {
    return true;
  }

  @Override
  public InputType type() {
    return InputType.SLASH;
  }

  @Override
  public GenericEvent event() {
    return event;
  }

}
