package com.ggj.webmagic.tieba.bean;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TieBaImage {
    private String id;
    private String pageId;
    private String autherName;
    private String imageUrl;
    private String tiebaImageUrl;
    private Date createdAt;
    private Date updatedAt;

}