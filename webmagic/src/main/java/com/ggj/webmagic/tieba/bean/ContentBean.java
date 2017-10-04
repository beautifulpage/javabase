package com.ggj.webmagic.tieba.bean;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;

import lombok.Getter;
import lombok.Setter;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;

/**
 * teiba 内容
 * @author:gaoguangjin
 * @date 2016/8/26 17:15
 */
@Getter
@Setter
public class ContentBean {
    //贴子id
    private String id;
    //贴吧名称
    private String name;
    //帖子作者名称
    private String authorName;
    //帖子标题
    private String title;
    //最后更新时间
    private String  date;

    public ContentBean(String pageId, String date, String tiebaName,String authorName,String title) {
        this.id=pageId;
        this.date=date;
        this.name=tiebaName;
        this.authorName=authorName;
        this.title=title;
    }
    public ContentBean(String pageId, String tiebaName,String authorName,String title) {
    	this.id=pageId;
    	this.name=tiebaName;
    	this.authorName=authorName;
    	this.title=title;
    }
    public ContentBean(String pageId,  String tiebaName) {
        this.id=pageId;
        this.name=tiebaName;
    }
    public ContentBean() {
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentBean contentBean = (ContentBean) o;
        if (!id.equals(contentBean.id)) return false;
        return title.equals(contentBean.title);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + title.hashCode();
        return result;
    }
    
    public static ContentBean parseHtml(Page page){
    	Html pageHtml = page.getHtml();
		Pattern pattern = Pattern.compile("<[^>]+>");
		
		String tiebaName = "";
		String authorName = "";
		String title = "";
		
		List<String> card_title_fnames = pageHtml.$(".card_title_fname").all();
		List<String> p_author_names = pageHtml.$(".p_author_name").all();
		List<String> core_title_txts = pageHtml.$(".core_title_txt").all();
		if(CollectionUtils.isNotEmpty(card_title_fnames)){
			String card_title_fname = pageHtml.$(".card_title_fname").all().get(0);
			Matcher matcher_tiebaName = pattern.matcher(card_title_fname);
			tiebaName = matcher_tiebaName.replaceAll("");
		}
		if(CollectionUtils.isNotEmpty(p_author_names)){
			String p_author_name = pageHtml.$(".p_author_name").all().get(0);
			Matcher matcher_authorName = pattern.matcher(p_author_name);
			authorName = matcher_authorName.replaceAll("");
		}
		if(CollectionUtils.isNotEmpty(core_title_txts)){
			String core_title_txt = pageHtml.$(".core_title_txt").all().get(0);
			Matcher matcher_title = pattern.matcher(core_title_txt);
			title = matcher_title.replaceAll("");
		}
		
		return new ContentBean(null, tiebaName, authorName, title);
    	
    }
}
