package com.stanum.skrudzh.jpa.model.base;

import com.stanum.skrudzh.model.enums.HashableTypeEnum;

public interface Hashable extends Base, HasCurrency {
    HashableTypeEnum getHashableType();

    String getPrototypeKey();
}
