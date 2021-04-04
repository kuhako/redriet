package com.riskrieg.bot.core.commands.moderation;

import com.aaronjyoder.util.json.gson.GsonUtil;
import com.google.gson.reflect.TypeToken;
import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.constant.Constants;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;

public class Stats extends Command {

  public Stats() {
    this.settings.setAliases("stats", "status");
    this.settings.setDescription("Gets information about how the bot is doing.");
    this.settings.setEmbedColor(BotConstants.MOD_CMD_COLOR);
    this.settings.setOwnerCommand(true);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {
    MessageBuilder mb = new MessageBuilder();
    EmbedBuilder eb = new EmbedBuilder();
    int shardCount = input.event().getJDA().getShardManager().getShards().size();
    int guildCount = 0;
    int estMemberCount = 0;
    int cachedMemberCount = 0;

    for (JDA jda : input.event().getJDA().getShardManager().getShards()) {
      guildCount += jda.getGuildCache().size();
      for (Guild guild : jda.getGuildCache().stream().toList()) {
        cachedMemberCount += guild.getMemberCache().size();
        estMemberCount += guild.getMemberCount();
      }
    }

    long saves;
    try {
      saves = this.fileCount((new File(Constants.SAVE_PATH)).toPath());
    } catch (IOException e) {
      saves = -1L;
    }

    long maps;
    try {
      Type type = (new TypeToken<HashSet<String>>() {
      }).getType();
      HashSet<String> availableMaps = GsonUtil.read(Constants.AVAILABLE_MAPS, type);
      maps = availableMaps.size();
    } catch (Exception e) {
      e.printStackTrace();
      maps = -1L;
    }

    eb.setColor(this.settings.getEmbedColor());
    eb.setTitle("Stats");
    eb.addField("Total Shards", Integer.toString(shardCount), true);
    eb.addField("Total Guilds", Integer.toString(guildCount), true);
    eb.addField("Est. Total Members", Integer.toString(estMemberCount), true);
    eb.addField("Members Cached", Integer.toString(cachedMemberCount), true);
    eb.addField("Total Maps", Long.toString(maps), true);
    eb.addField("Total Saves", Long.toString(saves), true);
    eb.addField("Ping", Long.toString(input.event().getJDA().getRestPing().complete()), false);
    eb.addField("Bot Version", BotConstants.VERSION, true);
    eb.addField(Constants.NAME + " Version", Constants.VERSION, true);
    mb.setEmbed(eb.build());
    input.event().getChannel().sendMessage(mb.build()).queue();
  }

  public long fileCount(Path dir) throws IOException {
    return Files.walk(dir)
        .parallel()
        .filter(p -> !p.toFile().isDirectory())
        .count();
  }

}
