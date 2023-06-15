package com.fengfengzi.chatserver.controller;
//
import com.fengfengzi.chatserver.common.R;
//import com.fengfengzi.chatserver.pojo.FileEntity;
import com.fengfengzi.chatserver.common.ResultEnum;
import com.fengfengzi.chatserver.pojo.FileEntity;
import com.fengfengzi.chatserver.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

import java.io.File;

import javax.annotation.Resource;

///**
// * @author 王丰
// * @version 1.0
// */
//
@RestController
@RequestMapping("/slipper/im/file")
public class FileController {

    @Resource
    FileService fileService;

    // @RequestParam
    @PostMapping("/upload")
    public R<FileEntity> upload(MultipartFile file) throws Exception {
        System.out.println("======文件上传请求======\n");
        R<FileEntity> r = new R<>();

        Map<String, Object> resMap = fileService.create(file);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            FileEntity data = (FileEntity) resMap.get("data");
            r.data(data);
            System.out.println("文件上传成功\n========");
        } else {
            r.error();
            System.out.println("文件上传失败\n========");
        }
//        r.data(fileEntity);

        return r;
    }

    @PostMapping("/upload/audio")
    public R<Object> uploadAudio(
            @RequestHeader("im-token") String token
    ) {
        System.out.println("catch audio up load");



        return null;
    }
//
}
