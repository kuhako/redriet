package com.riskrieg.bot.util;

import com.riskrieg.bot.Main;
import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.preference.Preferences;
import com.riskrieg.gamemode.Game;
import com.riskrieg.gamemode.GameMode;
import com.riskrieg.gamerule.GameRule;
import com.riskrieg.player.PlayerColor;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class MessageUtil {

  public static String listPreferences(Preferences prefs) {
    StringBuilder sb = new StringBuilder();
    prefs.getPreferences().forEach(pref -> {
      if (!pref.isDeprecated()) {
        sb.append(pref.isEnabled() ? BotConstants.ENABLED_EMOJI : BotConstants.DISABLED_EMOJI)
            .append("`").append(pref.getName()).append("` | *").append(pref.getDescription()).append("*").append("\n");
      }
    });
    if (sb.isEmpty()) {
      sb.append("*There are currently no preferences available.*");
    }
    return sb.toString();
  }

  public static String listGameRules(Game game, boolean appendUsage) {
    StringBuilder sb = new StringBuilder();
    Set<GameRule> gameRules = game.getGameRules();
    if (gameRules == null || gameRules.size() == 0) {
      sb.append("*There are currently no available game rules for this game mode.*");
    } else {
      gameRules.forEach(gameRule -> {
        sb.append(gameRule.isEnabled() ? BotConstants.ENABLED_EMOJI : BotConstants.DISABLED_EMOJI).append(gameRule.getDisplayName());
        if (appendUsage) {
          sb.append(" | Usage: `").append(Main.bot.auth().prefix()).append("gamerule ").append(gameRule.getName()).append(" [y/n]`");
        }
        sb.append("\n");
      });
    }
    return sb.toString();
  }

  public static Optional<Boolean> parseEnable(String str) {
    if (str.equals("false") || str.equals("disabled") || str.equals("disable") || str.equals("d") || str.equals("no") || str.equals("n")) {
      return Optional.of(false);
    } else if (str.equals("true") || str.equals("enabled") || str.equals("enable") || str.equals("e") || str.equals("yes") || str.equals("y")) {
      return Optional.of(true);
    }
    return Optional.empty();
  }

  public static Optional<PlayerColor> parseColor(String requestedColor) {
    if (requestedColor == null || requestedColor.isEmpty()) {
      return Optional.empty();
    }
    AtomicReference<Optional<PlayerColor>> result = new AtomicReference<>(Optional.empty());
    Arrays.stream(PlayerColor.values()).forEach(color -> {
      int distance = LevenshteinDistance.getDefaultInstance().apply(requestedColor.toLowerCase().trim(), color.toString().toLowerCase());
      if (distance < 2) {
        result.set(Optional.of(color));
      }
    });
    return result.get();
  }

  public static Optional<GameMode> getClosestGameMode(String choice) {
    if (choice == null || choice.isEmpty()) {
      return Optional.empty();
    }
    AtomicReference<Optional<GameMode>> result = new AtomicReference<>(Optional.empty());
    Arrays.stream(GameMode.values()).forEach(gameMode -> {
      int distance = LevenshteinDistance.getDefaultInstance().apply(choice.toLowerCase().trim(), gameMode.toString().toLowerCase());
      if (distance < 3) {
        result.set(Optional.of(gameMode));
      }
    });
    return result.get();
  }

}
