/**
 * 
 */
package org.codesearch.indexer.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;
import org.codesearch.indexer.shared.ManualIndexingData;

/**
 * @author Samuel Kogler
 *
 */
public interface ManualIndexingServiceAsync {

    void getData(AsyncCallback<ManualIndexingData> callback);

    void startManualIndexing(List<String> repositories, List<String> repoGroups, boolean clear, AsyncCallback<Void> callback);
}
