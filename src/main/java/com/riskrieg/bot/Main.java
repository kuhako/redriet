package com.riskrieg.bot;

import com.aaronjyoder.util.json.adapters.RuntimeTypeAdapterFactory;
import com.aaronjyoder.util.json.gson.GsonUtil;
import com.riskrieg.bot.core.Bot;
import com.riskrieg.bot.core.listeners.GuildListener;
import com.riskrieg.bot.core.listeners.MessageListener;
import com.riskrieg.bot.core.listeners.ReadyListener;
import com.riskrieg.bot.core.preference.Preference;
import com.riskrieg.bot.core.preference.preferences.KickOnGuildExit;
import com.riskrieg.bot.core.preference.preferences.PingOnTurn;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;

public class Main {

  public static final Bot bot = new Bot();

  public static void main(String[] args) {
    GsonUtil.register(RuntimeTypeAdapterFactory.of(Preference.class).with(KickOnGuildExit.class).with(PingOnTurn.class));
    registerFonts();
    try {
      bot.start(new ReadyListener(), new MessageListener(), new GuildListener());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void registerFonts() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    try {
      // Spectral
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/font/spectral/Spectral-Regular.ttf")));
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/font/spectral/Spectral-Italic.ttf")));
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/font/spectral/Spectral-Bold.ttf")));
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/font/spectral/Spectral-Light.ttf")));
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/font/spectral/Spectral-Medium.ttf")));

      // Noto Serif
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/font/noto-serif/NotoSerif-Regular.ttf")));

      // Noto Color Emoji
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/font/noto-color-emoji/NotoColorEmoji.ttf")));

      // Noto Emoji
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/font/noto-emoji/NotoEmoji-Regular.ttf")));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
