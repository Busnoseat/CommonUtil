package ;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * FTPFile  登录 上传 下载 删除功能
 */
public class FtpUtil {
    private static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    /**
     * 获取文件列表
     * @param hostname
     * @param port
     * @param username
     * @param password
     * @param pathname
     * @return
     */
    public static FTPFile[] returnFile(String hostname, int port, String username, String password, String pathname) {
        FTPClient ftpClient = new FTPClient();
        FTPFile[] ftpFiles = null;
        try {
            ftpClient = loginFTP(hostname, port, username, password, pathname);
            ftpFiles = ftpClient.listFiles();
            ftpClient.logout();
            return ftpFiles;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                } catch (IOException e) {

                }
            }
        }
        return ftpFiles;
    }

    /**
     * FTP上传文件
     * @param hostname
     * @param port
     * @param username
     * @param password
     * @param pathname
     * @param filename
     * @param input
     * @return
     */
    public static boolean Upload(String hostname, int port, String username, String password, String pathname, String filename, InputStream input) {
        boolean returnValue = false;
        FTPClient ftpClient = loginFTP(hostname, port, username, password, pathname);
        try {
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                logger.error("FTP服务器拒绝连接");
                return false;
            }
            ftpClient.storeFile(filename, input);
            input.close();
        } catch (IOException e) {
            logger.error("FTP客户端出错!");
            throw new RuntimeException("FTP客户端出错！", e);
        } finally {
            IOUtils.closeQuietly(input);
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                logger.error("关闭FTP连接发生异常！");
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
            returnValue = true;
        }
        return returnValue;
    }

    /**
     * Ftp 下载文件
     * @param hostname  ip地址
     * @param port      端口号
     * @param username  登录名
     * @param password  登录密码
     * @param pathname  远程的文件路径
     * @param filename  远程的文件名字
     * @param output    本地文件输出流
     * @return
     */
    public static boolean DownLoad(String hostname, int port, String username, String password, String pathname, String filename, OutputStream output) {
        boolean returnValue = false;
        FTPClient ftpClient = loginFTP(hostname, port, username, password, pathname);
        try {
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                logger.error("FTP服务器拒绝连接");
                return false;
            }
            ftpClient.retrieveFile(filename, output);
            output.close();
        } catch (IOException e) {
            logger.error("FTP客户端出错!");
            throw new RuntimeException("FTP客户端出错！", e);
        } finally {
            IOUtils.closeQuietly(output);
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                logger.error("关闭FTP连接发生异常！");
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
            returnValue = true;
        }
        return returnValue;
    }

    /**
     * FTP 删除文件
     * @param hostname ip地址
     * @param port     端口
     * @param username 登录名
     * @param password 登录密码
     * @param pathname 远程文件路径
     * @param filename 远程文件名
     * @return
     */
    public static boolean Delete(String hostname, int port, String username, String password, String pathname, String filename) {
        boolean returnValue = false;
        FTPClient ftpClient = loginFTP(hostname, port, username, password, pathname);
        try {
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                logger.error("FTP服务器拒绝连接");
                return false;
            }
            ftpClient.deleteFile(filename);
        } catch (IOException e) {
            logger.error("FTP客户端出错!");
            throw new RuntimeException("FTP客户端出错！", e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                logger.error("关闭FTP连接发生异常！");
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
            returnValue = true;
        }
        return returnValue;
    }

    /**
     * 登陆FTP
     * @param hostname
     * @param port
     * @param username
     * @param password
     * @param pathname
     * @return
     */
    public static FTPClient loginFTP(String hostname, int port, String username, String password, String pathname) {
        FTPClient ftpClient = new FTPClient();
        try {
            //连接FTP服务器
            ftpClient.connect(hostname, port);
            //登录FTP服务器
            ftpClient.login(username, password);
            //验证FTP服务器是否登录成功
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                logger.error("FTP服务器拒绝连接");
            } else {
                logger.info("FTP服务器成功连接");
                ftpClient.changeWorkingDirectory(pathname);
                ftpClient.setBufferSize(1024);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ftpClient;
    }

}
