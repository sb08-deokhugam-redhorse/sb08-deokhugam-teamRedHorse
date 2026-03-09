package com.redhorse.deokhugam.domain.user.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import java.util.Map;

public class UserHardDeletedException extends UserException {

  public UserHardDeletedException() {
    super(ErrorCode.HARD_DELETE_FAILED, Map.of("description", "서버 내부 오류로 인해 사용자 영구 삭제 처리가 완료되지 않았습니다."));
  }
}