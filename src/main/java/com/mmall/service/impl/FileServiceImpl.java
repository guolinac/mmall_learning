package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by guolin
 */
@Service("iFileService")
@Slf4j
public class FileServiceImpl implements IFileService {

    /**
     * 上传文件
     * @param file
     * @param path
     * @return
     */
    public String upload(MultipartFile file,String path){
        // 拿到要上传文件的原始文件名，全名
        String fileName = file.getOriginalFilename();

        // 获得文件扩展名
        // abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);

        // UUID.randomUUID()得到一个唯一的uuid
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;

        log.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            // 赋予可写权限
            fileDir.setWritable(true);
            // 创建文件夹
            fileDir.mkdirs();
        }

        File targetFile = new File(path,uploadFileName);

        try {
            // 将file移动到targetfile
            file.transferTo(targetFile);

            // 将targetfile上传到ftp服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            // 上传完之后，删除webapp下targetfile文件
            targetFile.delete();

        } catch (IOException e) {
            log.error("上传文件异常",e);
            return null;
        }
        //A:abc.jpg
        //B:abc.jpg
        return targetFile.getName();
    }

}
