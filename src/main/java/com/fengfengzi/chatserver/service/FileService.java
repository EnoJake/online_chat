package com.fengfengzi.chatserver.service;
//
import com.fengfengzi.chatserver.common.ResultEnum;
import com.fengfengzi.chatserver.pojo.FileEntity;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

//
///**
// * @author 王丰
// * @version 1.0
// */
//
@Service
public class FileService {

    //FileEntity
    public Map<String, Object> create(MultipartFile file) throws IOException {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        System.out.println("contentType: " + contentType);
        System.out.println("originalFilename: " + originalFilename);

        // 文件后缀
        String extension = "";
        if (originalFilename != null) {
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex != -1 && lastDotIndex < originalFilename.length() - 1) {
                extension = originalFilename.substring(lastDotIndex + 1);
            }
        }

        // 文件名
        String uniqueID = UUID.randomUUID().toString(); // 生成UUID
        String fileName = uniqueID + "." + extension;
        System.out.println("fileName: " + fileName);

        // linux也可以这样子保存，哭了，呜呜呜
        String base = "/usr/local/nginx/html/file";
        String folder = "";
        if (extension.equalsIgnoreCase("webm")) {
            folder = "/audio";
        } else if (
                extension.equalsIgnoreCase("jpg")   ||
                extension.equalsIgnoreCase("jpeg")  ||
                extension.equalsIgnoreCase("png")   ||
                extension.equalsIgnoreCase("gif")) {
            folder = "/image";
        } else {
            folder = "/other";
        }
        base += folder;

        // String base = "E:\\IDEAProjects\\javaWeb\\MongoDB\\src\\main\\resources\\static\\file";
        File fileTo = new File(base, fileName);
        file.transferTo(fileTo);

        //String fileUrl = "http://124.223.50.19/file" + folder + "/" + fileName;
        String fileUrl = "http://fengfengzi.fun/file" + folder + "/" + fileName;
        System.out.println("url: " + fileUrl);

        FileEntity data = new FileEntity(
                originalFilename,
                fileName,
                fileUrl
        );

        map.put("data", data);

        code = ResultEnum.SUCCESS.getCode();
        msg = ResultEnum.SUCCESS.getMessage();

        map.put("code", code);
        map.put("msg", msg);

        return map;
    }
}
