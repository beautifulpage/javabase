package com.ggj.webmagic.tieba.service;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.ggj.webmagic.BaseTest;
import com.ggj.webmagic.tieba.bean.TieBaImage;


/**
 * 数据入库
 * @author:gaoguangjin
 * @date 2016/8/30 19:13
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TieBaImageServiceTest extends BaseTest{
    @Resource
    private TieBaImageService tieBaImageService;
    @Test
    public void sychReisToMySql() throws Exception {
    	TieBaImage tieBaImage = new TieBaImage();
    	tieBaImage.setId("e0219f3df8dcd100ba521351798b4710bb122fde");
    	tieBaImage.setAutherName("55");
    	tieBaImage.setPageId("5354788976");
    	tieBaImage.setImageUrl("https://imgsa.baidu.com/forum/w%3D580/sign=c9ceae8f30c79f3d8fe1e4388aa0cdbc/e0219f3df8dcd100ba521351798b4710bb122fde.jpg");
    	tieBaImage.setTiebaImageUrl("www.baidu.com");
    	tieBaImage.setCreatedAt(new Date());
    	tieBaImage.setUpdatedAt(new Date());
    	tieBaImageService.saveTiebaImg(tieBaImage);
    }

    public static void main(String[] args) {
        System.out.println("args = [" + 2%4 + "]");
    }
}