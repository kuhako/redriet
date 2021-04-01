package com.riskrieg.bot.core.input;

import net.dv8tion.jda.api.events.GenericEvent;

public class SlashInput implements Input {

  private final GenericEvent event;
  private final String alias;

  public SlashInput(GenericEvent event, String alias) { // TODO: SlashCommandEvent
    this.event = event;
    this.alias = alias;
  }

  @Override
  public InputType type() {
    return InputType.SLASH;
  }

  @Override
  public String alias() {
    return alias;
  }

  public GenericEvent event() {
    return event;
  }

}
