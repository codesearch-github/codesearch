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
/**
 *
 */
package org.codesearch.indexer.server.rpc;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.codesearch.indexer.client.rpc.LogService;
import org.codesearch.indexer.server.manager.IndexingManager;
import org.codesearch.indexer.shared.LogServiceException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Samuel Kogler
 *
 */
@Singleton
public class LogServiceImpl extends RemoteServiceServlet implements LogService {

    /** . */
    private static final long serialVersionUID = -3848061992960375933L;
    /** . */
    private IndexingManager indexingManager;

    @Inject
    public LogServiceImpl(IndexingManager indexingManager) {
        this.indexingManager = indexingManager;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getLog() throws LogServiceException {
        return indexingManager.getLog();
    }

}
