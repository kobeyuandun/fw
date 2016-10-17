package com.hongyu.reward.request;

import com.hongyu.reward.config.Constants;
import com.hongyu.reward.http.BaseHttpRequestBuilder;
import com.hongyu.reward.model.ShopListMode;

/**
 * 获取可发布商家列表, 首页发布页面数据
 * Created by zhangyang131 on 16/9/12.
 */
public class GetShopListRequestBuilder extends BaseHttpRequestBuilder<ShopListMode> {
  private static final String PAGE = "page";
  private static final String LOCATION = "location";
  private static final String KEYWORD = "keyword";
  private static final String CITY = "city";
  private String page;
  private String location;
  private String keyword;
  private String city;

  public GetShopListRequestBuilder(String page, String location, String city, String keyword) {
    this.page = page;
    this.location = location;
    this.keyword = keyword;
    this.city = city;
  }

  @Override
  protected String getApiUrl() {
    return Constants.Server.API_STORE_LIST;
  }

  @Override
  protected Class<ShopListMode> getResponseClass() {
    return ShopListMode.class;
  }

  @Override
  protected void setParams(Params params) {
    super.setParams(params);
    checkNullAndSet(params, PAGE, page);
    params.put(LOCATION, location == null ? "" : location);
    checkNullAndSet(params, KEYWORD, keyword);
    checkNullAndSet(params, CITY, city);
  }
}
