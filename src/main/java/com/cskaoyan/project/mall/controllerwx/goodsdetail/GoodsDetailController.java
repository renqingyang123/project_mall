package com.cskaoyan.project.mall.controllerwx.goodsdetail;


import com.cskaoyan.project.mall.domain.*;
import com.cskaoyan.project.mall.mapper.*;
import com.cskaoyan.project.mall.service.advertiseService.GroupRulesService;
import com.cskaoyan.project.mall.service.goods.*;
import com.cskaoyan.project.mall.service.mall.BrandService;
import com.cskaoyan.project.mall.service.mall.IssueService;
import com.cskaoyan.project.mall.service.userService.UserService;
import com.cskaoyan.project.mall.utils.ResponseUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class GoodsDetailController {

    @Autowired
    GoodsService goodsService;
    @Autowired
    GoodsMapper goodsMapper;


    @Autowired
    GoodsAttributeService goodsAttributeService;
    @Autowired
    GoodsProductService goodsProductService;
    @Autowired
    IssueService issueService;
    @Autowired
    BrandService brandService;
    @Autowired
    BrandMapper brandMapper;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    GoodsSpecificationService goodsSpecificationService;
    @Autowired
    CollectMapper collectMapper;
    @Autowired
    FootprintMapper footprintMapper;
    @Autowired
    GroupRulesService groupRulesService;
    @Autowired
    UserService userService;





    @RequestMapping("wx/goods/detail")
    @ResponseBody
    public ResponseUtils<HashMap> detailesd( Integer id)//UserId// )
    {

        Subject subject = SecurityUtils.getSubject();
        subject = SecurityUtils.getSubject();
        //获取认证后的用户信息，通过Realm进行封装的
        User user = (User) subject.getPrincipal();
        int userId = user.getId();

        Goods goods = goodsMapper.selectByPrimaryKey(id);
        //Attribute
        List<GoodsAttribute> goodsAttributes = goodsAttributeService.queryByGoodsId(id);
        //
        List<GoodsSpecification> goodsSpecifications = goodsSpecificationService.queryByGoodsId(id);
        List<Map<String, Object>> goodsSpecificationVo = new ArrayList<>(goodsSpecifications.size());
        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            Map<String, Object> item = new HashMap<>();
            item.put("name","规格");
            item.put("valueList",goodsSpecifications);
            goodsSpecificationVo.add(item);
        }

        List<Issue> issues = issueService.queryPageIssues(1, 4,"","add_time","desc");

        List<GoodsProduct> goodsProducts = goodsProductService.queryByGoodsId(id);
        //团购信息
        GrouponRulesExample grouponRulesExample = new GrouponRulesExample();
        GrouponRulesExample.Criteria criteria1 = grouponRulesExample.createCriteria();
        criteria1.andGoodsIdEqualTo(id);
        List<GrouponRules> grouponRules = groupRulesService.selectByExample(grouponRulesExample);

        //评论
        CommentExample commentExample = new CommentExample();
        CommentExample.Criteria criteria = commentExample.createCriteria();
        criteria.andValueIdEqualTo(id);
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        List<HashMap> commentVo = new ArrayList<HashMap>();
        HashMap<String, Object> commentsVo = new HashMap<String, Object>();

        for (Comment comment : comments) {
            HashMap<String, Object> c = new HashMap<>();
            c.put("id", comment.getId());
            c.put("addTime", comment.getAddTime());
            c.put("content", comment.getContent());
            User user1 = userService.selectByPrimaryKey(comment.getUserId());
            c.put("nickname", user1 == null ? "" : user1.getNickname());
            c.put("avatar", user1 == null ? "" : user1.getAvatar());
            c.put("picList", comment.getPicUrls());
            commentVo.add(c);
        }


        commentsVo.put("count",comments.size());
        if(comments.size() > 3) {
            commentsVo.put("data", commentVo.subList(0, 2));
        }else {
            commentsVo.put("data", null);
        }
        Brand brand = new Brand();
    if(goods.getBrandId()!= null) {
        brand = brandMapper.selectByPrimaryKey(goods.getBrandId());
    }

    // 用户收藏
        int userHasCollect = 0;
        if (user != null) {
            CollectExample collectExample = new CollectExample();
            CollectExample.Criteria criteria2 = collectExample.createCriteria();
            criteria2.andUserIdEqualTo(userId);
            criteria2.andValueIdEqualTo(id);
            List<Collect> collects = collectMapper.selectByExample(collectExample);
           if(collects.size() != 0){
               userHasCollect = collects.get(0).getType();
           }else {
             userHasCollect = 0;
           }
        }

        // 记录用户的足迹 异步处理
        if (user != null) {
              //记录用户足迹
            FootprintExample footprintExample = new FootprintExample();
            FootprintExample.Criteria criteria2 = footprintExample.createCriteria();
            criteria2.andUserIdEqualTo(user.getId());
            criteria2.andGoodsIdEqualTo(id);
            long l = footprintMapper.countByExample(footprintExample);
            //去重
            if(l == 0) {
                Footprint footprint = new Footprint();
                footprint.setUserId(userId);
                footprint.setGoodsId(id);
                footprintMapper.insert(footprint);
            }
        }


        Map<String, Object> data = new HashMap<>();
        try {
            data.put("info", goods);
            data.put("userHasCollect", userHasCollect);
            data.put("issue", issues);
            data.put("comment", commentsVo);
            data.put("specificationList", goodsSpecificationVo);
            data.put("productList", goodsProducts);
            data.put("attribute", goodsAttributes);
            data.put("brand", brand);
            data.put("groupon", grouponRules);
            //SystemConfig.isAutoCreateShareImage()

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //商品分享图片地址
        data.put("shareImage", goods.getShareUrl());
        ResponseUtils pageBeanResponseUtils = new ResponseUtils<>();
        pageBeanResponseUtils.setData(data);
        pageBeanResponseUtils.setErrno(0);
        pageBeanResponseUtils.setErrmsg("成功");
        return pageBeanResponseUtils;
    }

    @RequestMapping("wx/goods/related")
    @ResponseBody
    public ResponseUtils detailed(Integer id)//UserId// )
    {

        Subject subject = SecurityUtils.getSubject();
        subject = SecurityUtils.getSubject();
        //获取认证后的用户信息，通过Realm进行封装的
        User user = (User) subject.getPrincipal();
        int userId = user.getId();
        //查相关商品
        Goods goods = goodsService.queryById(id);
        GoodsExample goodsExample = new GoodsExample();
        GoodsExample.Criteria criteria = goodsExample.createCriteria();
        criteria.andCategoryIdEqualTo(goods.getCategoryId());

        List<Goods> goodsList = goodsService.selectByExample(goodsExample);
        HashMap<String, List> item = new HashMap<>();
        item.put("goodsList",goodsList);
        ResponseUtils pageBeanResponseUtils = new ResponseUtils<>();
        pageBeanResponseUtils.setData(item);
        pageBeanResponseUtils.setErrno(0);
        pageBeanResponseUtils.setErrmsg("成功");
        return pageBeanResponseUtils;
    }


 /*   @RequestMapping("goodscount")
    @ResponseBody
    public ResponseUtils<int> detailed( userId,Integer id)//UserId// )
    {
       return null; //购物车，等购物车回显。
    }
*/



}

