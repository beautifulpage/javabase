package com.ggj.webmagic.tieba;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.ggj.webmagic.autoconfiguration.TieBaConfiguration;
import com.ggj.webmagic.tieba.bean.ContentBean;
import com.ggj.webmagic.util.SpiderExtend;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 爬虫获取帖子的id 例如：http://tieba.baidu.com/p/4747953342，4747953342就是我们想要的id
 * 
 * @author:gaoguangjin
 * @date 2016/8/19 0:56
 */
@Service
@Slf4j
public class ContentIdSinglePageProcessor implements PageProcessor {
	private volatile static boolean isAddTarget = false;
	// 默认就同步2*50 100个帖子
	private Integer endNum;
	private final Integer pageSize = 50;
	private static List<ContentBean> pageNumberList = new ArrayList<>();
	private static List<String> tiebaContentIds = new ArrayList<>();
	@Autowired
	private TieBaConfiguration tieBaConfiguration;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	private static String tiebaContentUrl;
	// 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Site site = Site.me().setRetryTimes(1).setSleepTime(1000);

	@Override
	public void process(Page page) {
		String pageId = page.getUrl().toString().split("\\?")[0].replace(tieBaConfiguration.getTiebaContentPageUrl(),
				"");
		if (StringUtils.isNotBlank(pageId)) {
			ContentBean cb = ContentBean.parseHtml(page);
			cb.setId(pageId);
			log.info("ContentIdSinglePageProcessor:{}", cb);
			pageNumberList.add(cb);
		}

		if (!isAddTarget) {
			List<String> pageContentId = tiebaContentIds;
			for (String contentId : pageContentId) {
				StringBuilder sb = new StringBuilder();
				sb.append(tiebaContentUrl).append(contentId);
				page.addTargetRequests(Arrays.asList(sb.toString()));
			}
			isAddTarget = true;
		}
	}

	@Override
	public Site getSite() {
		return site;
	}

	public void start() {
		isAddTarget = false;
		pageNumberList.clear();
		if (!tieBaConfiguration.isFetchContentPageGroup()) {
			tiebaContentIds.addAll(Arrays.asList(tieBaConfiguration.getTiebaContentId().split(",")));
		} else {
			
			List<String> tiebaContentPageIds = TieBaConfiguration.filtPageContentId(tieBaConfiguration.getTiebaContentPageId());
			for (String idGroup : tiebaContentPageIds) {
				tiebaContentIds.addAll(Arrays.asList(idGroup.split(",")));
			}
		}
		this.tiebaContentUrl = tieBaConfiguration.getTiebaContentPageUrl();
		SpiderExtend.create(this).addUrl(tiebaContentUrl).addPipeline(new ConsolePipeline()).thread(1).run();
		if (pageNumberList.size() > 0) {
			redisTemplate.convertAndSend(tieBaConfiguration.getTiebaContentIdSinglePageTopic(),
					JSONObject.toJSONString(pageNumberList));
		}
	}
}
