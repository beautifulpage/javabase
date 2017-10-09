package com.ggj.webmagic.tieba.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ggj.webmagic.tieba.bean.TieBaPageInfo;
import com.ggj.webmagic.tieba.dao.TieBaPageInfoMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Slf4j
public class TieBaPageInfoService {

	@Autowired
	private TieBaPageInfoMapper tieBaPageInfoMapper;

	@Transactional(readOnly = false)
	public void saveTiebaPageInfo(List<String> vals) {
		String autherName = null;
		String pageId = null;
		String title = null;
		String[] strArr = null;
		int i = 0;
		for (String str : vals) {
			++i;
			log.info("str:{},i:{}",str,i);
			autherName = str.split("_")[0];
			strArr = str.split("_")[1].split("\\|");
			TieBaPageInfo tieBaPageInfo = new TieBaPageInfo();
			tieBaPageInfo.setAutherName(autherName);
			tieBaPageInfo.setPageId(strArr[0]);
			tieBaPageInfo.setTitle(strArr[1]);
			tieBaPageInfo.setCreatedAt(new Date());
			tieBaPageInfo.setUpdatedAt(new Date());
			Integer count = tieBaPageInfoMapper.selectCountById(strArr[0]);
			if(count == null || count.intValue() == 0){
				tieBaPageInfoMapper.insert(tieBaPageInfo);
			}else{
				tieBaPageInfoMapper.update(tieBaPageInfo);
			}
		}

	}

}
