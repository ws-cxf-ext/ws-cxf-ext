package org.ws.cxf.ext.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ws.cxf.ext.utils.Utils.generateSignature;

public class FeignClientAuthenticationInterceptor implements RequestInterceptor {
  private static final Logger LOGGER = LoggerFactory.getLogger(FeignClientAuthenticationInterceptor.class);

  private String appid;

  private String env;

  private String url;

  private String subpathToSubstract;

  public FeignClientAuthenticationInterceptor(String url, String appid, String env, String subpathToSubstract) {
    this.url = url;
    this.appid = appid;
    this.env = env;
    this.subpathToSubstract = subpathToSubstract;

    if (isBlank(subpathToSubstract)) {
      this.subpathToSubstract = EMPTY;
    }

    LOGGER.info("[FeignClientAuthenticationInterceptor] Loading FeignClientAuthenticationInterceptor with env = {}, appid = {}, url = {}, subpathToSubstract = {}",
        this.env,
        this.appid,
        this.url,
        this.subpathToSubstract);
  }

  @Override
  public void apply(RequestTemplate requestTemplate) {
    String uri = url + requestTemplate.url();
    String signature = generateSignature(appid, env, uri, subpathToSubstract);

    LOGGER.info("[FeignClientAuthenticationInterceptor][apply] uri = {}, signature = {}", uri, signature);
    requestTemplate.header("Authorization", signature);
  }
}
