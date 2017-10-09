package com.ggj.webmagic.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtil {

	public static boolean makeDir(String targetdir) {
		File dir = new File(targetdir);
		boolean isExitDir = true;
		if (!dir.exists() && !dir.isDirectory()) {
			if (dir.mkdirs()) {
				log.info("创建了文件夹：{}", targetdir);
			} else {
				log.info("文件夹创建失败：{}", targetdir);
			}
			isExitDir = false;
		}
		return isExitDir;
	}

	public static void main(String[] args) {
		getFile("小白兔子miu");
	}

	public static Map<String, byte[]> getFileImg(String name){
		Map<String, String> names = getFile(name);
		Map<String, byte[]> bytes = Maps.newHashMap();
		for (String str : names.keySet()) {
			try {
				bytes.put(str, InputStream2ByteArray(names.get(str)));
			} catch (IOException e) {
				log.error("获取文件异常:{}", e);
				e.printStackTrace();
			}
		}
		return bytes;
	}
	
	public static Map<String, String> getFile(String name) {
		String path = "/Users/abel/.m2/repository/com/360/360/" + name;
		File f = new File(path);
		Map<String, String> map = Maps.newHashMap();
		if (!f.exists()) {
			System.out.println(path + " not exists");
			return map;
		}
		File fa[] = f.listFiles();
		String[] keys = null;
		String key = null;
		for (File file : fa) {
			keys = file.getAbsolutePath().split("\\/");
			key = keys[keys.length-1].split("\\.")[0];
			//System.out.println(key+"***"+file.getAbsolutePath());
			map.put(key, file.getAbsolutePath());
		}
		return map;
	}

	public static byte[] InputStream2ByteArray(String filePath) throws IOException {
		InputStream in = new FileInputStream(filePath);
		byte[] data = toByteArray(in);
		in.close();
		return data;
	}

	public static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 4];
		int n = 0;
		while ((n = in.read(buffer)) != -1) {
			out.write(buffer, 0, n);
		}
		return out.toByteArray();
	}

}
