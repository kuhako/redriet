package com.riskrieg.bot.core.commands.general;

import com.riskrieg.bot.Main;
import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.constant.Colors;
import com.riskrieg.constant.Constants;
import com.riskrieg.gamemode.GameMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Help extends Command {

  public Help() {
    this.settings.setAliases("help", "info", "instructions");
    this.settings.setDescription("Use this command to get help.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
  }

  protected void execute(MessageInput input) {
    String prefix = Main.bot.getPrefix();

    final MessageEmbed embed;
    if (input.args().length == 1) {
      embed = switch (input.argString().toLowerCase().trim()) {
        case "conquest" -> conquestHelp(prefix);
        case "creative" -> creativeHelp(prefix);
        default -> generalHelp(prefix);
      };
    } else {
      embed = generalHelp(prefix);
    }

    input.event().getChannel().sendMessage(embed).queue();
  }

  private MessageEmbed generalHelp(String prefix) {
    EmbedBuilder embedBuilder = getBaseEmbedBuilder();
    embedBuilder.setTitle("Help | General");

    StringBuilder description = new StringBuilder();
    description.append(Constants.NAME + " is a game that lets you simulate wars, battles, alternate history, and more, all through Discord!").append("\s");
    description.append("You can use `").append(prefix).append("help [gamemode]` to see the help pages for each game mode. A list of all game modes is shown below.").append("\n");
    description.append("If you need further help, you can join the official " + Constants.NAME + " server by using the `").append(prefix).append("invite` command.").append("\n\n");
    description.append("To see a list of general commands, use the `").append(prefix)
        .append("commands` command. You can also supply a game mode in order to see commands for that specific game mode.");

    // Adding all of the game modes.
    StringBuilder desc = new StringBuilder();
    for (GameMode gameMode : GameMode.values()) {
      desc.append("**").append(gameMode.getName()).append("** | *").append(gameMode.getDescription()).append("*").append("\n");
    }
    embedBuilder.addField("Game Modes", desc.toString(), false);

    embedBuilder.setDescription(description.toString());
    return embedBuilder.build();
  }

  private MessageEmbed conquestHelp(String prefix) {
    EmbedBuilder embedBuilder = getBaseEmbedBuilder();
    embedBuilder.setTitle("Help | Conquest");

    StringBuilder description = new StringBuilder();
    description.append("Use `").append(prefix).append("commands conquest` to see a full list of commands for this game mode.").append("\n\n");
    description.append("This game mode has two phases: the setup phase, and the active phase.");

    StringBuilder setup = new StringBuilder();
    setup.append("During the setup phase, players may join, select a map, select their capitals, and change the game rules.").append("\s");
    setup.append("Maps can be changed at will, capitals can be moved freely, and players can join and leave as they wish.").append("\s");
    setup.append("Once everyone has settled on the game rules, a map, and their capitals, the game may be started, moving the game to the active phase.");

    NumberFormat claimFormat = new DecimalFormat("#0");

    StringBuilder active = new StringBuilder();
    active.append("Once the game has started, each player takes their turn.").append("\s");
    active.append("On their turn, a player may claim any territories they are neighboring.").append("\s");
    active.append("All players start with " + Constants.MINIMUM_CLAIM_AMOUNT + " claim.").append("\s");
    active.append("Players gain +1 claim for every ").append(claimFormat.format(Constants.CLAIM_INCREASE_THRESHOLD)).append(" territories they own.").append("\n\n");

    active.append("When attacking a territory, the following occurs:").append("\s");
    active.append("the attacker gets a number of attack dice equal to the number of their own territories bordering the territory they are attacking.").append("\s");
    active.append("The defender gets a number of defense dice equal to the number of *their* own territories bordering the territory they are defending.").append("\n\n");

    active.append("If a capital territory is attacking, it gets +" + Constants.CAPITAL_ATTACK_ROLL_BOOST + " attack dice.").append("\n");
    active.append("If a capital territory is defending, it gets +" + Constants.CAPITAL_DEFENSE_ROLL_BOOST + " defense dice.");

    StringBuilder other = new StringBuilder();
    other.append("Some things can be done at any time, such as forming or breaking alliances.").append("\s");
    other.append("Alliances can only be formed or broken if the alliance game rule is turned on.").append("\s");
    other.append("Furthermore, players may not attack their own allies.").append("\n\n");
    other.append("The game ends if only one player is left, or if nobody can claim anything due to alliances formed.").append("\s");
    other.append("The game can also be force-ended by a moderator.").append("\n\n");

    embedBuilder.addField("Setup Phase", setup.toString(), false);
    embedBuilder.addField("Active Phase", active.toString(), false);
    embedBuilder.addField("Other Information", other.toString(), false);

    embedBuilder.setDescription(description.toString());
    return embedBuilder.build();
  }

  private MessageEmbed creativeHelp(String prefix) {
    EmbedBuilder embedBuilder = getBaseEmbedBuilder();
    embedBuilder.setTitle("Help | Creative");

    StringBuilder description = new StringBuilder();
    description.append("Use `").append(prefix).append("commands creative` to see a full list of commands for this game mode.").append("\n\n");
    description.append("This is the most free-form game mode.").append("\s");
    description.append("It is designed for those who wish to role-play, simulate their alternate histories, or run D&D campaigns with ultimate flexibility.").append("\s");
    description.append("There are no rules and no game phases; the one who creates the structure for the game is the Dungeon Master.").append("\n\n");
    description.append("The first player to join the game after it is created is deemed the Dungeon Master, or DM.").append("\s");
    description.append("The DM has full control over this game mode and is the only one who can use most of the commands.").append("\s");
    description.append("The DM has the ability to grant and revoke territories, add dummy players, change maps, and remove players.").append("\s");
    description.append("Other players can join and leave the game freely as long as there is room on the map.");

    embedBuilder.setDescription(description.toString());
    return embedBuilder.build();
  }

  private EmbedBuilder getBaseEmbedBuilder() {
    EmbedBuilder embedBuilder = settings.embedBuilder();
    embedBuilder.setFooter(Constants.NAME + " v" + Constants.VERSION);
    return embedBuilder;
  }

}
