package com.ggj.webmagic.autoconfiguration;

import static com.ggj.webmagic.WebmagicService.getByte;
import static com.ggj.webmagic.WebmagicService.getString;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.ggj.webmagic.WebmagicService;
import com.ggj.webmagic.tieba.ContentImageWallProcessor;
import com.ggj.webmagic.tieba.ContentSinglePageImageProcessor;
import com.ggj.webmagic.tieba.bean.ContentBean;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

/**
 * redis pub/sub 订阅者
 * @author:gaoguangjin
 * @date 2016/8/24 18:06
 */
@Slf4j
@Component
public class TieBaSinglePageImageIdMessageListener implements MessageListener {
	
	public static final String TIEBA_CONTENT_SINGLE_PAGE_IMAGE_INDEX = "tieba_content_single_page_image_index";
	public static final String TIEBA_CONTENT_SINGLE_PAGE_IMAGE_KEY = "tieba_content_single_page_image_";
	
	@Autowired
	private ContentSinglePageImageProcessor contentSinglePageImageProcessor;
	@Autowired
	private ContentImageWallProcessor contentImageWallProcessor;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 所有帖子ID，部分帖子包含图片
	 * @param message
	 * @param bytes
     */
	@Override
	public void onMessage(Message message, byte[] bytes) {
		String jsonStr = WebmagicService.getString(message.getBody());
		List<ContentBean> pageList = JSONObject.parseArray(jsonStr, ContentBean.class);
		if (pageList != null && pageList.size() > 0) {
			Map<String, ContentBean> mapContent = Maps.newHashMap();
			for (ContentBean contentBean : pageList) {
				mapContent.put(contentBean.getId(), contentBean);
			}
			
			log.info("{}:待同步图片page数量：{}" ,mapContent.toString(), mapContent.size());
			///ConcurrentHashMap<byte[], byte[]> map = contentSinglePageImageProcessor.start();
			ConcurrentHashMap<byte[], byte[]> map = contentImageWallProcessor.start();
			redisTemplate.executePipelined(new RedisCallback<Object>() {
				@Override
				public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
					//保存所有帖子 tieba_content_image_4813146001
					//if(map.size()>0)
					//redisConnection.mSet(map);
					if(MapUtils.isNotEmpty(map)){
						String pageId = null;
						ContentBean cb = null;
						String redisKey = null;
						String redisIndexValue = null;
						for (byte[] b : map.keySet()) {
							pageId = getString(b).replace(TIEBA_CONTENT_SINGLE_PAGE_IMAGE_KEY, "");
							cb = mapContent.get(pageId);
							redisKey = TIEBA_CONTENT_SINGLE_PAGE_IMAGE_KEY + cb.getAuthorName() + "_" + pageId;
							redisConnection.sAdd(getByte(redisKey),map.get(b));
							
							//key=TIEBA_CONTENT_SINGLE_PAGE_IMAGE_INDEX
							redisIndexValue = cb.getAuthorName() + "_" + pageId + "|" + cb.getTitle();
							redisConnection.sAdd(getByte(TIEBA_CONTENT_SINGLE_PAGE_IMAGE_INDEX),WebmagicService.getByte(redisIndexValue));
						}
					}
					return null;
				}
			});
		}
	}

}
