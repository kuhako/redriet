package com.riskrieg.bot.core.commands.moderation;

import com.aaronjyoder.util.json.gson.GsonUtil;
import com.google.gson.reflect.TypeToken;
import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.constant.Constants;
import com.riskrieg.map.MapInfo;
import com.riskrieg.map.MapStatus;
import java.lang.reflect.Type;
import java.util.HashSet;

public class SetMapStatus extends Command {

  public SetMapStatus() {
    this.settings.setAliases("mapstatus", "map-status");
    this.settings.setDescription("Sets a " + Constants.NAME + " map's release status.");
    this.settings.setEmbedColor(BotConstants.MOD_CMD_COLOR);
    this.settings.setGuildOnly(true);
  }

  protected void execute(MessageInput input) {
    if (input.event().getGuild().getId().equals("699410244857757696") && input.event().getMember().getRoles().stream()
        .anyMatch((role) -> role.getId().equals("714159616103153665"))) {
      if (input.args().length == 2) {
        Type type = (new TypeToken<HashSet<String>>() {
        }).getType();
        HashSet<String> maps = GsonUtil.read(Constants.AVAILABLE_MAPS, type);
        String name = input.arg(0).trim();
        String status = input.arg(1).toLowerCase().trim();
        if (maps.contains(name)) {
          MapInfo mapInfo = GsonUtil.read(Constants.MAP_PATH + name + "/" + name + ".json", MapInfo.class);
          if (mapInfo != null) {
            switch (status) {
              case "coming_soon" -> mapInfo.setStatus(MapStatus.COMING_SOON);
              case "available" -> mapInfo.setStatus(MapStatus.AVAILABLE);
              default -> {
                input.event().getChannel().sendMessage("Invalid status.").queue();
                return;
              }
            }
            GsonUtil.write(Constants.MAP_PATH + name + "/" + name + ".json", MapInfo.class, mapInfo);
            input.event().getChannel().sendMessage("Map status successfully changed to **" + mapInfo.status().toString() + "**.").queue();
          } else {
            input.event().getChannel().sendMessage("Could not find valid map files for **" + name + "**. Map was not changed.").queue();
          }
        } else {
          input.event().getChannel().sendMessage("Map not present in maps list. Please add it first.").queue();
        }
      } else {
        input.event().getChannel().sendMessage("Invalid arguments. Please provide a valid map name.").queue();
      }
    }

  }

}
