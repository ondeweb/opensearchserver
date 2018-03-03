/*
 * Copyright 2017-2018 Emmanuel Keller / Jaeksoft
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.jaeksoft.opensearchserver.services;

import com.jaeksoft.opensearchserver.model.CrawlTaskRecord;
import com.jaeksoft.opensearchserver.model.TaskRecord;
import com.qwazr.crawler.common.CrawlDefinition;
import com.qwazr.crawler.common.CrawlStatus;
import com.qwazr.crawler.common.CrawlerServiceInterface;
import com.qwazr.server.ServerException;
import com.qwazr.server.client.ErrorWrapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;

public abstract class CrawlProcessingService<T extends CrawlTaskRecord, D extends CrawlDefinition, S extends CrawlStatus<D>>
		implements ProcessingService<T, S> {

	private final CrawlerServiceInterface<D, S> crawlerService;
	protected final ConfigService configService;
	protected final IndexesService indexesService;

	protected CrawlProcessingService(final ConfigService configService,
			final CrawlerServiceInterface<D, S> crawlerService, final IndexesService indexesService) {
		this.configService = configService;
		this.crawlerService = crawlerService;
		this.indexesService = indexesService;
	}

	@Override
	final public S getStatus(final String taskId) {
		return ErrorWrapper.bypass(() -> crawlerService.getSession(taskId), 404);
	}

	@Override
	final public boolean isRunning(final String taskId) {
		final S crawlStatus = getStatus(taskId);
		return crawlStatus != null && crawlStatus.endTime == null;
	}

	@Override
	public void abort(final String taskId) {
		try {
			crawlerService.abortSession(taskId, "User request");
		} catch (WebApplicationException e) {
			if (e.getResponse().getStatus() == 404)
				return;
			throw e;
		} catch (ServerException e) {
			if (e.getStatusCode() == 404)
				return;
			throw e;
		}
	}

	protected abstract D getNewCrawlDefinition(final String schema, final T taskRecord) throws MalformedURLException;

	@Override
	final public void checkIsRunning(final String schema, final TaskRecord taskRecord) throws MalformedURLException {
		if (isRunning(taskRecord.getTaskId()))
			return;
		final D crawlDefinition = getNewCrawlDefinition(schema, getTaskRecordClass().cast(taskRecord));
		if (crawlDefinition == null) // Nothing to do
			return;
		try {
			crawlerService.runSession(taskRecord.getTaskId(), crawlDefinition);
		} catch (WebApplicationException e) {
			if (e.getResponse().getStatus() != Response.Status.CONFLICT.getStatusCode())
				throw e;
		}
	}
}
