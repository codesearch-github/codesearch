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
