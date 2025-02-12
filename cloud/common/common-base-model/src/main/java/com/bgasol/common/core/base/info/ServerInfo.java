package com.bgasol.common.core.base.info;

/**
 * 服务器信息
 */
public interface ServerInfo {
    /**
     * @return 服务器IP
     */
    String getServerIp();

    /**
     * @param ip 服务器IP
     */
    void setServerIp(String ip);

    /**
     * @return 服务器名称
     */
    String getServerName();

    /**
     * @param name 服务器名称
     */
    void setServerName(String name);

    /**
     * @return 服务器端口
     */
    Integer getServerPort();

    /**
     * @param port 服务器端口
     */
    void setServerPort(Integer port);

    /**
     * @return 服务器登录账号
     */
    String getServerUserName();

    /**
     * @param userName 服务器登录账号
     */
    void setServerUserName(String userName);

    /**
     * @return 服务器登录密码
     */
    String getServerPassword();

    /**
     * @param password 服务器登录密码
     */
    void setServerPassword(String password);

    /**
     * @return 服务版本
     */
    String getServerVersion();

    void setServerVersion(String version);
}
