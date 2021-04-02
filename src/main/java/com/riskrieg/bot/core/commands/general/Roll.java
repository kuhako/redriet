package com.riskrieg.bot.core.commands.general;

import com.riskrieg.bot.core.Command;
import com.riskrieg.bot.core.input.MessageInput;
import com.riskrieg.bot.core.input.SlashInput;
import com.riskrieg.constant.Colors;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.apache.commons.lang3.StringUtils;

public class Roll extends Command { // TODO: Redo this disgusting mess.

  private final int MAX_ROLL_STRING_LENGTH = 500;

  public Roll() {
    this.settings.setAliases("roll", "dice", "diceroll", "rolldice");
    this.settings.setDescription("Rolls the specified number of dice with the amount of sides specified.");
    this.settings.setEmbedColor(Colors.BORDER_COLOR);
  }

  @Override
  protected void execute(SlashInput input) {

  }

  protected void execute(MessageInput input) {
    List<SingleRoll> rollInputs = new ArrayList<>();
    String inputStr = StringUtils.join(input.args(), " ").trim();
    Pattern p = Pattern.compile("(\\d{0,5})(\\s+)?d(\\s+)?(\\d{1,5}(\\s+)?)((?=(\\s+)?\\+(\\s+)?\\d{1,5}).?.?(?!\\d{1,5}(\\s+)?d)\\d{1,5})?");
    Matcher m = p.matcher(inputStr);
    while (m.find()) {
      rollInputs.add(new SingleRoll(m.group().replaceAll("\\s+", "").trim()));
    }

    boolean shouldSum = inputStr.endsWith("-sum");
    if (rollInputs.size() == 0) {
      SingleRoll r = new SingleRoll();
      if (input.event().isFromType(ChannelType.TEXT)) {
        outputNormal(input.event().getChannel(), input.event().getMember().getEffectiveName(), r.getSides(), r.getAmount(), r.getAddition(), r.generateList());
      } else {
        outputNormal(input.event().getChannel(), input.event().getAuthor().getName(), r.getSides(), r.getAmount(), r.getAddition(), r.generateList());
      }
    } else if (rollInputs.size() == 1) {
      SingleRoll r = rollInputs.get(0);
      if ((shouldSum && r.getAmount() > 1)
          || r.generateList().toString().length() > MAX_ROLL_STRING_LENGTH) {
        if (input.event().isFromType(ChannelType.TEXT)) {
          outputSum(input.event().getChannel(), input.event().getMember().getEffectiveName(), r.getSides(), r.getAmount(), r.getAddition(), r.generateList());
        } else {
          outputSum(input.event().getChannel(), input.event().getAuthor().getName(), r.getSides(), r.getAmount(), r.getAddition(), r.generateList());
        }
      } else {
        if (input.event().isFromType(ChannelType.TEXT)) {
          outputNormal(input.event().getChannel(), input.event().getMember().getEffectiveName(), r.getSides(), r.getAmount(), r.getAddition(), r.generateList());
        } else {
          outputNormal(input.event().getChannel(), input.event().getAuthor().getName(), r.getSides(), r.getAmount(), r.getAddition(), r.generateList());
        }
      }
    } else {
      long totalSum = 0;
      Map<SingleRoll, List<Integer>> rollMap = new TreeMap<>();
      for (SingleRoll roll : rollInputs) {
        List<Integer> sum = roll.generateList();
        rollMap.put(roll, sum);
        totalSum += sum.stream().mapToInt(Integer::intValue).sum();
      }
      StringBuilder sb = new StringBuilder();
      int padding = 0;
      List<String> diceList = new ArrayList<>();
      List<String> listingList = new ArrayList<>();
      List<Integer> sumList = new ArrayList<>();
      for (List<Integer> rollList : rollMap.values()) {
        SingleRoll r = getKeyByValue(rollMap, rollList);
        diceList.add(r.getRoll());
        listingList.add(rollList.toString());
        sumList.add(rollList.stream().mapToInt(Integer::intValue).sum() + r.getAddition());
        if (padding < r.getRoll().length()) {
          padding = r.getRoll().length();
        }
      }
      for (int i = 0; i < diceList.size(); i++) {
        sb.append(String.format("%-" + padding + "s" + " | %s" + " | Sum: %s\n", diceList.get(i), listingList.get(i), sumList.get(i)));
      }
      diceList.clear();
      listingList.clear();
      sumList.clear();
      rollMap.clear();
      if (input.event().isFromType(ChannelType.TEXT)) {
        outputMultiple(input.event().getChannel(), input.event().getMember().getEffectiveName(), totalSum, sb);
      } else {
        outputMultiple(input.event().getChannel(), input.event().getAuthor().getName(), totalSum, sb);
      }
    }
    rollInputs.clear();
  }

  private void outputNormal(MessageChannel channel, String name, int sides, int amount, int addition, List<Integer> rolls) {
    channel.sendMessage(("**" + name + "** rolled " + rolls.toString() + (addition > 0 ? "+" + addition : "") + (
        amount == 1 ? (addition > 0 ? " (Sum: " + (rolls.get(0) + addition) + ")" : "")
            : " (Sum: " + (rolls.stream().mapToInt(Integer::intValue).sum() + addition) + ")")
        + " on " + numberToWord(amount) + " " + sides + "-sided " + (amount == 1
        ? "die" : "dice") + ".").replaceAll("\\s+", " ")).queue();
  }

