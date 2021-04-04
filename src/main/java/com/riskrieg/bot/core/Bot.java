package com.riskrieg.bot.core;

import com.riskrieg.bot.auth.AuthRecord;
import com.riskrieg.bot.constant.BotConstants;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Bot {

  private final CommandHandler commandHandler;
  private final AuthRecord auth;
  private final Instant startTime;

  public Bot() {
    this.startTime = Instant.now();
    this.auth = readAuthRecord();
    this.commandHandler = new CommandHandler();
  }

  public Instant getStartTime() {
    return startTime;
  }

  public CommandHandler getCommandHandler() {
    return commandHandler;
  }

  public AuthRecord auth() {
    return auth;
  }

  private AuthRecord readAuthRecord() {
    File file = new File(BotConstants.AUTH_PATH + "auth.json");
    if (file.exists()) {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<AuthRecord> jsonAdapter = moshi.adapter(AuthRecord.class);
      try {
        return jsonAdapter.fromJson(Files.readString(file.toPath()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return new AuthRecord("TOKEN", "CLIENT_ID", "OWNER_ID", "PREFIX");
  }

  public void start(@Nonnull final Object... listeners) {
    try {
      DefaultShardManagerBuilder shardBuilder = DefaultShardManagerBuilder.createDefault(auth.token())
          .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
          .setMemberCachePolicy(MemberCachePolicy.ALL);
      shardBuilder.addEventListeners(listeners);
      shardBuilder.build();
    } catch (LoginException e) {
      e.printStackTrace();
    }
  }

}
