package com.lxd.web.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RequestMapping("/media")
@RestController
public class MediaController {

    @Value("${fileConfig.path}")
    private String path;

    @GetMapping("/pic/{ml}/{name}")
    public void getMadia(@PathVariable("ml")String ml, @PathVariable("name") String name, HttpServletResponse response){
        String filePath = path+ml+"\\"+name;
        FileInputStream fis = null;
        ServletOutputStream out = null;
        try {
            File madia = new File(filePath);
            if(!madia.exists()){
                madia = new File(path+"\\default.jpg");
            }
            fis = new FileInputStream(madia);
            response.setHeader("content-type","image/jpeg");
            out = response.getOutputStream();
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = fis.read(b)) != -1){
                out.write(b,0,len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
                fis.close();
            } catch (IOException e) {
                System.out.println("关闭资源出错");
                e.printStackTrace();
            }
        }

    }

}
