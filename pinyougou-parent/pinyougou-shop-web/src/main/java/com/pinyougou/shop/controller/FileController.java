package com.pinyougou.shop.controller;

import com.pinyougou.entity.Result;
import com.pinyougou.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传与下载controller层
 */
@RestController
public class FileController {

    @Value("${tracker_server_url}")
    private String trackerServerUrl;
    @RequestMapping("/upload")
    public Result uploadFile(MultipartFile upload){
        try {
            //获取文件名后缀
            String originalFilename = upload.getOriginalFilename();
            String extensionName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //加载配置文件
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            String uploadFile = fastDFSClient.uploadFile(upload.getBytes(), extensionName);
            String complationUrl = trackerServerUrl + uploadFile;
            System.out.println(complationUrl);
            return new Result(true,complationUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }




}
