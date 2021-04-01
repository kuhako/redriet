package com.riskrieg.bot.core;

import com.riskrieg.bot.core.input.MessageInput;

public abstract class Command {

  protected final CommandSettings settings = new CommandSettings();

  protected abstract void execute(MessageInput input);

  public CommandSettings getSettings() {
    return settings;
  }

}