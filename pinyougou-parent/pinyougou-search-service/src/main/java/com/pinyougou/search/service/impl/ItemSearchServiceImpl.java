package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String,Object> resultMap=new HashMap<String, Object>();
        resultMap.putAll(searchList(searchMap));
        return resultMap;
    }

    //定义私有方法
    private Map searchList(Map searchMap){
        //接受的map集合
        Map<String,Object> resultMap =   new HashMap<String, Object>();
//        Query query = new SimpleQuery("*:*");
//        Object keywords = searchMap.get("keywords");
//        //查询条件
//        Criteria criteria = new Criteria("item_keywords").is(searchMap.get(keywords));
//        query.addCriteria(criteria);
//        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
//        resultMap.put("rows",page.getContent());
        //构建高亮集合设置高亮域(前置,后置)
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置高亮域
        highlightOptions.addField("item_title");
        highlightOptions.addField("item_brand");
        //设置高亮属性
        highlightOptions.setSimplePrefix("<span style='color:red'>");
        highlightOptions.setSimplePostfix("</span>");
        //高亮属性添加
        query.setHighlightOptions(highlightOptions);
        //查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //循环高亮入口集合
        List<HighlightEntry<TbItem>> highlightEntryList = page.getHighlighted();
        for (HighlightEntry<TbItem> highlightEntry : highlightEntryList) {
            List<HighlightEntry.Highlight> list = highlightEntry.getHighlights();
            //获取头文件的高亮
            HighlightEntry.Highlight itemTitle = list.get(0);
            String title = itemTitle.getSnipplets().get(0);
            //获取品牌的高亮
//            HighlightEntry.Highlight itemBrand = list.get(1);
//            String brand = itemBrand.getSnipplets().get(0);
            TbItem item = highlightEntry.getEntity();
            item.setTitle(title);
//            item.setBrand(brand);
        }
        resultMap.put("rows",page.getContent());
        return resultMap;
    }

}
