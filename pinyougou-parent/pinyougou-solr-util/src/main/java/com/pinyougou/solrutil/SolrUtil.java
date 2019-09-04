package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 导入数据
     */
    public void importItemData(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1"); //审核通过
        List<TbItem> itemList = itemMapper.selectByExample(example);
        System.out.println("===商品列表===");
        for (TbItem item : itemList) {
            ////将spec字段中的json字符串转换为map//将spec字段中的json字符串转换为map
            Map specMap = JSON.parseObject(item.getSpec());
            item.setSpecMap(specMap);
            System.out.println(item.getTitle());
        }

        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();

        System.out.println("===结束===");

    }

    public static void main(String[] args) {
        ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");

        SolrUtil solrUtil = (SolrUtil) app.getBean("solrUtil");

        solrUtil.importItemData();
    }


}
