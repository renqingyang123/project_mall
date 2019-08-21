package com.cskaoyan.project.mall.controller.mall;

import com.cskaoyan.project.mall.domain.Order;
import com.cskaoyan.project.mall.domain.OrderGoods;
import com.cskaoyan.project.mall.domain.User;
import com.cskaoyan.project.mall.service.mall.OrderGoodsService;
import com.cskaoyan.project.mall.service.mall.OrderService;
import com.cskaoyan.project.mall.service.userService.UserService;
import com.cskaoyan.project.mall.utils.PageBean;
import com.cskaoyan.project.mall.utils.ResponseUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 申涛涛
 * @date 2019/8/18 15:36
 */
@Controller
public class OederController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderGoodsService orderGoodsService;
    @Autowired
    UserService userService;

    @RequestMapping("/admin/order/list")
    @ResponseBody
    public ResponseUtils queryAllOrders(int page, int limit, String sort, String order, Integer userId, String orderSn, Short[] orderStatusArray) {
        List<Order> orders = orderService.queryPageOrderByExample(page, limit, sort, order, userId, orderSn, orderStatusArray);

        PageInfo pageInfo = new PageInfo(orders);
        long total = pageInfo.getTotal();
        PageBean pageBean = new PageBean(orders,total);
        ResponseUtils responseUtils = new ResponseUtils();
        if (orders == null) {
            responseUtils.setErrno(401);
            responseUtils.setErrmsg("服务端错误！");
        } else {
            responseUtils.setErrno(0);
            responseUtils.setErrmsg("成功！");
            responseUtils.setData(pageBean);
        }
        return responseUtils;
    }

    @RequestMapping("/admin/order/detail")
    @ResponseBody
    public ResponseUtils queryOrderDetail(Integer id) {

        Order order = orderService.queryOrderById(id);
        List<OrderGoods> orderGoods = orderGoodsService.queryAllOrderGoodsByOrderId(order.getId());
        User user = null;
        if (order.getUserId() != null) {
            user = userService.selectByPrimaryKey(order.getUserId());
        }
        Map map = new HashMap();
        map.put("order",order);
        map.put("orderGoods",orderGoods);
        map.put("user",user);

        ResponseUtils responseUtils = new ResponseUtils();
        if (order == null) {
            responseUtils.setErrno(401);
            responseUtils.setErrmsg("服务端错误！");
        } else {
            responseUtils.setErrno(0);
            responseUtils.setErrmsg("成功！");
            responseUtils.setData(map);
        }
        return responseUtils;
    }

    @RequestMapping("/admin/order/create")
    @ResponseBody
    public ResponseUtils insertOrder(Order order) {
        ResponseUtils responseUtils = new ResponseUtils();


        //int insert = orderService.insertOrder(orders);


        if (order == null) {
            responseUtils.setErrno(401);
            responseUtils.setErrmsg("服务端错误！");
        } else {
            responseUtils.setErrno(0);
            responseUtils.setErrmsg("成功！");
            responseUtils.setData(order);
        }
        return responseUtils;
    }

    @RequestMapping("/admin/order/update")
    @ResponseBody
    public ResponseUtils updateOrder(Order order) {
        ResponseUtils responseUtils = new ResponseUtils();
        if (order.getId() == null) {
            responseUtils.setErrno(401);
            responseUtils.setErrmsg("id 不能为 null");
            return responseUtils;
        }
//        int update = orderService.updateOrderById(orders);

        if (order == null) {
            responseUtils.setErrno(401);
            responseUtils.setErrmsg("服务端错误！");
        } else {
            responseUtils.setErrno(0);
            responseUtils.setErrmsg("成功！");
            responseUtils.setData(order);
        }
        return responseUtils;
    }

    @RequestMapping("/admin/order/delete")
    @ResponseBody
    public ResponseUtils deleteOrder(Order order) {
        ResponseUtils responseUtils = new ResponseUtils();
        if (order.getId() == null) {
            responseUtils.setErrno(401);
            responseUtils.setErrmsg("id 不能为 null");
            return responseUtils;
        }
        int deleted = orderService.deleteLogicOrderByDeleted(order);

        if (deleted == 0) {
            responseUtils.setErrno(401);
            responseUtils.setErrmsg("服务端错误！");
        } else {
            responseUtils.setErrno(0);
            responseUtils.setErrmsg("成功！");
            responseUtils.setData(null);
        }
        return responseUtils;
    }
}
