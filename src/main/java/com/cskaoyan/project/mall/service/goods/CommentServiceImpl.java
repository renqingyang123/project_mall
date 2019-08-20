package com.cskaoyan.project.mall.service.goods;

import com.cskaoyan.project.mall.controller.goods.vo.PageVO;
import com.cskaoyan.project.mall.controller.goods.vo.ResponseVO;
import com.cskaoyan.project.mall.domain.Comment;
import com.cskaoyan.project.mall.domain.Goods;
import com.cskaoyan.project.mall.mapper.CommentMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 *
 * @auther XXX
 * @date 2019/8/16
 * @time 17:40
 */
@Service
public class CommentServiceImpl implements CommentService{
    @Autowired
    CommentMapper commentMapper;

    @Override
    public ResponseVO<PageVO<Comment>> queryAll(int page, int limit) {
        PageHelper.startPage(page,limit);
        List<Comment> comments = commentMapper.queryAll();
        //查询total
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);
        //把total，list<comment>放到pageVO中
        PageVO<Comment> pageVO = new PageVO<>(pageInfo.getTotal(), pageInfo.getList());
        ResponseVO<PageVO<Comment>> responseVO = new ResponseVO<>(pageVO, "成功", 0);
        return responseVO;
    }

    @Override
    public int deleteById(Integer id) {
        int i = commentMapper.deleteByPrimaryKey(id);
        return i;
    }

    @Override
    public Comment selectByPrimaryKey(int commentId) {
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        return comment;
    }

    @Override
    public int updateByPrimaryKey(Comment comment) {
        int i = commentMapper.updateByPrimaryKey(comment);
        return i;
    }

    @Override
    public ResponseVO<PageVO<Comment>> fuzzyQuery(int page, int limit, String userId, String valueId) {
        PageHelper.startPage(page, limit);
        List<Comment> goods = commentMapper.fuzzyQuery("%" + userId + "%", "%" + valueId + "%");
        PageInfo<Comment> pageInfo = new PageInfo<>(goods);
        PageVO<Comment> pageVO = new PageVO<>(pageInfo.getTotal(), pageInfo.getList());
        ResponseVO<PageVO<Comment>> responseVO = new ResponseVO<>(pageVO, "成功", 0);
        return responseVO;

    }
}