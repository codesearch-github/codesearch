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
package org.commons.codesearch.utils.bazaar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.vcs.bazaar.client.BazaarRevision;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.vcs.bazaar.client.BazaarClientFactory;
import org.vcs.bazaar.client.BazaarClientPreferences;
import org.vcs.bazaar.client.BazaarPreference;
import org.vcs.bazaar.client.IBazaarClient;
import org.vcs.bazaar.client.IBazaarLogMessage;
import org.vcs.bazaar.client.commandline.CommandLineClientFactory;
import org.vcs.bazaar.client.commandline.commands.options.Option;
import org.vcs.bazaar.client.core.BazaarClientException;
import org.vcs.bazaar.client.core.BranchLocation;

/**
 * This class hold serveral methods that can be used to access
 * the bazaar console client.
 * @author zeheron
 */
public class BazaarUtils {
    //TODO: add ssh auth
    //TODO: add logging

    private static BazaarUtils instance;
    private IBazaarClient bazaarClient;
    private String executablePath = "/usr/bin/bzr"; //TODO read path form config
    private static final Logger LOG = Logger.getLogger(BazaarUtils.class);

    /**
     * Contstructor
     */
    private BazaarUtils() {
        try {
            BazaarClientPreferences.getInstance().set(BazaarPreference.EXECUTABLE, executablePath);
            BazaarClientFactory.setupBestAvailableBackend();
        } catch (BazaarClientException ex) {
            LOG.error(ex);
        }
        this.bazaarClient = BazaarClientFactory.createClient(CommandLineClientFactory.CLIENT_TYPE);
    }

    /**
     * Retrieves the singleton instance to use.
     */
    public static BazaarUtils getInstance() {
        if (instance == null) {
            instance = new BazaarUtils();
        }
        return instance;
    }

    /**
     * Executes a 'bzr cat <url> <revno>' command and returns
     * the content,
     * @param url
     * @param revision
     * @return the content as byte[]
     * @throws BazaarClientException
     * @throws IOException
     */
    public byte[] retrieveFileContent(URI url, String revision) throws BazaarClientException, IOException {
        int revisionNumber = Integer.parseInt(revision);
        BazaarRevision bRevision = BazaarRevision.getRevision(revisionNumber);
        InputStream in = bazaarClient.cat(new File(url), bRevision);
        return IOUtils.toByteArray(in);
    }

    /**
     * Creates a new BranchLocation for the given repositoryUrl and
     * if the username and password are not null it will use them to
     * authenticate.
     * 
     * @param repositoryUrl
     * @param userName 
     * @param password
     * @return BranchLocation
     * @throws URISyntaxException
     */
    public BranchLocation createBranchLocation(String repositoryUrl, String userName, String password) throws URISyntaxException {
        BranchLocation branchLocation = new BranchLocation(repositoryUrl);
        URI uri = branchLocation.getURI();
        if (!userName.isEmpty() && !password.isEmpty()) {
            URI newURI = new URI(uri.getScheme(), userName + ":" + password, uri.getPath(), uri.getQuery(), uri.getFragment());
            branchLocation = new BranchLocation(newURI);
            return branchLocation;
        } else {
            return branchLocation;
        }
    }

    /**
     * Creates a new BranchLocation instance for the given repositoryUrl.
     * @param repositoryUrl
     * @return BranchLocation
     * @throws URISyntaxException
     */
    public BranchLocation createBranchLocation(String repositoryUrl) throws URISyntaxException {
        return createBranchLocation(repositoryUrl, null, null);
    }

    /**
     * Retrieves the information about the change details since the specified
     * revision
     * @param uri
     * @param revision
     * @return List of IBazaarLogMessages
     * @throws BazaarClientException
     * @throws URISyntaxException
     */
    public List<IBazaarLogMessage> getChangesSinceRevison(String repositoryUrl, String revision) throws BazaarClientException, URISyntaxException {
        List<Option> options = new ArrayList<Option>();
        String revisionRange = revision + "..";
        if (revision != null) {
            options.add(new Option("-r"));
            options.add(new Option(revisionRange));
        }
        options.add(new Option("-v"));
        Option[] optionArray = options.toArray(new Option[options.size()]);
        return bazaarClient.log(createBranchLocation(repositoryUrl), optionArray);
    }

    /**
     * Returns the lates revisionid
     * @param bl
     * @return
     * @throws BazaarClientException
     */
    public String getLatestRevisionNumber(BranchLocation bl) throws BazaarClientException {
        IBazaarLogMessage logMessage = bazaarClient.log(bl, new Option("-r-1")).get(0);
        return logMessage.getRevision().getValue();
    }
}
