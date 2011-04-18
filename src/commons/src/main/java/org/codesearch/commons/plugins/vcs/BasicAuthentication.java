/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.vcs;

/**
 * Authentication used by the VersionControlSystem if the repository allows authentication via a username and a password.
 * @author David Froehlich
 */
public class BasicAuthentication implements AuthenticationType {
    private String username;
    private String password;

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public BasicAuthentication(String username, String password){
        this.username = username;
        this.password = password;
    }
}
