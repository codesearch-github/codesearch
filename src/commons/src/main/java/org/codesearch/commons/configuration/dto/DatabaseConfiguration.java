package org.codesearch.commons.configuration.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Stores configuration values for the database connection.
 * @author Samuel Kogler
 */
@XStreamAlias("database")
public class DatabaseConfiguration {
    @XStreamAlias("host-name")
    private String hostName;
    private int port;
    @XStreamAlias("user-name")
    private String username;
    private String password;
    private String driver;
    private String protocol;
    private String database;
    @XStreamAlias("max-connections")
    private int maxConnections;
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getDriver() {
        return driver;
    }
    public void setDriver(String driver) {
        this.driver = driver;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public int getMaxConnections() {
        return maxConnections;
    }
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }
    public String getProtocol() {
        return protocol;
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public String getHostName() {
        return hostName;
    }
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    public String getDatabase() {
        return database;
    }
    public void setDatabase(String database) {
        this.database = database;
    }
}
