package com.redhorse.deokhugam.domain.user.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import java.util.Map;

public class UserDuplicateException extends UserException {

  public UserDuplicateException(String email) {
    super(ErrorCode.USER_DUPLICATE, Map.of("email", maskEmail(email)));
  }

  private static String maskEmail(String email) {
    if (email == null || !email.contains("@")) {
      return "redacted";
    }

    String[] parts = email.split("@", 2);
    String local = parts[0];
    String maskedLocal = local.length() <= 2 ? "**" : local.substring(0, 2) + "***";
    return maskedLocal + "@" + parts[1];
  }
}