package com.cskaoyan.project.mall.mapper;



import com.cskaoyan.project.mall.controller.goods.util.Categorylist;
import com.cskaoyan.project.mall.controller.goods.util.Item;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 *
 * @auther XXX
 * @date 2019/8/16
 * @time 20:57
 */
public interface CartAndBrandMapper {

    List<Item> queryBrandList();

    List<Categorylist> queryCartList();

    List<Item> queryCartListByPid(int value);
}
