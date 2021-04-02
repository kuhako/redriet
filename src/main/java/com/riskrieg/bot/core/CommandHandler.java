package com.riskrieg.bot.core;

import com.riskrieg.bot.Main;
import com.riskrieg.bot.core.input.Input;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.bot.core.input.raw.RawInput;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class CommandHandler {

  public static final List<Command> commands = new ArrayList<>();

  public CommandHandler() {
    try {
      Set<URL> classPathList = new HashSet<>(ClasspathHelper.forJavaClassPath());
      Set<Class<? extends Command>> result = new Reflections(
          new ConfigurationBuilder().setScanners(new SubTypesScanner()).setUrls(classPathList))
          .getSubTypesOf(Command.class);

      for (Class<? extends Command> c : result) {
        String[] categoryString = c.getPackage().toString().split("\\.");
        String category = categoryString[categoryString.length - 1];
        if (category.equalsIgnoreCase("commands")) {
          category = "default";
        }
        Command command = c.getDeclaredConstructor().newInstance();
        command.settings.setCategory(category);
        commands.add(command);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Parses the raw input, gets a command from the parsed input, and then executes the command if it exists.
   *
   * @param rawInput The raw input provided by the user.
   */
  public void process(RawInput rawInput) {
    Input input = parse(rawInput);
    Optional<Command> result = getCommand(input);
    switch (input.type()) {
      case SLASH -> result.ifPresent(cmd -> {
        if (canExecute(cmd, input)) {
          cmd.execute((SlashInput) input);
        }
      });
      case MESSAGE -> result.ifPresent(cmd -> {
        if (canExecute(cmd, input)) {
          try {
            cmd.execute((MessageInput) input);
          } catch (InsufficientPermissionException e) {
            ((MessageInput) input).event().getChannel().sendMessage(
                "**Insufficient Permissions**: __Embed Links__ permission required. This bot uses message embeds to send game updates, which requires that permission.\n\nIf you have granted that permission and still need help, join the official Riskrieg server for help.")
                .queue();
          }
        }
      });
    }
  }

  /**
   * Parses the command and returns a processed input object, or null if processing fails.
   *
   * @param rawInput The raw input from the user.
   * @return A more processed version of the input. This can be null.
   */
  private Input parse(RawInput rawInput) {
    return switch (rawInput.type()) {
      case MESSAGE -> {
        MessageReceivedEvent event = (MessageReceivedEvent) rawInput.event();

        String msg = event.getMessage().getContentRaw();

        boolean startsWithPrefix = msg.startsWith(Main.bot.auth().prefix());
        boolean startsWithMention = msg.startsWith("<@!" + event.getJDA().getSelfUser().getId() + ">");
        boolean isPrivateChannel = event.isFromType(ChannelType.PRIVATE);

        String[] args = null;

        if (startsWithPrefix) {
          args = msg.replaceFirst(Main.bot.auth().prefix(), "").trim().split("\\s+");
        } else if (startsWithMention) {
          args = msg.replaceFirst("<@!" + event.getJDA().getSelfUser().getId() + ">", "").trim().split("\\s+"); // TODO: Don't parse mentions like this
        } else if (isPrivateChannel) {
          args = msg.trim().split("\\s+");
        }

        if (args == null) {
          yield null;
        }

        String alias = args[0]; // Command alias should always be the first entry
        final String[] finalArgs = args.length == 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length); // Skip command alias, if there are no args set it as empty array

        yield new MessageInput(event, alias, finalArgs);
      }
      case SLASH -> { // TODO: Uncomment after having access to SlashCommandEvent
//        SlashCommandEvent event = (SlashCommandEvent) rawInput.event();
//        yield new SlashInput(event, event.getName());
        yield null;
      }
    };
  }

  /**
   * Uses the parsed input to find the associated command. Then it returns that command if it exists. If the input is null, it returns an empty Optional to ensure a command never
   * executes on a null input.
   *
   * @param input The parsed command input.
   * @return The command associated with the processed input, if it exists.
   */
  private Optional<Command> getCommand(Input input) {
    if (input == null) {
      return Optional.empty();
    }
    for (Command command : commands) {
      if (Arrays.asList(command.settings.getAliases()).contains(input.alias())) {
        return Optional.of(command);
      }
    }
    return Optional.empty();
  }

  /**
   * Determines if the command is valid to use based on who is using it and the settings for that command.
   */
  private boolean canExecute(Command cmd, Input input) {
    if (cmd.settings.isDisabled()) {
      return false;
    }
    if (input instanceof MessageInput messageInput) {
      return canExecute(cmd, messageInput);
    } else if (input instanceof SlashInput slashInput) {
      return canExecute(cmd, slashInput);
    }
    return false;
  }

  /**
   * Specific to MessageInput due to the MessageReceivedEvent.
   */
  private boolean canExecute(Command cmd, MessageInput input) {
    if (cmd.settings.isOwnerCommand() && !input.event().getAuthor().getId().equals(Main.bot.auth().ownerID())) {
      return false;
    }
    if (cmd.settings.isGuildOnly() && !input.event().isFromGuild()) {
      return false;
    }
    if (input.event().isFromGuild() && !input.event().getGuild().getSelfMember().hasPermission(cmd.settings.getSelfPermissions())) {
      return false;
    }
    if (input.event().isFromGuild() && input.event().getMessage().mentionsEveryone()
        && !input.event().getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) {
      return false;
    }
    Member member = input.event().getMember();
    if (member != null && !member.hasPermission(cmd.settings.getAuthorPermissions())) {
      return false;
    }
    return true;
  }

  /**
   * Specific to SlashInput due to the SlashCommandEvent. TODO: Uncomment after having access to SlashCommandEvent
   */
//  private boolean canExecute(Command cmd, SlashInput input) {
//    if (cmd.settings.isOwnerCommand() && !input.event().getUser().getId().equals(Main.bot.auth().ownerID())) {
//      return false;
//    }
//    if (cmd.settings.isGuildOnly() && !input.event().isFromGuild()) {
//      return false;
//    }
//    if (input.event().isFromGuild() && input.event().getGuild() != null && !input.event().getGuild().getSelfMember().hasPermission(cmd.settings.getSelfPermissions())) {
//      return false;
//    }
//    Member member = input.event().getMember();
//    if (member != null && !member.hasPermission(cmd.settings.getAuthorPermissions())) {
//      return false;
//    }
//    return true;
//  }

}
