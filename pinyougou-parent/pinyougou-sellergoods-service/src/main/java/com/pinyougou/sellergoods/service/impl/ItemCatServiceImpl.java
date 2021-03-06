package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.pojo.TbItemCatExample.Criteria;
import com.pinyougou.sellergoods.service.ItemCatService;

import com.pinyougou.entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbItemCat> page=   (Page<TbItemCat>) itemCatMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKey(itemCat);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public String delete(Long[] ids) {
		String result=null;
		for(Long id:ids){
			List<TbItemCat> itemCats = findByParentId(id);
			if (itemCats!=null && itemCats.size()>0){
				result="删除失败--->请先查询下级--->删除下级后再删除该分类";
			}else {
				itemCatMapper.deleteByPrimaryKey(id);
				result="删除成功";
			}
		}
		return result;
	}
	
	
		@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		
		if(itemCat!=null){			
			if(itemCat.getName()!=null && itemCat.getName().length()>0){
				criteria.andNameLike("%"+itemCat.getName()+"%");
			}
			if (itemCat.getParentId()!=null){
				criteria.andParentIdEqualTo(itemCat.getParentId());
			}
		}

		Page<TbItemCat> page= (Page<TbItemCat>)itemCatMapper.selectByExample(example);
		List<TbItemCat> list = findAll();
		for (TbItemCat itemCat1 : list) {
			String name = itemCat1.getName();
			Long typeId = itemCat1.getTypeId();
			redisTemplate.boundHashOps("itemCatList").put(name,typeId);
		}
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 根据parentId分级查询
	 * 引入redis缓存,将结果加入到redis缓存中
	 * @param parentId
	 * @return
	 */
	@Override
	public List<TbItemCat> findByParentId(Long parentId) {
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbItemCat> list = findAll();
		for (TbItemCat itemCat : list) {
			String name = itemCat.getName();
			Long typeId = itemCat.getTypeId();
			redisTemplate.boundHashOps("itemCatList").put(name,typeId);
		}

		return itemCatMapper.selectByExample(example);
	}

}
