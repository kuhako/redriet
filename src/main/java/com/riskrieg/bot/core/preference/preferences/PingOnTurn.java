package com.riskrieg.bot.core.preference.preferences;

import com.riskrieg.bot.core.preference.Preference;

public class PingOnTurn implements Preference {

  private final String name;
  private final String description;
  private boolean enabled;
  private boolean deprecated;

  public PingOnTurn() {
    this.name = "pingOnTurn";
    this.description = "Pings a player when it is their turn.";
    this.enabled = false;
    this.deprecated = false;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public boolean isDeprecated() {
    return deprecated;
  }

  @Override
  public void setDeprecated(boolean outdated) {
    this.deprecated = outdated;
  }

}
