/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.security.oauth2.client;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.security.oauth2.resource.ResourceServerTokenServicesConfiguration;
import org.springframework.cloud.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.cloud.security.oauth2.sso.OAuth2SsoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;

@Configuration
@ConditionalOnClass(OAuth2ClientContext.class)
@ConditionalOnBean(ResourceServerTokenServicesConfiguration.class)
@ConditionalOnWebApplication
@EnableConfigurationProperties
public class OAuth2ClientAutoConfiguration {

	@Configuration
	protected abstract static class BaseConfiguration {

		@Resource
		@Qualifier("accessTokenRequest")
		protected AccessTokenRequest accessTokenRequest;

	}

	@Configuration
	@ConditionalOnBean(OAuth2SsoConfiguration.class)
	protected abstract static class SessionScopedConfiguration extends BaseConfiguration {

		@Bean
		@Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
		public OAuth2ClientContext oauth2ClientContext() {
			return new DefaultOAuth2ClientContext(accessTokenRequest);
		}

	}

	@Configuration
	@ConditionalOnMissingBean(OAuth2SsoConfiguration.class)
	@ConditionalOnBean(UserInfoTokenServices.class)
	protected abstract static class RequestScopedConfiguration extends BaseConfiguration {

		@Bean
		@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
		public OAuth2ClientContext oauth2ClientContext() {
			return new DefaultOAuth2ClientContext(accessTokenRequest);
		}

	}

}
