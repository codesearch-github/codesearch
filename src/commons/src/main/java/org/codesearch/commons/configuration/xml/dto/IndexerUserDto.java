/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.configuration.xml.dto;

/**
 * DTO class that stores information about a user that is authorized to use the indexer via the web interface
 * @author David Froehlich
 */
public class IndexerUserDto {
    private String userName;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
