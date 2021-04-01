package com.riskrieg.bot.core.preference;

public interface Preference {

  String getName();

  String getDescription();

  boolean isEnabled();

  void setEnabled(boolean enabled);

  boolean isDeprecated();

  void setDeprecated(boolean deprecated);

}
