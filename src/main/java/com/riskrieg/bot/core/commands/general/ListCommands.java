package com.riskrieg.bot.core.commands.general;

import com.riskrieg.bot.Main;
import com.riskrieg.bot.constant.BotConstants;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.constant.Constants;
import com.riskrieg.gamemode.GameMode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ListCommands extends Command {

  public ListCommands() {
    this.settings.setAliases("commands", "commandlist", "command", "cmd", "cmds");
    this.settings.setDescription("Displays a list of all bot commands.");
    this.settings.setEmbedColor(BotConstants.GENERIC_CMD_COLOR);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {
    String prefix = Main.bot.auth().prefix();

    final MessageEmbed embed;
    if (input.args().length == 1) {
      embed = switch (input.argString().toLowerCase().trim()) {
        case "conquest" -> conquestCommands(prefix);
        case "creative" -> creativeCommands(prefix);
        default -> generalCommands(prefix);
      };
    } else {
      embed = generalCommands(prefix);
    }

    input.event().getChannel().sendMessage(embed).queue();
  }

  private MessageEmbed generalCommands(String prefix) {
    EmbedBuilder embedBuilder = getBaseEmbedBuilder();
    embedBuilder.setTitle("Commands | General");

    StringBuilder description = new StringBuilder();
    description.append("You can use `").append(prefix).append("commands [gamemode]` to see the commands for each game mode. A list of all game modes is shown below.")
        .append("\n\n");

    // Adding all of the game modes.
    StringBuilder desc = new StringBuilder();
    for (GameMode gameMode : GameMode.values()) {
      desc.append("**").append(gameMode.getName()).append("** | *").append(gameMode.getDescription()).append("*").append("\n");
    }
    embedBuilder.addField("Game Modes", desc.toString(), false);

    StringBuilder general = new StringBuilder();
    general.append("`").append(prefix).append("help` | *Shows you how to play the game.*").append("\n");
    general.append("`").append(prefix).append("commands` | *Lists available commands.*").append("\n");
    general.append("`").append(prefix).append("gamemodes` | *Lists available game modes.*").append("\n");
    general.append("`").append(prefix).append("prefs` | *Lists available bot preferences.*").append("\n");
    general.append("`").append(prefix).append("setpref [preference] [y/n]` | *Allows you to enable/disable specific bot preferences.*").append("\n");
    general.append("`").append(prefix).append("invite` | *Displays the bot invite and the official server invite.*").append("\n");
    general.append("`").append(prefix).append("bugreport` | *Generates information needed for a bug report.*").append("\n");
    general.append("`").append(prefix).append("feedback [text]` | *Sends feedback to the official " + Constants.NAME + " Discord server.*").append("\n");
    general.append("`").append(prefix).append("donate` | *Displays information on how to support the creator.*").append("\n");

    StringBuilder anyMode = new StringBuilder();
    anyMode.append("`").append(prefix).append("create [gamemode]` | *Creates a new game in the current channel.*").append("\n");
    anyMode.append("`").append(prefix).append("gamerules` | *Lists available game rules.*").append("\n");
    anyMode.append("`").append(prefix).append("maps` | *Lists all game maps.*").append("\n");
    anyMode.append("`").append(prefix).append("leave` | *Leaves a game if you are in one.*").append("\n");
    anyMode.append("`").append(prefix).append("kick [@mention/color]` | *Kicks a player from the game. Requires Discord 'kick' permission.*").append("\n");
    anyMode.append("`").append(prefix).append("end` | *Forcibly ends the game. Requires the Discord 'manage channel' permission.*").append("\n");

    embedBuilder.addField("General", general.toString(), false);
    embedBuilder.addField("Any Mode", anyMode.toString(), false);

    embedBuilder.setDescription(description.toString());
    return embedBuilder.build();
  }

  private MessageEmbed conquestCommands(String prefix) {
    EmbedBuilder embedBuilder = getBaseEmbedBuilder();
    embedBuilder.setTitle("Commands | Conquest");

    StringBuilder setup = new StringBuilder();
    setup.append("`").append(prefix).append("join [color]` | *Joins the game with the selected color.*").append("\n");
    setup.append("`").append(prefix).append("gamerule [rule] [y/n]` | *Sets the specified game rule to the specified value.*").append("\n");
    setup.append("`").append(prefix).append("map [name]` | *Selects the specified map.*").append("\n");
    setup.append("`").append(prefix).append("capital [territory]` | *Grants you the specified capital territory on the map during the setup phase.*").append("\n");
    setup.append("`").append(prefix).append("play` | *Begins the game!*").append("\n");

    StringBuilder active = new StringBuilder();
    active.append("`").append(prefix).append("claim [territories]` | *Allows you to claim or attack territories.*").append("\n");
    active.append("`").append(prefix).append("ally [@mention/color]` | *Requests an alliance, or forms one, with another player.*").append("\n");
    active.append("`").append(prefix).append("unally [@mention/color]` | *Revokes an alliance request, or breaks an alliance, with another player.*").append("\n");
    active.append("`").append(prefix).append("allies` | *Shows you all of your current alliances.*").append("\n");
    active.append("`").append(prefix).append("skip` | *Allows you to skip your turn.*").append("\n");
    active.append("`").append(prefix).append("turn` | *Tells you whose turn it currently is.*").append("\n");

    embedBuilder.addField("Setup Phase", setup.toString(), false);
    embedBuilder.addField("Active Phase", active.toString(), false);

    return embedBuilder.build();
  }

  private MessageEmbed creativeCommands(String prefix) {
    EmbedBuilder embedBuilder = getBaseEmbedBuilder();
    embedBuilder.setTitle("Commands | Creative");

    StringBuilder any = new StringBuilder();
    any.append("`").append(prefix).append("join [color]` | *Joins the game with the selected color.*").append("\n");
    any.append("`").append(prefix).append("roll [query]` | *Parses the dice roll query and returns a result. Uses standard D&D syntax.*").append("\n");

    StringBuilder dmOnly = new StringBuilder();
    dmOnly.append("`").append(prefix).append("map [name]` | *Selects the specified map.*").append("\n");
    dmOnly.append("`").append(prefix).append("add [name]` | *Adds a dummy player to the game with the specified name.*").append("\n");
    dmOnly.append("`").append(prefix).append("grant [color] [territories]` | *Grants the specified player the specified territories.*").append("\n");
    dmOnly.append("`").append(prefix).append("revoke [color] [territories]` | *Grants the specified territories from the specified player.*").append("\n");
    dmOnly.append("`").append(prefix).append("remove [color]` | *Removes the specified player from the game.*").append("\n");

    embedBuilder.addField("Anyone", any.toString(), false);
    embedBuilder.addField("DM Only", dmOnly.toString(), false);

//    embedBuilder.setDescription(description.toString());
    return embedBuilder.build();
  }

  private EmbedBuilder getBaseEmbedBuilder() {
    EmbedBuilder embedBuilder = settings.embedBuilder();
    embedBuilder.setFooter(Constants.NAME + " v" + Constants.VERSION);
    return embedBuilder;
  }

}