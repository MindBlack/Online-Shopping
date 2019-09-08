package com.pinyougou.search.service;


import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 搜索关键字高亮显示结果
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map searchMap);

    /**
     * 数据导入
     * @param list
     */
    public void importList(List list);

    public void deleteGoodsIds(List goodsIdList);

}
