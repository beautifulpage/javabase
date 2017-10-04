package com.ggj.webmagic.tieba.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ggj.webmagic.tieba.bean.TieBaImage;
import com.ggj.webmagic.tieba.dao.TieBaImageMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Slf4j
public class TieBaImageService {

	@Autowired
	private TieBaImageMapper tieBaImageMapper;
	
	@Transactional(readOnly = false)
	public void saveTiebaImg(TieBaImage tieBaImage){
		Integer count = tieBaImageMapper.selectCountById(tieBaImage.getId());
		if(count == null || count.intValue() == 0){
			tieBaImageMapper.insert(tieBaImage);
		}else{
			tieBaImageMapper.update(tieBaImage);
		}
	}
	

}
