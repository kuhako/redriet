package com.riskrieg.bot.core.preference;

import com.aaronjyoder.util.json.moshi.MoshiUtil;
import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.preference.preferences.KickOnGuildExit;
import com.riskrieg.bot.core.preference.preferences.PingOnTurn;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Preferences {

  private String guildID;
  private Set<Preference> preferences;

  public Preferences(String guildID) { // TODO: Have a way to remove outdated preferences.
    this.guildID = guildID;
    this.preferences = new HashSet<>();
    preferences.add(new KickOnGuildExit());
    preferences.add(new PingOnTurn());
  }

  public static Preferences load(String guildID) {
    try {
      Preferences prefs = MoshiUtil.read(BotConstants.PREF_PATH + guildID + ".json", Preferences.class);
      if (prefs == null) {
        return new Preferences(guildID);
      }
      Preferences updatedPrefs = new Preferences(guildID);
      if (updatedPrefs.getPreferences().size() > prefs.getPreferences().size()) {
        for (Preference preference : prefs.getPreferences()) {
          updatedPrefs.setPreference(preference.getName(), preference.isEnabled());
        }
        return updatedPrefs;
      }
      return prefs;
    } catch (Exception e) {
      return new Preferences(guildID);
    }
  }

  public static Optional<Preference> retrievePreference(String name, String guildID) {
    return load(guildID).getPreference(name);
  }

  public void save() {
    MoshiUtil.write(BotConstants.PREF_PATH + this.guildID + ".json", Preferences.class, this);
  }

  public Set<Preference> getPreferences() {
    return preferences;
  }

  public Optional<Preference> getPreference(String name) {
    return preferences.stream().filter(preference -> preference.getName().equalsIgnoreCase(name)).findFirst();
  }

  public boolean setPreference(String name, boolean enabled) {
    Optional<Preference> optPref = getPreference(name);
    if (optPref.isPresent()) {
      optPref.get().setEnabled(enabled);
      save();
      return true;
    }
    return false;
  }

  public boolean deprecatePreference(String name, boolean deprecated) {
    Optional<Preference> optPref = getPreference(name);
    if (optPref.isPresent()) {
      optPref.get().setDeprecated(deprecated);
      save();
      return true;
    }
    return false;
  }

}
