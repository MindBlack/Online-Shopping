package com.pinyougou.search.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemSearch")
public class ItemSearchController {

    @Reference(timeout = 5000)
    private ItemSearchService itemSearchService;

    /**
     * 搜索关键字高亮显示结果
     * @param searchMap
     * @return
     */
    @RequestMapping("/search")
    public Map<String,Object> search(@RequestBody Map searchMap){
        return itemSearchService.search(searchMap);
    }

}
