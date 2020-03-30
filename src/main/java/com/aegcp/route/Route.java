/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aegcp.route;

import com.google.api.server.spi.auth.EspAuthenticator;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiIssuer;
import com.google.api.server.spi.config.ApiIssuerAudience;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.UnauthorizedException;

/**
 * The Route API which Endpoints will be exposing.
 */
// [START route_api_annotation]
@Api(
    name = "route",
    version = "v1",
    namespace =
    @ApiNamespace(
        ownerDomain = "route.aegcp.com",
        ownerName = "route.aegcp.com",
        packagePath = ""
    ),
    // [START_EXCLUDE]
    issuers = {
        @ApiIssuer(
            name = "firebase",
            issuer = "https://securetoken.google.com/apolo-272515",
            jwksUri =
                "https://www.googleapis.com/service_accounts/v1/metadata/x509/securetoken@system"
                    + ".gserviceaccount.com"
        )
    }
// [END_EXCLUDE]
)
// [END route_api_annotation]

public class Route {

  /**
   * Routes the received message back. If n is a non-negative integer, the message is copied that
   * many times in the returned message.
   *
   * <p>Note that name is specified and will override the default name of "{class name}.{method
   * name}". For example, the default is "route.route".
   *
   * <p>Note that httpMethod is not specified. This will default to a reasonable HTTP method
   * depending on the API method name. In this case, the HTTP method will default to POST.
   */
  // [START route_method]
  @ApiMethod(name = "route")
  public Message route(Message message, @Named("n") @Nullable Integer n) {
    return doRoute(message, n);
  }
  // [END route_method]

  /**
   * Routes the received message back. If n is a non-negative integer, the message is copied that
   * many times in the returned message.
   *
   * <p>Note that name is specified and will override the default name of "{class name}.{method
   * name}". For example, the default is "route.route".
   *
   * <p>Note that httpMethod is not specified. This will default to a reasonable HTTP method
   * depending on the API method name. In this case, the HTTP method will default to POST.
   */
  // [START route_path]
  @ApiMethod(name = "route_path_parameter", path = "route/{n}")
  public Message routePathParameter(Message message, @Named("n") int n) {
    return doRoute(message, n);
  }
  // [END route_path]

  /**
   * Routes the received message back. If n is a non-negative integer, the message is copied that
   * many times in the returned message.
   *
   * <p>Note that name is specified and will override the default name of "{class name}.{method
   * name}". For example, the default is "route.route".
   *
   * <p>Note that httpMethod is not specified. This will default to a reasonable HTTP method
   * depending on the API method name. In this case, the HTTP method will default to POST.
   */
  // [START route_api_key]
  @ApiMethod(name = "route_key", path = "route_key", apiKeyRequired = AnnotationBoolean.TRUE)
  public Message routeKey(Message message, @Named("n") @Nullable Integer n) {
    return doRoute(message, n);
  }
  // [END route_api_key]

  private Message doRoute(Message message, Integer n) {
    if (n != null && n >= 0) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < n; i++) {
        if (i > 0) {
          sb.append(" ");
        }
        sb.append(message.getMessage());
      }
      message.setMessage(sb.toString());
    }
    return message;
  }

  /**
   * Gets the authenticated user's email. If the user is not authenticated, this will return an HTTP
   * 401.
   *
   * <p>Note that name is not specified. This will default to "{class name}.{method name}". For
   * example, the default is "route.getUserEmail".
   *
   * <p>Note that httpMethod is not required here. Without httpMethod, this will default to GET due
   * to the API method name. httpMethod is added here for example purposes.
   */
  // [START google_id_token_auth]
  @ApiMethod(
      httpMethod = ApiMethod.HttpMethod.GET,
      authenticators = {EspAuthenticator.class},
      audiences = {"519803264524-0657fq2uplesee2aug728r2o08pdu7sp.apps.googleusercontent.com"},
      clientIds = {"519803264524-0657fq2uplesee2aug728r2o08pdu7sp.apps.googleusercontent.com"}
  )
  public Email getUserEmail(User user) throws UnauthorizedException {
    if (user == null) {
      throw new UnauthorizedException("Invalid credentials");
    }

    Email response = new Email();
    response.setEmail(user.getEmail());
    return response;
  }
  // [END google_id_token_auth]

  /**
   * Gets the authenticated user's email. If the user is not authenticated, this will return an HTTP
   * 401.
   *
   * <p>Note that name is not specified. This will default to "{class name}.{method name}". For
   * example, the default is "route.getUserEmail".
   *
   * <p>Note that httpMethod is not required here. Without httpMethod, this will default to GET due
   * to the API method name. httpMethod is added here for example purposes.
   */
  // [START firebase_auth]
  @ApiMethod(
      path = "firebase_user",
      httpMethod = ApiMethod.HttpMethod.GET,
      authenticators = {EspAuthenticator.class},
      issuerAudiences = {
          @ApiIssuerAudience(
              name = "firebase",
              audiences = {"apolo-272515"}
          )
      }
  )
  public Email getUserEmailFirebase(User user) throws UnauthorizedException {
    if (user == null) {
      throw new UnauthorizedException("Invalid credentials");
    }

    Email response = new Email();
    response.setEmail(user.getEmail());
    return response;
  }
  // [END firebase_auth]
}
