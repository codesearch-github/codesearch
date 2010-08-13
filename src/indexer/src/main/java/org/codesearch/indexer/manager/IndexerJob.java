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

package org.codesearch.indexer.manager;

/**
 *
 * @author Stiboller Stephan
 */
public class IndexerJob extends Thread
{
    /** Indicates if the thread is suspende or not. */
    private boolean threadIsSuspended = false;
    private boolean threadIsTerminated = false;

    /**
     * Suspends the thread in a save way.
     */
    public void suspendSavely()
    {
        threadIsSuspended = true;
    }

   /**
     * Resumes the thread.
     * The thread is maybe not instantly killed because the changes of
     * the IndexerJob will be reverted.
     */
    public void resumeSavely()
    {
        threadIsSuspended = false;
    }
    
    /**
     * First suspends and then terminates the thread.
     * The thread is maybe not instantly killed because the changes of
     * the IndexerJob will be reverted and cleaned.
     */
    public void terminateSavely()
    {
        threadIsSuspended = true;
        threadIsTerminated = true;
    }

    /**
     * //TODO: impl
     */
    public void run() {
        while (!threadIsSuspended)
        {
            //TODO: impl
        }
        if(threadIsTerminated)
        {
             // TODO: Clean up all changes made by the thread
        }
       
    }

}
