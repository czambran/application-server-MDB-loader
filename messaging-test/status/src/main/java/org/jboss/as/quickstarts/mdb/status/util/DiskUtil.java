/**
 * 
 */
package org.jboss.as.quickstarts.mdb.status.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author bmaxwell
 *
 */
public class DiskUtil {
	private ExecutorService executor = null;
	/**
	 * 
	 */
	public DiskUtil(ExecutorService executor) {
		this.executor = executor;
	}

	private ExecutorService getExecutor() {
		return this.executor;
	}
	
	public Future<String> diskUsageFuture(final String dir) {
		return getExecutor().submit(new Callable<String>() {
			@Override
			public String call() {
				BufferedReader reader = null;
				StringBuilder sb = new StringBuilder();
				try {
					ProcessBuilder pb = new ProcessBuilder("du", "-h", "-d", "0", dir);
					Process p = pb.start();
					reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

					String line = null;
					while ((line = reader.readLine()) != null)
						sb.append(line);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (reader != null)
							reader.close();
					} catch (Exception e) {
					}
				}
				return sb.toString();
			}
		});
	}

	public Future<List<String>> diskUsage(final List<String> dirs) {		
		return getExecutor().submit(new Callable<List<String>>() {
			@Override
			public List<String> call() {
				List<String> response = new ArrayList<String>(dirs.size());
				Future<String>[] futures = new Future[dirs.size()];

				for (int i = 0; i < dirs.size(); i++)
					futures[i] = diskUsageFuture(dirs.get(i));

				for (int i = 0; i < dirs.size(); i++) {
					try {
						response.add(futures[i].get(5L, TimeUnit.SECONDS));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return response;
			}
		});				
	}
}
