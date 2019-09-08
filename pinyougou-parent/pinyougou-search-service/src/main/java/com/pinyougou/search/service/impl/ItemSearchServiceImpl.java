package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;


@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 搜索框输入关键字显示  输入的关键字高亮显示
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map searchMap) {
        //1.结果高亮显示
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Map searchList = searchList(searchMap);
        resultMap.putAll(searchList);

        //2.根据关键字查询商品分类
        List categoryList = searchCategoryList(searchMap);
        resultMap.put("categoryList", categoryList);

        //3.查询品牌和规格列表
        Object category = searchMap.get("category");
        if (category != null || "".equals(category)) {
            if (categoryList != null && categoryList.size() > 0) {
                String firstCategoryName = (String) categoryList.get(0);
                Map brandAndSpecList = getBrandAndSpecList(firstCategoryName);
                resultMap.putAll(brandAndSpecList);
            }
        } else {
            Map brandAndSpecList = getBrandAndSpecList(String.valueOf(category));
            resultMap.putAll(brandAndSpecList);
        }
        return resultMap;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 删除索引库
     * @param goodsIdList
     */
    @Override
    public void deleteGoodsIds(List goodsIdList) {
        System.out.println("删除商品 id ： "+goodsIdList);
        Query query = new SimpleQuery();
        Criteria criteria  = new Criteria("item_goodsId").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 定义私有方法关键字搜索结果高亮显示
     *
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap) {
        //设置去掉用户输入的关键字
        String keywords = (String) searchMap.get("keywords");
        keywords = keywords.replace(" ", "");
        //接受的map集合
        Map<String, Object> resultMap = new HashMap<String, Object>();
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
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);
        //1.1根据分类名称查询
        if (searchMap.get("category") != null && !"".equals(searchMap.get("category"))) {
            FilterQuery filterQuery = new SimpleFacetQuery();
            filterQuery.addCriteria(new Criteria("item_category").is(searchMap.get("category")));
            query.addFilterQuery(filterQuery);
        }

        //1.2根据品牌名称查询
        if (searchMap.get("brand") != null && !"".equals(searchMap.get("brand"))) {
            FilterQuery filterQuery = new SimpleFacetQuery();
            filterQuery.addCriteria(new Criteria("item_brand").is(searchMap.get("brand")));
            query.addFilterQuery(filterQuery);
        }

        //1.3根据规格过滤
        if (searchMap.get("spec") != null) {
            Map<String, String> spec = (Map<String, String>) searchMap.get("spec");
            for (String specName : spec.keySet()) {
                String optionName = spec.get(specName);
                FilterQuery filterQuery = new SimpleFacetQuery();
                filterQuery.addCriteria(new Criteria("item_spec" + specName).is(optionName));
                query.addFilterQuery(filterQuery);
            }
        }

        //1.4价格的过滤
        if (searchMap.get("price") != null && !"".equals(searchMap.get("price"))) {
            String[] prices = searchMap.get("price").toString().split("-");
            String lowerPrice = prices[0];
            String upperPrice = prices[1];
            FilterQuery filterQuery = new SimpleFacetQuery();
            filterQuery.addCriteria(new Criteria("item_price").greaterThanEqual(lowerPrice));
            if (!upperPrice.equals("*")) {
                filterQuery.addCriteria(new Criteria("item_price").lessThanEqual(upperPrice));
            }
            query.addFilterQuery(filterQuery);
        }

        //1.5分页
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            pageSize = 20;
        }
        query.setOffset((pageNo - 1) * pageSize);
        query.setRows(pageSize);

        //1.6价格排序
        String sortValue = (String) searchMap.get("sort");  //排序方式
        String sortFiled = (String) searchMap.get("sortFiled");  //排序字段
        if (sortFiled!=null && !sortFiled.equals("")){
            if (sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortFiled);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortFiled);
                query.addSort(sort);
            }
        }


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
        resultMap.put("rows", page.getContent());
        resultMap.put("totalPages", page.getTotalPages());  //返回总页数
        resultMap.put("total", page.getTotalElements());    //返回总记录数
        return resultMap;
    }

    /**
     * 定义私有方法来查询列表
     *
     * @param searchMap
     * @return
     */
    private List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList();
        //分组条件创建
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //执行分组查询
        GroupPage<TbItem> Page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //解析分组结果
        GroupResult<TbItem> pageGroupResult = Page.getGroupResult("item_category");
        List<GroupEntry<TbItem>> content = pageGroupResult.getGroupEntries().getContent();

        for (GroupEntry<TbItem> groupEntry : content) {
            list.add(groupEntry.getGroupValue());
        }
        return list;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获得满足条件的分类名称
     *
     * @param categoryName
     * @return
     */
    private Map getBrandAndSpecList(String categoryName) {
        Long templateId = (Long) redisTemplate.boundHashOps("itemCatList").get(categoryName);
        Map map = new HashMap();
        if (templateId != null) {
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(templateId);
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(templateId);
            map.put("brandList", brandList);
            map.put("specList", specList);
        }
        return map;
    }

}
