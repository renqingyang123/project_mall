package com.cskaoyan.project.mall.controllerwx;

import com.cskaoyan.project.mall.domain.*;
import com.cskaoyan.project.mall.service.advertiseService.TopicService;
import com.cskaoyan.project.mall.service.goods.CommentService;
import com.cskaoyan.project.mall.service.goods.GoodsService;
import com.cskaoyan.project.mall.service.userService.UserService;
import com.cskaoyan.project.mall.utils.ResponseUtils;
import com.cskaoyan.project.mall.utils.userbean.UserInfo;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("wx")
public class WxCommentController {
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    TopicService topicService;

    /**
     * 评论列表
     * @param type     类型ID。 如果是0，则查询商品评论；如果是1，则查询专题评论。
     * @param valueId  商品或专题ID。如果type是0，则是商品ID；如果type是1，则是专题ID。
     * @param showType 显示类型。如果是0，则查询全部；如果是1，则查询有图片的评论。
     * @param page     分页页数
     * @param size     分页大小
     * @return 评论列表
     */
    @RequestMapping("comment/list")
    public Object list(int page, int size, Byte type, Integer valueId, Integer showType){
        List<Comment> commentList = commentService.query(page, size, type, valueId, showType);
        HashMap<String, Object> hashMap = new HashMap<>();
        PageInfo<Comment> pageInfo=new PageInfo<>(commentList);
        hashMap.put("count",pageInfo.getTotal());
        hashMap.put("currentPage",1);
        ArrayList<Map<String, Object>> mapArrayList = new ArrayList<>(commentList.size());
        for (Comment comment : commentList) {
            Map<String, Object> commentVo = new HashMap<>();
            commentVo.put("addTime", comment.getAddTime());
            commentVo.put("content", comment.getContent());
            commentVo.put("picList", comment.getPicUrls());

            User user = userService.selectByPrimaryKey(comment.getUserId());
            UserInfo userInfo=new UserInfo();
            userInfo.setAvatarUrl(user.getAvatar());
            userInfo.setNickName(user.getNickname());
            commentVo.put("userInfo", userInfo);
            mapArrayList.add(commentVo);
        }
        hashMap.put("data",mapArrayList);
        return  ResponseUtils.ok(hashMap);
    }

    /**
     * 发表评论
     * @param userId  用户ID
     * @param comment 评论内容
     * @return 发表评论操作结果
     */
    @RequestMapping("comment/post")
    public Object comment(@RequestBody Comment comment){
        Subject subject = SecurityUtils.getSubject();
        User user = (User)subject.getPrincipal();
        if(user.getId() == null){
            return ResponseUtils.unlogin();
        }
        Object validate = validate(comment);
        if(validate != null){
            return validate;
        }
        comment.setUserId(user.getId());
        int i = commentService.insertSelective(comment);
        if (i==1){
            return ResponseUtils.ok(comment);
        }
        return ResponseUtils.fail();

    }
    //格式化comment对象数据
    private Object validate(Comment comment){
        String content = comment.getContent();
        if(content == null || "".equals(content.trim())){
            return ResponseUtils.badArgument();
        }
        Short star = comment.getStar();
        if(star == null){
            return ResponseUtils.badArgument();
        }
        if(star < 0 || star > 5){
            return ResponseUtils.badArgument();
        }
        Byte type = comment.getType();
        Integer valueId = comment.getValueId();
        if(type == null || valueId == null){
            return ResponseUtils.badArgument();
        }
        if(type == 0){
            if(goodsService.queryById(valueId) == null){
                return ResponseUtils.badArgument();
            }
        }else if(type == 1){
            if(topicService.selectByPrimaryKey(valueId) == null){
                return ResponseUtils.badArgument();
            }
        }else {
            return ResponseUtils.badArgument();
        }
        Boolean hasPicture = comment.getHasPicture();
        if(hasPicture == null || !hasPicture){
            comment.setPicUrls(new String[0]);
        }
        //更新时间
        comment.setAddTime(new Date());
        comment.setUpdateTime(new Date());

        return null;
    }
   //查询专题的详细信息
    @RequestMapping("topic/related")
    public Object queryTopic(int id){
        List<Topic> topicList = topicService.selectByExample(new TopicExample());
        Random random = new Random();
        int i = random.nextInt(topicList.size() - 4);
        List<Topic> topics = topicList.subList(i, i + 4);
        return ResponseUtils.ok(topics);
    }
    @RequestMapping("topic/detail")
    public Object queryDetail(int id){
        Map<String,Object> map=new HashMap<>();
        Topic topic = topicService.selectByPrimaryKey(id);
        map.put("goods",new String[]{});
        map.put("topic",topic);
        return ResponseUtils.ok(map);
    }

    //查询全部以及有图评论的数量
    @RequestMapping("comment/count")
    public Object count(Byte type, Integer valueId){
        HashMap<String, Integer> map = new HashMap<>();
        CommentExample example1 = new CommentExample();
        example1.createCriteria().andValueIdEqualTo(valueId).andTypeEqualTo(type);
        int i1 = commentService.countByExample(example1);
        CommentExample example2 = new CommentExample();
        example2.createCriteria().andTypeEqualTo(type).andValueIdEqualTo(valueId).andHasPictureEqualTo(true);
        int i2 = commentService.countByExample(example2);
        map.put("allCount",i1);
        map.put("hasPicCount",i2);
        return ResponseUtils.ok(map);
    }
}
