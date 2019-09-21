package com.pinyougou.page.serveice;


public interface ItemPageService {

    /**
     * 生成商品详细页
     */
    boolean genItemHtml(Long goodsId);

    /**
     * 删除商品详细页
     * @param goodsIds
     * @return
     */
    public boolean deleteitemHtml(Long[] goodsIds);

}
