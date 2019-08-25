package com.pinyougou.sellergoods.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    /**
     * 查询所有的品牌
     * @return
     */
    List<TbBrand> findAll();

    /**
     * 分页查询
     * @param currPage
     * @param pageSize
     * @return
     */
    PageResult<TbBrand> findByPage(Integer currPage , Integer pageSize);

    /**
     *增加
     * @param brand
     */
    void save(TbBrand brand);

    /**
     * 通过id查询brand
     * @param id
     * @return
     */
    TbBrand findOne(long id);

    /**
     * 修改brand
     * @param brand
     * @return
     */
    void update(TbBrand brand);

    /**
     * 删除选中
     * @param ids
     */
    void selete(long[] ids);

    /**
     * 模糊条件查询
     * @return
     */
    PageResult<TbBrand> search(TbBrand brand,Integer currPage , Integer pageSize);

    /**
     * 查询所有产品按照指定字段返回
     * @return
     */
    List<Map> selectBrandList();

}
