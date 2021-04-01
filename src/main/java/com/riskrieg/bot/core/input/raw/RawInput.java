package com.riskrieg.bot.core.input.raw;

import com.riskrieg.bot.core.input.InputType;
import net.dv8tion.jda.api.events.GenericEvent;

public interface RawInput {

  boolean isForBot();

  InputType type();

  GenericEvent event();

}
