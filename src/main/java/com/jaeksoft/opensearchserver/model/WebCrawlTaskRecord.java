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

package com.jaeksoft.opensearchserver.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.qwazr.crawler.web.WebCrawlDefinition;

import java.util.UUID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
		getterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE,
		creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName(WebCrawlTaskRecord.TYPE)
public class WebCrawlTaskRecord extends CrawlTaskRecord<WebCrawlDefinition> {

	final static String TYPE = "web";

	@JsonCreator
	WebCrawlTaskRecord(@JsonProperty("crawlUuid") UUID crawlUuid, @JsonProperty("indexUuid") UUID indexUuid,
			@JsonProperty("crawlDefinition") WebCrawlDefinition crawlDefinition,
			@JsonProperty("creationTime") Long creationTime, @JsonProperty("status") Status status,
			@JsonProperty("statusTime") Long statusTime) {
		super(crawlUuid, indexUuid, crawlDefinition, creationTime, status, statusTime);
	}

	public String getType() {
		return TYPE;
	}

	WebCrawlTaskRecord(final Builder builder) {
		super(builder);
	}

	public Builder from() {
		return new Builder(this);
	}

	public static Builder of(final WebCrawlRecord record, final UUID indexUuid) {
		return new Builder(record.getUuid(), indexUuid, record.crawlDefinition);
	}

	public static class Builder extends BaseBuilder<WebCrawlDefinition, WebCrawlTaskRecord, Builder> {

		/**
		 * Constructor to use when updating a record
		 *
		 * @param record
		 */
		private Builder(final WebCrawlTaskRecord record) {
			super(Builder.class, record);
		}

		private Builder(UUID crawlUuid, UUID indexUuid, WebCrawlDefinition crawlDefinition) {
			super(Builder.class, crawlUuid, indexUuid, crawlDefinition);
		}

		public WebCrawlTaskRecord build() {
			return new WebCrawlTaskRecord(this);
		}
	}
}
