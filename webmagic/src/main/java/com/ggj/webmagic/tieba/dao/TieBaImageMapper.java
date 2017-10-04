package com.ggj.webmagic.tieba.dao;

import com.ggj.webmagic.tieba.bean.TieBaImage;

import java.util.List;

public interface TieBaImageMapper {
    int insertBatch(List<TieBaImage> list);
    int insert(TieBaImage tieBaImage);
    Integer selectCountById(String id);
    List<String> pkSelectByParam(TieBaImage tieBaImage);
    List<TieBaImage> selectByIds(List<String> ids);
	void update(TieBaImage tieBaImage);
}