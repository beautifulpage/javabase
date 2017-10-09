package com.ggj.webmagic.tieba.bean;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TieBaPageInfo {
	private String pageId;
    private String autherName;
    private String title;
    private Date createdAt;
    private Date updatedAt;

}