package com.pinyougou.sellergoods.service.impl;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import com.pinyougou.entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbSellerMapper sellerMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		TbGoods tbGoods = goods.getGoods();
		tbGoods.setAuditStatus("0");
		TbGoodsDesc tbGoodsDesc = goods.getGoodsDesc();

		//获取主键  给goods表添加数据
		goodsMapper.insert(tbGoods);
		//设置主键
		Long goodsId = tbGoods.getId();
		tbGoodsDesc.setGoodsId(goodsId);

		//添加数据  给goodsDesc表添加数据
		goodsDescMapper.insert(tbGoodsDesc);

		if ("1".equals(tbGoods.getIsEnableSpec())){
			//添加数据   给item表添加数据
			List<TbItem> itemList = goods.getItemList();
			for (TbItem item : itemList) {
				//设置标题title值
				String title = tbGoods.getGoodsName();
				//{"spec":{"网络":"移动3G","机身内存":"16G"},"price":8888,"num":9999,"status":0,"isDefault":0}
				String spec = item.getSpec();
				Map map = JSON.parseObject(spec, Map.class);
				Collection values = map.values();
				for (Object value : values) {
					title+=" " + value;
				}
				item.setTitle(title);
				//调用方法
				setItemValus(tbGoodsDesc,item,tbGoods);
				//添加数据
				itemMapper.insert(item);
			}
		}else {
			TbItem item = new TbItem();
			//设置标题title值
			String title = tbGoods.getGoodsName();
			item.setTitle(title);
			//设置价格
			item.setPrice(tbGoods.getPrice());
			//设置数量
			item.setNum(999);
			//设置是否默认
			item.setIsDefault("1");
			//设置状态
			item.setStatus("1");
			item.setSpec("{}");
			//调用方法
			setItemValus(tbGoodsDesc,item,tbGoods);
			//添加数据
			itemMapper.insert(item);
		}
	}

	/**
	 * 自定义方法来封装数据
	 * @param tbGoodsDesc
	 * @param item
	 * @param tbGoods
	 */
	public void setItemValus(TbGoodsDesc tbGoodsDesc,TbItem item,TbGoods tbGoods){
		//设置image为第一张图片
		String itemImages = tbGoodsDesc.getItemImages();
		List<Map> mapList = JSON.parseArray(itemImages, Map.class);
		//"goodsDesc":{"itemImages":[{"color":"白色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhV1pwUGAFfDdAACnXhSeGsk687.jpg"}]
		if (mapList.size()>0){
			String url = mapList.get(0).get("url").toString();
			item.setImage(url);
		}
		//设置分类id
		//"goods":{"category1Id":74,"category2Id":89,"category3Id":91,"typeTemplateId":35,"isEnableSpec":"1","goodsName":"哈哈","brandId":2,"caption":"娃哈哈","price":"25"}
		item.setCategoryid(tbGoods.getCategory3Id());
		//设置spuId
		item.setGoodsId(tbGoods.getId());
		//设置商家id
		item.setSellerId(tbGoods.getSellerId());
		//设置sku添加时间
		item.setCreateTime(new Date());
		//设置sku更新时间
		item.setUpdateTime(new Date());
		//设置分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
		item.setCategory(itemCat.getName());
		//设置品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
		item.setBrand(brand.getName());
		//设置商家名
		TbSeller seller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
		item.setSeller(seller.getNickName());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> itemList = itemMapper.selectByExample(example);
		Goods goods = new Goods();
		goods.setGoods(tbGoods);
		goods.setGoodsDesc(tbGoodsDesc);
		goods.setItemList(itemList);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
//				criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
