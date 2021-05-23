package com.stanum.skrudzh.exception;

import java.io.Serializable;

public interface AppError extends Serializable {

    Integer getStatus();

    String getErrorCode();

}
