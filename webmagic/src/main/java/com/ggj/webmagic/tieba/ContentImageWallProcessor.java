package com.ggj.webmagic.tieba;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.ggj.webmagic.WebmagicService;
import com.ggj.webmagic.autoconfiguration.TieBaConfiguration;
import com.ggj.webmagic.autoconfiguration.TieBaImageIdMessageListener;
import com.ggj.webmagic.tieba.bean.ContentBean;
import com.ggj.webmagic.tieba.bean.TieBaImage;
import com.ggj.webmagic.tieba.service.TieBaImageService;
import com.ggj.webmagic.util.QiNiuUtil;
import com.ggj.webmagic.util.SpiderExtend;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author:gaoguangjin
 * @date 2016/8/24 14:36
 */
@Service
@Slf4j
public class ContentImageWallProcessor implements PageProcessor {
	@Autowired
	private TieBaConfiguration tieBaConfiguration;
	@Autowired
	private QiNiuUtil qiNiuUtil;
	@Autowired
	private TieBaImageService tieBaImageService;
	private static String url;
	private volatile static boolean isAddTarget = false;
	private static Set<String> pageNumberList = new HashSet<>();
	private static ConcurrentHashMap<byte[], byte[]> map = new ConcurrentHashMap<byte[], byte[]>();
	// 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Site site = Site.me().setRetryTimes(1).setSleepTime(1000);

	// 获取img标签正则
	private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
	// 获取src路径的正则
	private static final String IMGSRC_REG = "https:\"?(.*?)(\"|>|\\s+)";

	@Override
	public void process(Page page) {
		List<String> imageUrlList = Lists.newArrayList();
		String pageId = page.getUrl().toString().split("\\?")[0].replace(tieBaConfiguration.getTiebaContentPageUrl(),
				"");
		if (StringUtils.isNotEmpty(pageId)) {
			// Document doc =
			// Jsoup.connect("https://tieba.baidu.com/p/2211919471?&see_lz=1&red_tag=3173180955#!/l/p1").get();
			// String content = doc.getElementById("ag_main_list").innerhtml();
			File input = new File("/Users/abel/Documents/temp.html");
			Document doc2 = null;
			try {
				doc2 = Jsoup.parse(input, "UTF-8",
						"https://tieba.baidu.com/p/2211919471?&see_lz=1&red_tag=3173180955#!/l/p1");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Elements links = doc2.select("a.ag_ele_a_v > img");
			for (Element link : links) {
				String linkHref = link.attr("src");
				String linkText = link.text();
				imageUrlList.add(linkHref);
			}
			ContentBean cb = new ContentBean();
			cb.setAuthorName("520jaena");
			cb.setId("2211919471");
			cb.setTitle("图册");
			List<String> list = new ArrayList<>();
			for (String imageUrl : imageUrlList) {
				if (imageUrl.startsWith(tieBaConfiguration.getTiebaWallImageUrl())) {
					// 上传七牛
					imageUrl = convertImageUrl(imageUrl, cb);
					if (null != imageUrl)
						list.add(imageUrl);
				}
			}
			if (list.size() > 0) {
				map.put(WebmagicService
						.getByte(TieBaImageIdMessageListener.TIEBA_CONTENT_SINGLE_PAGE_IMAGE_KEY + cb.getId()),
						WebmagicService.getByte(JSONObject.toJSONString(list)));
			}
		}

		if (!isAddTarget) {
			for (String id : pageNumberList) {
				StringBuilder sb = new StringBuilder();
				sb.append(url).append(id);
				page.addTargetRequests(Arrays.asList(sb.toString()));
			}
			isAddTarget = true;
		}
	}

	/**
	 * 百度图片禁止图片外链，所以需要自己上传到七牛
	 * 
	 * @param imageUrl
	 */
	private String convertImageUrl(String imageUrl, ContentBean cb) {
		try {
			String qiniuUrlTml = cb.getAuthorName() + "|" + cb.getId() + "|"
					+ imageUrl.replace(tieBaConfiguration.getTiebaWallImageUrl(), "").split("/")[1];
			String retQNUrl = qiNiuUtil.upload(qiniuUrlTml, imageUrl);
			TieBaImage tieBaImage = new TieBaImage();
			tieBaImage.setId(imageUrl.replace(tieBaConfiguration.getTiebaWallImageUrl(), "").split("/")[1].split("\\.")[0]);
			tieBaImage.setAutherName(cb.getAuthorName());
			tieBaImage.setPageId(cb.getId());
			tieBaImage.setImageUrl(retQNUrl);
			tieBaImage.setTiebaImageUrl(imageUrl);
			tieBaImage.setCreatedAt(new Date());
			tieBaImage.setUpdatedAt(new Date());
			tieBaImageService.saveTiebaImg(tieBaImage);
			return retQNUrl;
		} catch (IOException e) {
			log.error("百度图片转换失败：" + e.getLocalizedMessage());
		}
		return null;
	}

	@Override
	public Site getSite() {
		return site;
	}

	public ConcurrentHashMap<byte[], byte[]> start() {
		isAddTarget = false;
		map.clear();
		pageNumberList.clear();
		if (!tieBaConfiguration.isFetchContentPageGroup()) {
			this.pageNumberList.addAll(Arrays.asList(tieBaConfiguration.getTiebaContentId().split(",")));
		} else {
			List<String> tiebaContentPageIds = TieBaConfiguration
					.filtPageContentId(tieBaConfiguration.getTiebaContentPageId());
			for (String idGroup : tiebaContentPageIds) {
				this.pageNumberList.addAll(Arrays.asList(idGroup.split(",")));
			}
		}
		this.url = tieBaConfiguration.getTiebaContentPageUrl();
		SpiderExtend.create(this).addUrl(url).addPipeline(new ConsolePipeline())
				// 开启5个线程抓取
				.thread(5)
				// 启动爬虫
				.run();
		return map;
	}
}
