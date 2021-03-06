package com.cskaoyan.project.mall.controller.mall;

import com.cskaoyan.project.mall.domain.Keyword;
import com.cskaoyan.project.mall.service.mall.KeywordService;
import com.cskaoyan.project.mall.utils.PageBean;
import com.cskaoyan.project.mall.utils.ResponseUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * @author 申涛涛
 * @date 2019/8/18 19:36
 */
@Controller
public class KeywordController {
    @Autowired
    KeywordService keywordService;

    @RequestMapping("/admin/keyword/list")
    @ResponseBody
    public ResponseUtils queryPageKeyword(int page,int limit,String keyword,String url,String sort,String order) {
        List<Keyword> keywords = keywordService.queryPageKeyword(page, limit, keyword, url, sort, order);

        PageInfo pageInfo = new PageInfo(keywords);
        long total = pageInfo.getTotal();
        PageBean pageBean = new PageBean(keywords,total);
        ResponseUtils responseUtils = new ResponseUtils();
        if (keywords == null) {
            responseUtils.setErrno(401);
            responseUtils.setErrmsg("服务端错误！");
        } else {
            responseUtils.setErrno(0);
            responseUtils.setErrmsg("成功！");
            responseUtils.setData(pageBean);
        }
        return responseUtils;
    }

    @RequestMapping("/admin/keyword/create")
    @ResponseBody
    public ResponseUtils insertKeyword(@RequestBody Keyword keyword) {
        ResponseUtils responseUtils = new ResponseUtils();
        if (keyword.getKeyword() == null) {
            responseUtils.setErrno(401);
            responseUtils.setErrmsg("参数不对，关键词为必选项");
            return responseUtils;
        }
        //为以下字段设置默认值
        if (keyword.getUrl() == null) {
            keyword.setUrl("");
        }
        if (keyword.getIsHot() == null) {
            keyword.setIsHot(false);
        }
        if (keyword.getIsDefault() == null) {
            keyword.setIsDefault(false);
        }
        Date date = new Date();
        keyword.setDeleted(false);
        keyword.setAddTime(date);
        keyword.setUpdateTime(date);
        keyword.setSortOrder(10);
        //sql层实现了添加后查询操作
        int insert = keywordService.insertKeyword(keyword);

        if (insert == 0) {
            responseUtils.setErrno(401);
            responseUtils.setErrmsg("服务端错误！");
        } else {
            responseUtils.setErrno(0);
            responseUtils.setErrmsg("成功！");
            responseUtils.setData(keyword);
        }
        return responseUtils;
    }

    @RequestMapping("/admin/keyword/update")
    @ResponseBody
    public ResponseUtils updateKeyword(@RequestBody Keyword keyword) {
        ResponseUtils responseUtils = new ResponseUtils();
        if (keyword.getKeyword() == null || keyword.getId() == null) {
            responseUtils.setErrno(401);
            responseUtils.setErrmsg("关键词 或 关键词id 不能为null");
            return responseUtils;
        }
        Keyword keyword1 = keywordService.queryKeyword(keyword.getId());
        //若是没有传以下数据，则取原数据
        if (keyword.getUrl() == null) {
            keyword.setUrl(keyword1.getUrl());
        }
        if (keyword.getIsHot() == null) {
            keyword.setIsHot(keyword1.getIsHot());
        }
        if (keyword.getIsDefault() == null) {
            keyword.setIsDefault(keyword1.getIsDefault());
        }
        if (keyword.getSortOrder() == null) {
            keyword.setSortOrder(keyword1.getSortOrder());
        }
        //add_time和deleted不允许用户编辑时修改
        keyword.setAddTime(keyword1.getAddTime());
        keyword.setDeleted(keyword1.getDeleted());

        keyword.setUpdateTime(new Date());
        //更新操作
        int update = keywordService.updateKeyword(keyword);
        //查询最新的keyword
        keyword = keywordService.queryKeyword(keyword.getId());

        if (update == 0) {
            responseUtils.setErrno(401);
            responseUtils.setErrmsg("服务端错误！");
        } else {
            responseUtils.setErrno(0);
            responseUtils.setErrmsg("成功！");
            responseUtils.setData(keyword);
        }
        return responseUtils;
    }

    @RequestMapping("/admin/keyword/delete")
    @ResponseBody
    public ResponseUtils deleteKeyword(@RequestBody Keyword keyword) {
        //逻辑删除
        int deleted = keywordService.deleteLogicKeywordByDeleted(keyword);
        ResponseUtils responseUtils = new ResponseUtils();
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
