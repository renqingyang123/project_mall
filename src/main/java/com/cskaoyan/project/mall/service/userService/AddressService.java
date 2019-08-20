package com.cskaoyan.project.mall.service.userService;

import com.cskaoyan.project.mall.domain.Address;
import com.cskaoyan.project.mall.domain.AddressExample;

import java.util.List;

public interface AddressService {
    List<Address> selectByExample(AddressExample example);

    List<Address> findAllAddress(int page,int limit,String name,Integer userId);
}
