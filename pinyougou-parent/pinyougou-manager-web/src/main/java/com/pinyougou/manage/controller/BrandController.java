package com.pinyougou.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询所有的品牌
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /**
     * 分页查询所有的品牌
     * @param currPage
     * @param pageSize
     * @return
     */
    @RequestMapping("/findByPage")
    public PageResult<TbBrand> findByPage(Integer currPage,Integer pageSize){
        return brandService.findByPage(currPage,pageSize);
    }

    /**
     * 添加brand
     * @param brand
     * @return
     */
    @RequestMapping("/add")
    public Result save(@RequestBody TbBrand brand){
        try {
            brandService.save(brand);
            return new Result(true , "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    /**
     * 通过id查询
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(long id){
        return brandService.findOne(id);
    }

    /**
     * 修改brand
     * @param brand
     * @return
     */
    @RequestMapping("/update")
    //{"firstChar":"T","id":26,"name":"特步L"}  // 加注解@RequestBody
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    /**
     * 删除选中
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(long[] ids){
        try {
            brandService.selete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    /**
     * 模糊条件查询
     * @param brand
     * @param currPage
     * @param pageSize
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,Integer currPage,Integer pageSize){
        return brandService.search(brand,currPage,pageSize);
    }

    /**
     * 查询所有的brand按照指定格式返回
     * @return
     */
    @RequestMapping("/selectBrandList")
    List<Map> selectBrandList(){
        return brandService.selectBrandList();
    }

}
