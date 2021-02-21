package com.lxd.web.rest;

import com.lxd.mode.Video;
import com.lxd.utils.FileUtils;
import com.lxd.web.dto.ReturnModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@RequestMapping("/video")
@RestController
public class VideoController {

    @Value("${fileConfig.path}")
    private String path;

    @GetMapping("/list")
    public ResponseEntity getVideoList(){
        ReturnModel<List> result = new ReturnModel<>();
        try {
            List<Video> data = FileUtils.getFileList(path);
            result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            result.fail(e.toString());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/player/{ml}/{name}")
    public void player(@PathVariable("ml")String ml, @PathVariable("name") String name, HttpServletRequest request, HttpServletResponse response){
        String filePath = path+ml+"\\"+name;
        File downloadFile = new File(filePath);
        try {
            if (!downloadFile.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            long fileLength = downloadFile.length();// 记录文件大小
            long pastLength = 0;// 记录已下载文件大小
            int rangeSwitch = 0;// 0：从头开始的全文下载；1：从某字节开始的下载（bytes=27000-）；2：从某字节开始到某字节结束的下载（bytes=27000-39000）
            long contentLength = 0;// 客户端请求的字节总量
            String rangeBytes = "";// 记录客户端传来的形如“bytes=27000-”或者“bytes=27000-39000”的内容
            RandomAccessFile raf = null;// 负责读取数据
            OutputStream os = null;// 写出数据
            OutputStream out = null;// 缓冲
            int bsize = 1024;// 缓冲区大小
            byte b[] = new byte[bsize];// 暂存容器

            String range = request.getHeader("Range");
            int responseStatus = 206;
            if (range != null && range.trim().length() > 0 && !"null".equals(range)) {// 客户端请求的下载的文件块的开始字节
                responseStatus = javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT;
                rangeBytes = range.replaceAll("bytes=", "");
                if (rangeBytes.endsWith("-")) {
                    rangeSwitch = 1;
                    rangeBytes = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                    pastLength = Long.parseLong(rangeBytes.trim());
                    contentLength = fileLength - pastLength;
                } else {
                    rangeSwitch = 2;
                    String temp0 = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                    String temp2 = rangeBytes.substring(rangeBytes.indexOf('-') + 1, rangeBytes.length());
                    pastLength = Long.parseLong(temp0.trim());
                }
            } else {
                contentLength = fileLength;// 客户端要求全文下载
            }

            // 清除首部的空白行
            response.reset();
            // 告诉客户端允许断点续传多线程连接下载,响应的格式是:Accept-Ranges: bytes
            response.setHeader("Accept-Ranges", "bytes");
            // 如果是第一次下,还没有断点续传,状态是默认的 200,无需显式设置;响应的格式是:HTTP/1.1

            if (rangeSwitch != 0) {
                response.setStatus(responseStatus);
                // 不是从最开始下载，断点下载响应号为206
                // 响应的格式是:
                // Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
                switch (rangeSwitch) {
                    case 1: {
                        String contentRange = new StringBuffer("bytes ")
                                .append(new Long(pastLength).toString()).append("-")
                                .append(new Long(fileLength - 1).toString())
                                .append("/").append(new Long(fileLength).toString())
                                .toString();
                        response.setHeader("Content-Range", contentRange);
                        break;
                    }
                    case 2: {
                        String contentRange = range.replace("=", " ") + "/"
                                + new Long(fileLength).toString();
                        response.setHeader("Content-Range", contentRange);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            } else {
                String contentRange = new StringBuffer("bytes ").append("0-")
                        .append(fileLength - 1).append("/").append(fileLength)
                        .toString();
                response.setHeader("Content-Range", contentRange);
            }
            response.setContentType("video/mp4;charset=UTF-8");
            response.setHeader("Content-Length", String.valueOf(contentLength));
            os = response.getOutputStream();
            out = new BufferedOutputStream(os);
            raf = new RandomAccessFile(downloadFile, "r");
            try {
                long outLength = 0;// 实际输出字节数
                switch (rangeSwitch) {
                    case 0: {
                    }
                    case 1: {
                        raf.seek(pastLength);
                        int n = 0;
                        while ((n = raf.read(b)) != -1) {
                            out.write(b, 0, n);
                            outLength += n;
                        }
                        break;
                    }
                    case 2: {
                        raf.seek(pastLength);
                        int n = 0;
                        long readLength = 0;// 记录已读字节数
                        while (readLength <= contentLength - bsize) {// 大部分字节在这里读取
                            n = raf.read(b);
                            readLength += n;
                            out.write(b, 0, n);
                            outLength += n;
                        }
                        if (readLength <= contentLength) {// 余下的不足 1024 个字节在这里读取
                            n = raf.read(b, 0, (int) (contentLength - readLength));
                            out.write(b, 0, n);
                            outLength += n;
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
                out.flush();
            } catch (IOException e){

            }
            finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e) {
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }
    }

}
