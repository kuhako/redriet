package com.riskrieg.bot.auth;

import java.util.Objects;

public record AuthRecord(String token, String clientID, String ownerID, String prefix) {

  public AuthRecord {
    Objects.requireNonNull(token);
    Objects.requireNonNull(clientID);
    Objects.requireNonNull(ownerID);
    Objects.requireNonNull(prefix);
    if (token.isBlank() || clientID.isBlank() || ownerID.isBlank() || prefix.isBlank()) {
      throw new IllegalArgumentException("field cannot be blank");
    }
  }

}
