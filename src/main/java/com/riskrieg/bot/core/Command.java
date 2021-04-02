package com.riskrieg.bot.core;

import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;

public abstract class Command {

  protected final CommandSettings settings = new CommandSettings();

  protected abstract void execute(SlashInput input);

  protected abstract void execute(MessageInput input);

  public CommandSettings settings() {
    return settings;
  }

}