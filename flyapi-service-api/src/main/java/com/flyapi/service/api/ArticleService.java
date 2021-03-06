package com.flyapi.service.api;

import com.flyapi.core.base.BaseService;
import com.flyapi.model.CmsArticle;
import com.flyapi.pojo.vo.ArticleDetailVo;
import com.flyapi.pojo.vo.ArticleSimpleVo;
import com.flyapi.pojo.vo.ViewLevelVo;

import java.util.List;

/**
 * author: flyhero
 * Date: 2017/6/9 0009 下午 3:57
 */
public interface ArticleService extends BaseService<CmsArticle> {
    /**
     * 获取文章详情
     * Title: findArticleDetail
     * params: [articleId]
     * return: com.flyapi.service.vo.ArticleDetailVo
     * author: flyhero(http://flyhero.top)
     * date: 2017/6/22 0022 上午 11:26
     */
    ArticleDetailVo findArticleDetail(Long articleId);

    List<ArticleSimpleVo> findArticleSimple();

    List<CmsArticle> findLastUpdateOrHotArticles(Integer type);

    List<CmsArticle> findArticleBySubjectId(Long subjectId);

    List<CmsArticle> findArticleByUserId(Long userId);

    List<ViewLevelVo> findViewLevel(Long userId);

    List<CmsArticle> findHotArticlesByUserId(Long userId);

    int findArticleCountByUserId(Long userId);

    List<CmsArticle> findLastUpdateArticlesByUserId(Long userId);

    CmsArticle findArticleSumBySubjectId(Long subjectId);

    int updateLikeNum(CmsArticle article);
}
