package com.lxd.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.lxd.mode.Video;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.json.JSONParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static List<Video> getFileList(String path){
        List<Video> result = new ArrayList<>();

        File ml = new File(path);

        File[] files = ml.listFiles();

        for (File file : files){
            if(file.isDirectory()){
                Video video = new Video();
                File[] nexts = file.listFiles();
                video.setMl(file.getName());
                video.setTitle(file.getName());
                video.setSm("暂无详细信息");
                for (File item : nexts){
                    if(item.getName().contains(".jpg")){
                        video.setMedia(item.getName());
                    }
                    if(item.getName().contains(".mp4")){
                        video.setVideoName(item.getName());
                        video.setFileSize(item.length());
                    }
                }
                if(StringUtils.isNotBlank(video.getVideoName())){
                    result.add(video);
                }
            }
        }
        return result;
    }


    public static void main(String[] args) {

        getFileList("");
    }
}
