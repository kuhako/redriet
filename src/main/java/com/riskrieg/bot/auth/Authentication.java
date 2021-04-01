package com.riskrieg.bot.auth;

import com.google.gson.Gson;
import com.riskrieg.bot.constant.BotConstants;
import java.io.FileWriter;
import java.io.Writer;

public class Authentication {

  private String token;
  private String clientID;
  private String ownerID;
  private String defaultPrefix;
  private String prefix;

  public void setPrefix(String prefix) {
    this.prefix = prefix;
    Gson gson = new Gson();
    String json = gson.toJson(this);
    try {
      Writer writer = new FileWriter(BotConstants.AUTH_PATH + "auth.json");
      writer.write(json);
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getToken() {
    return token;
  }

  public String getClientID() {
    return clientID;
  }

  public String getOwnerID() {
    return ownerID;
  }

  public String getDefaultPrefix() {
    return defaultPrefix;
  }

  public String getPrefix() {
    return prefix;
  }

}
