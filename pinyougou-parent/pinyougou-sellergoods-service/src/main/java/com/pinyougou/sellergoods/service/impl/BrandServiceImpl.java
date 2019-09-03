package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import org.apache.zookeeper.data.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    /**
     * 查询所有
     * @return
     */
    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    /**
     * 分页查询
     * @param currPage
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<TbBrand> findByPage(Integer currPage, Integer pageSize) {
        PageHelper.startPage(currPage,pageSize);
        List<TbBrand> brandList = brandMapper.selectByExample(null);
        PageInfo<TbBrand> pageInfo = new PageInfo<>(brandList);
        return new PageResult<>(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 增加
     * @param brand
     */
    @Override
    public void save(TbBrand brand) {
        brandMapper.insert(brand);
    }

    /**
     * 通过id查询brand
     * @param id
     * @return
     */
    @Override
    public TbBrand findOne(long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 修改brand
     * @param brand
     */
    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    /**
     * 删除选中
     * @param ids
     */
    @Override
    public void selete(long[] ids) {
        for (long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    /**
     * 模糊搜索查询分页
     * @param brand
     * @param currPage
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<TbBrand> search(TbBrand brand, Integer currPage, Integer pageSize) {
        PageHelper.startPage(currPage,pageSize);
        TbBrandExample brandExample = new TbBrandExample();
        TbBrandExample.Criteria criteria = brandExample.createCriteria();
        if (brand!=null){
            if (brand.getName()!=null && !"".equals(brand.getName())){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if (brand.getFirstChar()!=null && !"".equals(brand.getFirstChar())){
                criteria.andFirstCharLike(brand.getFirstChar());
            }
        }
//        Page page = (Page) brandMapper.selectByExample(brandExample);
//        long total = page.getTotal();
//        List result = page.getResult();
//        PageResult pageResult = new PageResult<>(total, result);
        List<TbBrand> brandList = brandMapper.selectByExample(brandExample);
        PageInfo<TbBrand> pageInfo = new PageInfo<>(brandList);
        PageResult<TbBrand> pageResult = new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
        return pageResult;
    }

    /**
     * 查询商品按照指定集合返回
     * @return
     */
    @Override
    public List<Map> selectBrandList() {
        return brandMapper.selectBrandList();
    }

}
