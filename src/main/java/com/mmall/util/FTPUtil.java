package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by guolin
 */
@Slf4j
public class FTPUtil {

    // 拿到ip,user,pass
    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;

    private int port;

    private String user;

    private String pwd;

    private FTPClient ftpClient;

    /**
     * 构造器
     * @param ip
     * @param port
     * @param user
     * @param pwd
     */
    public FTPUtil(String ip, int port, String user, String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    /**
     * 上传文件的静态方法，支持批量上传
     * @param fileList
     * @return
     * @throws IOException
     */
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        log.info("开始连接ftp服务器");
        boolean result = ftpUtil.uploadFile("img",fileList);
        log.info("开始连接ftp服务器,结束上传,上传结果:{}");
        return result;
    }

    /**
     * 上传的具体逻辑
     * @param remotePath
     * @param fileList
     * @return
     * @throws IOException
     */
    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;

        // 连接FTP服务器,如果连接成功，再继续调用文件上传的功能
        if(connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                // 切换文件目录，如果remotePath为空，则不切换
                ftpClient.changeWorkingDirectory(remotePath);

                // 设置文件转移时候的一次性读取大小，缓冲区大小
                ftpClient.setBufferSize(1024);

                // ControlEncoding
                ftpClient.setControlEncoding("UTF-8");

                // 设置文件转移的时候，文件的格式
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

                // 打开本地的被动模式
                // FTP客户端连接到FTP服务器的21端口，发送用户名和密码登录，登录成功后要list列表或者读取数据时，发送PASV命令到FTP服务器， 服务器在本地随机开放一个端口（1024以上），然后把开放的端口告诉客户端， 客户端再连接到服务器开放的端口进行数据传输
                ftpClient.enterLocalPassiveMode();

                for(File fileItem : fileList){
                    // 创建一个Input流
                    fis = new FileInputStream(fileItem);

                    // 指定文件名和Input流
                    ftpClient.storeFile(fileItem.getName(),fis);
                }

            } catch (IOException e) {
                log.error("上传文件异常",e);
                uploaded = false;
                e.printStackTrace();
            } finally {
                // 释放连接
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    /**
     * 连接ftp服务器
     * @param ip
     * @param port
     * @param user
     * @param pwd
     * @return
     */
    private boolean connectServer(String ip,int port,String user,String pwd){
        boolean isSuccess = false;

        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pwd);
        } catch (IOException e) {
            log.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
