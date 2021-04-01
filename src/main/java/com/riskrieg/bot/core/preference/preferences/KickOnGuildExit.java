package com.riskrieg.bot.core.preference.preferences;

import com.riskrieg.bot.core.preference.Preference;
import com.riskrieg.constant.Constants;

public class KickOnGuildExit implements Preference {

  private final String name;
  private final String description;
  private boolean enabled;
  private boolean deprecated;

  public KickOnGuildExit() {
    this.name = "kickOnGuildExit";
    this.description = "Remove a player from any " + Constants.NAME + " games if they exit the server.";
    this.enabled = true;
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
