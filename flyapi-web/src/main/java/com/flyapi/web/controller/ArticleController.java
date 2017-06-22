package com.flyapi.web.controller;

import com.flyapi.core.base.BaseController;
import com.flyapi.core.constant.JSONResult;
import com.flyapi.model.CmsArticle;
import com.flyapi.model.UcenterUser;
import com.flyapi.service.api.ArticleService;
import com.flyapi.service.api.UserService;
import com.flyapi.vo.ArticleDetailVo;
import com.flyapi.vo.ArticleSimpleVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * author: flyhero
 * Date: 2017/6/19 0019 下午 2:17
 */
@Controller
@RequestMapping("article")
public class ArticleController extends BaseController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserService userService;
    /**
     * Title: findArticleDetail
     * params: [articleId]
     * return: org.springframework.web.servlet.ModelAndView
     * author: flyhero(http://flyhero.top)
     * date: 2017/6/22 0022 上午 11:35
     */
    @RequestMapping(value = "detail/{articleId}",method = RequestMethod.GET)
    public ModelAndView findArticleDetail(@PathVariable("articleId")Long articleId){

        ArticleDetailVo detailVo = articleService.findArticleDetail(articleId);

        mv.addObject("detailVo",detailVo);
        mv.setViewName("html/article/detail");
        return mv;
    }
    /**
     * Title: findArticleList
     * params: [pageNum, pageSize]
     * return: com.flyapi.core.constant.JSONResult
     * author: flyhero(http://flyhero.top)
     * date: 2017/6/22 0022 上午 11:10
     */
    @ResponseBody
    @RequestMapping("findArticleList")
    public JSONResult findArticleList(int pageNum,int pageSize){
        PageInfo<ArticleSimpleVo> pageInfo = null;
        PageHelper.startPage(pageNum, pageSize);
        try{
            List<ArticleSimpleVo> list = articleService.findArticleSimple();
            pageInfo = new PageInfo<ArticleSimpleVo>(list);
        }catch (Exception ex){
            return JSONResult.error();
        }

        return JSONResult.ok(pageInfo);
    }
    /**
     * Title: findLastUpdateOrHotArticles
     * params: [type]
     * return: com.flyapi.core.constant.JSONResult
     * author: flyhero(http://flyhero.top)
     * date: 2017/6/22 0022 下午 6:11
     */
    @ResponseBody
    @RequestMapping("findLastUpdateOrHotArticles")
    public JSONResult findLastUpdateOrHotArticles(int type){
        List<CmsArticle> list =null;
        try{
            list=articleService.findLastUpdateOrHotArticles(type);
        }catch (Exception e){
            return JSONResult.error();
        }
        return JSONResult.ok(list);
    }

}