  private void outputSum(MessageChannel channel, String name, int sides, int amount, int addition, List<Integer> rolls) {
    channel.sendMessage((":game_die: **" + amount + "d" + sides + (addition > 0 ? "+" + addition : "") + "** | **"
        + name + "**, Sum: **" + (rolls.stream().mapToInt(Integer::intValue).sum() + addition)
        + "**").replaceAll("\\s+", " ")).queue();
  }

  private void outputMultiple(MessageChannel channel, String name, long totalSum, StringBuilder sb) {
    channel.sendMessage(":game_die: **Sum: " + totalSum + "** | **" + name + "**\n" + (
        sb.toString().length() > MAX_ROLL_STRING_LENGTH ? ""
            : "```\n" + sb.toString() + "\n```")).queue();
  }

  private String unitsMap[] = {"zero", "one", "two", "three", "four", "five", "six", "seven",
      "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen",
      "sixteen", "seventeen", "eighteen", "nineteen"};
  private String tensMap[] = {"zero", "ten", "twenty", "thirty", "forty", "fifty", "sixty",
      "seventy", "eighty", "ninety"};

  private String numberToWord(long number) {
    if (number == 0) {
      return "zero";
    }

    if (number < 0) {
      return "-" + numberToWord(Math.abs(number));
    }

    String words = "";

    if ((number / 1000000000) > 0) {
      words += numberToWord(number / 1000000000) + " billion ";
      number %= 1000000000;
    }

    if ((number / 1000000) > 0) {
      words += numberToWord(number / 1000000) + " million ";
      number %= 1000000;
    }

    if ((number / 1000) > 0) {
      words += numberToWord(number / 1000) + " thousand ";
      number %= 1000;
    }

    if ((number / 100) > 0) {
      words += numberToWord(number / 100) + " hundred ";
      number %= 100;
    }

    if (number > 0) {
      if (number < 20) {
        words += unitsMap[(int) number];
      } else {
        words += tensMap[(int) (number / 10)];
        if ((number % 10) > 0) {
          words += "-" + unitsMap[(int) (number % 10)];
        }
      }
    }
    return words;
  }

  private <T, E> T getKeyByValue(Map<T, E> map, E value) {
    for (Entry<T, E> entry : map.entrySet()) {
      if (Objects.equals(value, entry.getValue())) {
        return entry.getKey();
      }
    }
    return null;
  }

}

class SingleRoll implements Comparable<SingleRoll> {

  private final SecureRandom r = new SecureRandom();

  final long uniqueID = new AtomicLong(0).getAndIncrement();

  private int amount = 1;
  private int sides = 6;
  private int addition = 0;
  private int sum = -1;
  private List<Integer> list;

  public SingleRoll(String inputStr) {
    String diceRoll = inputStr;
    if (inputStr.contains("+") && inputStr.split("\\+")[1].trim().matches("^[1-9][0-9]{0,5}$")) {
      diceRoll = inputStr.split("\\+")[0].trim();
      addition = Integer.parseInt(inputStr.split("\\+")[1].trim());
    }
    if (!diceRoll.startsWith("d") && diceRoll.split("d")[0].trim().matches("^[1-9][0-9]{0,5}$")) {
      amount = Integer.parseInt(diceRoll.split("d")[0].trim());
    }
    if (diceRoll.split("d")[1].trim().matches("^[1-9][0-9]{0,5}$")) {
      sides = Integer.parseInt(diceRoll.split("d")[1].trim());
    }
  }

  public SingleRoll() {
    this.amount = 1;
    this.sides = 6;
    this.addition = 0;
  }

  @Override
  public int compareTo(SingleRoll o) {
    int n;
    n = Integer.compare(this.amount, o.amount);
    if (n != 0) {
      return n;
    }
    n = Integer.compare(this.sides, o.sides);
    if (n != 0) {
      return n;
    }
    n = Integer.compare(this.addition, o.addition);
    if (n != 0) {
      return n;
    }
    return Long.compare(this.uniqueID, o.uniqueID);
  }

  public int generateRoll() {
    return r.ints(0, sides).findAny().getAsInt() + 1;
  }

  public int generateSum() {
    int sum = 0;
    for (int i = 0; i < amount; i++) {
      sum += generateRoll();
    }
    this.sum = sum + addition;
    return sum + addition;
  }

  public List<Integer> generateList() {
    List<Integer> rolls = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      rolls.add(generateRoll());
    }
    this.list = rolls;
    return rolls;
  }

  public long getUniqueID() {
    return uniqueID;
  }

  public int getAmount() {
    return amount;
  }

  public int getSides() {
    return sides;
  }

  public int getAddition() {
    return addition;
  }

  public int getSum() {
    return sum;
  }

  public List<Integer> getList() {
    return list;
  }

  public String getRoll() {
    return amount + "d" + sides + (addition > 0 ? "+" + addition : "");
  }

}
