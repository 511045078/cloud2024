package com.atguigu.cloud.service;

import com.atguigu.cloud.entities.Storage;
import com.atguigu.cloud.resp.ResultData;
import feign.Param;

public interface StorageService {

    // 缩减库存
    void decrease(@Param("productId") Long productId, @Param("count") Integer count);
}
