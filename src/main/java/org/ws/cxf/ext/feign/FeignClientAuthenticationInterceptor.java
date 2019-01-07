package org.ws.cxf.ext.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ws.cxf.ext.utils.Utils.generateSignature;

public class FeignClientAuthenticationInterceptor implements RequestInterceptor {
  private static final Logger LOGGER = LoggerFactory.getLogger(FeignClientAuthenticationInterceptor.class);

  private String appid;

  private String env;

  private String url;

  public FeignClientAuthenticationInterceptor(String url, String appid, String env) {
    this.url = url;
    this.appid = appid;
    this.env = env;

    LOGGER.info("[FeignClientAuthenticationInterceptor] Loading FeignClientAuthenticationInterceptor with env = {}, appid = {}, url = {}",
        this.env,
        this.appid,
        this.url);
  }

  @Override
  public void apply(RequestTemplate requestTemplate) {
    String uri = url + requestTemplate.url();
    String signature = generateSignature(appid, env, uri);

    LOGGER.info("[FeignClientAuthenticationInterceptor][apply] uri = {}, signature = {}", uri, signature);
    requestTemplate.header("Authorization", signature);
  }
}
