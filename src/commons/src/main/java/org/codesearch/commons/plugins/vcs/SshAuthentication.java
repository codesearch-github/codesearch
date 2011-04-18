/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.commons.plugins.vcs;

/**
 * Authentication type used by VersionControlSystems if the repository requires SSH-authentication
 * @author David Froehlich
 */
public class SshAuthentication implements AuthenticationType {
    public String sshFilePath;
    
    public SshAuthentication(String sshFilePath) {
        this.sshFilePath = sshFilePath;
    }
}
