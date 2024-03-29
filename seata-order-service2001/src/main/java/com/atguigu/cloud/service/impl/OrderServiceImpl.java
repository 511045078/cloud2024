package com.atguigu.cloud.service.impl;

import com.atguigu.cloud.apis.AccountFeignApi;
import com.atguigu.cloud.apis.StorageFeignApi;
import com.atguigu.cloud.entities.Order;
import com.atguigu.cloud.mapper.OrderMapper;
import com.atguigu.cloud.service.OrderService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private StorageFeignApi storageFeignApi;

    @Resource
    private AccountFeignApi accountFeignApi;

    @Override
    @GlobalTransactional(name = "zzyy-create-order",rollbackFor = Exception.class) //AT
    public void create(Order order) {
        // xid全局事务id检查
        String xid = RootContext.getXID();
        // 新建订单
        log.info("-----------------开始新建订单："+"\t"+"xid:"+ xid);
        // 订单初始值应为0
        order.setStatus(0);
        int result = orderMapper.insertSelective(order);
        // 插入订单成功后获得插入mysql的实体对象
        Order orderFromDB = null;

        if (result > 0) {
            //从mysql中查出刚插入的数据
            orderFromDB = orderMapper.selectOne(order);
            log.info("-----------------新建订单成功 orderFromDB info:"+ orderFromDB);
            // 2.扣减库存
            log.info("-----------------扣减库存开始 orderFromDB info:"+ orderFromDB);
            storageFeignApi.decrease(orderFromDB.getProductId(),orderFromDB.getCount());
            log.info("-----------------扣减库存成功 orderFromDB info:"+ orderFromDB);
            // 3.扣减余额
            log.info("-----------------扣减余额开始 orderFromDB info:"+ orderFromDB);
            accountFeignApi.decrease(orderFromDB.getUserId(),orderFromDB.getMoney());
            log.info("-----------------扣减余额成功 orderFromDB info:"+ orderFromDB);
            // 4.订单状态从0修改为1，表示修改完成
            orderFromDB.setStatus(1);

            // 查出条件为userId为orderFromDB.getUserId()，并且status为0的数据，然后根据这个where条件更新
            Example example = new Example(Order.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId",orderFromDB.getUserId());
            criteria.andEqualTo("status",0);
            int updateResult = orderMapper.updateByExampleSelective(orderFromDB, example);

        }
        log.info("-----------------结束新建订单："+"\t"+"xid:"+ xid);

    }

}
