package com.riskrieg.bot.core.commands.moderation;

import com.aaronjyoder.util.json.moshi.MoshiUtil;
import com.google.gson.reflect.TypeToken;
import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.constant.Constants;
import com.riskrieg.map.MapInfo;
import java.lang.reflect.Type;
import java.util.HashSet;

public class AddMap extends Command {

  public AddMap() {
    this.settings.setAliases("addmap");
    this.settings.setDescription("Adds a " + Constants.NAME + " map.");
    this.settings.setEmbedColor(BotConstants.MOD_CMD_COLOR);
    this.settings.setGuildOnly(true);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {
    if (input.event().getGuild().getId().equals("699410244857757696") && input.event().getMember().getRoles().stream()
        .anyMatch((role) -> role.getId().equals("714159616103153665"))) {
      if (input.args().length == 1) {
        Type type = (new TypeToken<HashSet<String>>() {
        }).getType();
        HashSet<String> maps = MoshiUtil.read(Constants.AVAILABLE_MAPS, type);
        String name = input.arg(0).trim();
        MapInfo mapInfo = MoshiUtil.read(Constants.MAP_PATH + name + "/" + name + ".json", MapInfo.class);
        if (mapInfo != null && !maps.contains(name)) {
          maps.add(name);
          MoshiUtil.write(Constants.AVAILABLE_MAPS, type, maps);
          input.event().getChannel().sendMessage("Map successfully added.").queue();
        } else if (maps.contains(name)) {
          input.event().getChannel().sendMessage("Map already present in maps list.").queue();
        } else {
          input.event().getChannel().sendMessage("Could not find valid map files for **" + name + "**. Map was not added.").queue();
        }
      } else {
        input.event().getChannel().sendMessage("Invalid arguments. Please provide a valid map name.").queue();
      }
    }

  }

}
