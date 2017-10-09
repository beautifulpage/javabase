package com.ggj.webmagic.autoconfiguration;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Strings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * 贴吧Top爬虫配置项
 * @author:gaoguangjin
 * @date 2016/8/22 15:22
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix ="tieba")
public class TieBaConfiguration {
    private String[] tiebaName;
    private String tiebaTopUrl;
    //贴吧主页
    private String tiebaContentUrl;
    //贴吧帖子内容页面
    private String tiebaContentPageUrl;
    //贴吧帖子内容页面
    private String tiebaImageUrl;
    //贴吧图册帖子内容页面
    private String tiebaWallImageUrl;
    //贴吧帖子id  redis pub/sub topic名称
    private String tiebaContentIdTopic;
    private String tiebaContentIdSinglePageTopic;
    //贴吧帖子 不存在图片的提诶子id redis pub/sub topic名称
    private String tiebaContentNoImageIdTopic;
    //同步帖子最后一页
    private String tiebaContentPageEndNum;
    //是否执删除帖子图片的定时任务，默认false
    private String executeDeleteTiebaImageTask;
    //只看楼主
    private String tiebaContentSeeLzOnly;
    //是否只看第一页
    private boolean tiebaContentSeeFirstPage;
    //要抓取的单个帖子
    private String tiebaContentId;
    //是否抓取所有的贴吧分组
    private boolean fetchContentPageGroup;
    
    private List<String> tiebaContentPageId;

	public static List<String> filtPageContentId(List<String> ids){
		return ids.stream().filter(c -> !Strings.isNullOrEmpty(c)).distinct().collect(Collectors.toList());
	}
    
}
