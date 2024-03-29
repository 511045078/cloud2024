package com.atguigu.cloud.service;

import feign.Param;

public interface AccountService {

    void decrease(@Param("userId") Long userId, @Param("money") Long money);

}
