package com.riskrieg.bot.core.commands.moderation;

import com.aaronjyoder.util.json.moshi.MoshiUtil;
import com.google.gson.reflect.TypeToken;
import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.constant.Constants;
import java.lang.reflect.Type;
import java.util.HashSet;

public class RemoveMap extends Command {

  public RemoveMap() {
    this.settings.setAliases("removemap");
    this.settings.setDescription("Removes a " + Constants.NAME + " map.");
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
        if (maps.contains(name)) {
          maps.remove(name);
          MoshiUtil.write(Constants.AVAILABLE_MAPS, type, maps);
          input.event().getChannel().sendMessage("Map successfully removed.").queue();
        } else {
          input.event().getChannel().sendMessage("Map **" + name + "** not present in maps list, so there was nothing to remove.").queue();
        }
      } else {
        input.event().getChannel().sendMessage("Invalid arguments. Please provide a valid map name.").queue();
      }
    }
  }

}
