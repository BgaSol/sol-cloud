package com.bgasol.common.util.ssh;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

public class JSchUtil {

    private Session session;

    // 连接到远程服务器
    public void connect(String host, int port, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(username, host, port);
        session.setPassword(password);

        // 忽略主机密钥检查
        Properties properties = new Properties();
        properties.put("StrictHostKeyChecking", "no");
        session.setConfig(properties);

        session.connect();
        System.out.println("Connected to " + host);
    }

    // 执行远程命令
    public String executeCommand(String command) throws JSchException, IOException {
        if (session == null || !session.isConnected()) {
            throw new IllegalStateException("Session is not connected.");
        }

        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand(command);
        channelExec.setErrStream(System.err);

        InputStream inputStream = channelExec.getInputStream();
        channelExec.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }

        channelExec.disconnect();
        return result.toString();
    }

    // 将本地文件复制到远程服务器
    public void copyFileToRemote(String localFilePath, String remoteFilePath) throws JSchException, IOException, SftpException {
        if (session == null || !session.isConnected()) {
            throw new IllegalStateException("Session is not connected.");
        }

        // 删除远程文件（如果存在）
        executeCommand("rm -f " + remoteFilePath);

        Channel channel = session.openChannel("sftp");
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.connect();

        sftpChannel.put(localFilePath, remoteFilePath, ChannelSftp.OVERWRITE);
        sftpChannel.disconnect();
        System.out.println("File copied to " + remoteFilePath);
    }

    // 将本地目录复制到远程服务器
    public void copyDirectoryToRemote(String localDirPath, String remoteDirPath) throws JSchException, IOException, SftpException {
        if (session == null || !session.isConnected()) {
            throw new IllegalStateException("Session is not connected.");
        }

        // 删除远程目录内容（如果存在）
        executeCommand("rm -rf " + remoteDirPath);

        Channel channel = session.openChannel("sftp");
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.connect();

        File localDir = new File(localDirPath);
        if (localDir.isDirectory()) {
            sftpChannel.mkdir(remoteDirPath);
            copyDirectory(localDir, remoteDirPath, sftpChannel);
        } else {
            throw new IllegalArgumentException(localDirPath + " is not a directory.");
        }

        sftpChannel.disconnect();
        System.out.println("Directory copied to " + remoteDirPath);
    }

    // 递归地将目录内容复制到远程服务器
    private void copyDirectory(File localDir, String remoteDirPath, ChannelSftp sftpChannel) throws SftpException, FileNotFoundException {
        for (File file : Objects.requireNonNull(localDir.listFiles())) {
            if (file.isDirectory()) {
                String subDirPath = remoteDirPath + "/" + file.getName();
                sftpChannel.mkdir(subDirPath);
                copyDirectory(file, subDirPath, sftpChannel);
            } else {
                FileInputStream fileInputStream = new FileInputStream(file);
                sftpChannel.put(fileInputStream, remoteDirPath + "/" + file.getName(), ChannelSftp.OVERWRITE);
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // 断开连接
    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            System.out.println("Disconnected from server.");
        }
    }
}