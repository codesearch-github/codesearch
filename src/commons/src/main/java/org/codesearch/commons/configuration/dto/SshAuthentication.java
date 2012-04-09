/**
 * Copyright 2010 David Froehlich   <david.froehlich@businesssoftware.at>,
 *                Samuel Kogler     <samuel.kogler@gmail.com>,
 *                Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Codesearch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Codesearch.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.configuration.dto;

/**
 * Authentication type used by VersionControlSystems if the repository requires SSH-authentication
 * @author David Froehlich
 */
public class SshAuthentication implements AuthenticationType {
    private String sshFilePath;
    private String username;
    private String password;
    private String port;
    
    public SshAuthentication(String username, String password, String port, String sshFilePath) {
        this.sshFilePath = sshFilePath;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSshFilePath() {
        return sshFilePath;
    }

    public void setSshFilePath(String sshFilePath) {
        this.sshFilePath = sshFilePath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    @Override
    public String toString() {
        return "SshAuthentication{" + "sshFilePath=" + sshFilePath + '}';
    }
}
