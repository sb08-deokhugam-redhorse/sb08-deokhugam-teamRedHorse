package com.redhorse.deokhugam.domain.user.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import java.util.Map;

public class UserNotSoftDeletedException extends UserException {

  public UserNotSoftDeletedException() {
    super(ErrorCode.USER_NOT_SOFT_DELETED, Map.of("description", "영구 삭제는 소프트 삭제된 계정에 대해서만 가능합니다. 먼저 회원 탈퇴를 진행해 주세요."));
  }
}