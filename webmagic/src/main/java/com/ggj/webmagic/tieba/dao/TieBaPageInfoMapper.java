package com.ggj.webmagic.tieba.dao;

import java.util.List;

import com.ggj.webmagic.tieba.bean.TieBaPageInfo;

public interface TieBaPageInfoMapper {
    int insert(TieBaPageInfo tieBaPageInfo);
    Integer selectCountById(String pageId);
	void update(TieBaPageInfo tieBaPageInfo);
    List<TieBaPageInfo> selectAll();

}