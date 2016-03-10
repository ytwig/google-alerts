/*     */ package org.nnh.service;
/*     */ 
/*     */ import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.nnh.bean.Alert;
import org.nnh.utils.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ 
/*     */ public class GAService
/*     */ {
/*  57 */   private String _email = "";
/*  58 */   private String _pwd = "";
/*  59 */   private String _alert_user_id = "";
/*  60 */   private Map<String, String> mapAlertId = new HashMap();
/*  61 */   private String cookies = "";
/*  62 */   private HttpClient client = new DefaultHttpClient();
/*     */   private static final String HEADER_USER_AGENT = "Mozilla/5.0";
/*     */   private static final String HEADER_ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
/*     */   private static final String HEADER_ACCEPT_LANGUAGE = "en-US,en;q=0.5";
/*     */   private static final String HEADER_CONTENT_TYPE = "application/x-www-form-urlencoded";
/*     */
/*     */   public GAService(String email, String pwd)
/*     */     throws Exception
/*     */   {
/*  70 */     if ((!StringUtils.isValidEmailAddress(email)) || (StringUtils.isEmpty(pwd))) {
/*  71 */       throw new Exception("Please use email and password correctly!");
/*     */     }
/*  73 */     this._email = email;
/*  74 */     this._pwd = pwd;
/*     */   }
/*     */
/*     */   public boolean doLogin()
/*     */   {
/*  82 */     boolean ret = false;
/*     */     try {
/*  84 */       String loginPage = getPageContent("https://accounts.google.com/ServiceLoginAuth");
/*  85 */       List postParams = getLoginFormParams(loginPage, this._email, this._pwd);
/*     */
/*  87 */       String html = postData("https://accounts.google.com/ServiceLoginAuth", postParams);
/*  88 */       Document doc = Jsoup.parse(html);
/*  89 */       Elements els = doc.select("span[id^=errormsg]");
/*  90 */       if ((els != null) && (els.size() > 0)) {
/*  91 */         return false;
/*     */       }
/*     */ 
/*  94 */       html = postData("https://www.google.com/alerts", null);
/*  95 */       doc = Jsoup.parse(html);
/*  96 */       Elements scripts = doc.select("script");
/*  97 */       for (Element script : scripts)
/*  98 */         if (script.html().startsWith("window.STATE")) {
/*  99 */           String data = script.html();
/* 100 */           data = data.substring(data.indexOf(" = ") + 3, data.length() - 1);
/* 101 */           JSONParser parser = new JSONParser();
/* 102 */           JSONArray json = (JSONArray)parser.parse(data);
/* 103 */           if (json.get(1) != null) {
/* 104 */             JSONArray array = (JSONArray)((JSONArray)json.get(1)).get(1);
/* 105 */             if ((array != null) && (array.size() > 0)) {
/* 106 */               for (Iterator localIterator2 = array.iterator(); localIterator2.hasNext(); ) { Object obj = localIterator2.next();
/* 107 */                 JSONArray anArray = (JSONArray)obj;
/* 108 */                 this.mapAlertId.put((String)anArray.get(1), (String)((JSONArray)((JSONArray)((JSONArray)anArray.get(2)).get(6)).get(0)).get(11));
/*     */               }
/*     */             }
/*     */           }
/*     */
/* 113 */           this._alert_user_id = ((String)json.get(3));
/* 114 */           ret = true;
/* 115 */           break;
/*     */         }
/*     */     } catch (Exception localException) {
/*     */     }
/* 119 */     return ret;
/*     */   }
/*     */
/*     */   public String createAlert(Alert alert) {
/* 123 */     String id = "";
/* 124 */     if (alert != null) {
/*     */       try {
/* 126 */         List paramList = new ArrayList();
/* 127 */         paramList.add(new BasicNameValuePair("params", buildParamValue(alert, false)));
/* 128 */         id = parseData(postData("https://www.google.com/alerts/create?", paramList));
/*     */       } catch (Exception ex) {
/* 130 */         id = ex.getMessage();
/*     */       }
/*     */     }
/* 133 */     return id;
/*     */   }
/*     */
/*     */   public String updateAlert(Alert alert) {
/* 137 */     String id = "";
/* 138 */     if ((alert != null) && (!StringUtils.isEmpty(alert.getId()))) {
/*     */       try {
/* 140 */         List paramList = new ArrayList();
/* 141 */         paramList.add(new BasicNameValuePair("params", buildParamValue(alert, true)));
/* 142 */         id = parseData(postData("https://www.google.com/alerts/modify?", paramList));
/*     */       } catch (Exception ex) {
/* 144 */         id = ex.getMessage();
/*     */       }
/*     */     }
/* 147 */     return id;
/*     */   }
/*     */
/*     */   public List<Alert> getAlerts() {
/* 151 */     return getAlerts("", "", "");
/*     */   }
/*     */
/*     */   public List<Alert> getAlerts(String searchQuery) {
/* 155 */     return getAlerts(searchQuery, "", "");
/*     */   }
/*     */
/*     */   public Alert getAlertById(String alertId) {
/* 159 */     List lst = getAlerts("", alertId, "");
/* 160 */     if ((lst != null) && (lst.size() > 0)) {
/* 161 */       return (Alert)lst.get(0);
/*     */     }
/* 163 */     return null;
/*     */   }
/*     */
/*     */   public List<Alert> getAlertByDelivery(String deliveryBy) {
/* 167 */     return getAlerts("", "", deliveryBy);
/*     */   }
/*     */
/*     */   private List<Alert> getAlerts(String searchQuery, String alertId, String deliveryBy) {
/* 171 */     List lst = new ArrayList();
/*     */     try {
/* 173 */       String html = postData("https://www.google.com/alerts", null);
/* 174 */       Document doc = Jsoup.parse(html);
/* 175 */       Elements scripts = doc.select("script");
/* 176 */       for (Element script : scripts)
/* 177 */         if (script.html().startsWith("window.STATE")) {
/* 178 */           String data = script.html();
/* 179 */           data = data.substring(data.indexOf(" = ") + 3, data.length() - 1);
/* 180 */           JSONParser parser = new JSONParser();
/* 181 */           JSONArray json = (JSONArray)parser.parse(data);
/* 182 */           if (json.get(1) == null) break;
/* 183 */           JSONArray array = (JSONArray)((JSONArray)json.get(1)).get(1);
/* 184 */           if ((array == null) || (array.size() <= 0)) break;
/* 185 */           for (Iterator localIterator2 = array.iterator(); localIterator2.hasNext(); ) { Object obj = localIterator2.next();
/* 186 */             Alert alert = new Alert();
/* 187 */             JSONArray anArray = (JSONArray)obj;
/* 188 */             if ((StringUtils.isEmpty(alertId)) || (alertId.equalsIgnoreCase((String)anArray.get(1))))
/*     */             {
/* 191 */               String feedUrl = "https://www.google.com/alerts/feeds/" + anArray.get(3);
/* 192 */               alert.setId((String)anArray.get(1));
/* 193 */               anArray = (JSONArray)anArray.get(2);
/* 194 */               alert.setHowMany(((Long)anArray.get(5)).toString());
/* 195 */               JSONArray part = (JSONArray)anArray.get(3);
/* 196 */               if ((StringUtils.isEmpty(searchQuery)) || (((String)part.get(1)).indexOf(searchQuery) >= 0))
/*     */               {
/* 199 */                 alert.setSearchQuery(StringEscapeUtils.unescapeHtml3((String)part.get(1)));
/* 200 */                 part = (JSONArray)part.get(3);
/* 201 */                 alert.setLanguage((String)part.get(1));
/* 202 */                 alert.setRegion((String)part.get(2));
/* 203 */                 String[] sources = { "" };
/* 204 */                 if (anArray.get(4) != null) {
/* 205 */                   JSONArray jsonArray = (JSONArray)anArray.get(4);
/* 206 */                   sources = new String[jsonArray.size()];
/* 207 */                   for (int i = 0; i < jsonArray.size(); i++) {
/* 208 */                     sources[i] = jsonArray.get(i).toString();
/*     */                   }
/*     */                 }
/* 211 */                 alert.setSources(sources);
/* 212 */                 part = (JSONArray)((JSONArray)anArray.get(6)).get(0);
/* 213 */                 String feedKey = ((Long)part.get(1)).toString();
/* 214 */                 feedUrl = feedUrl + "/" + part.get(11);
/* 215 */                 if ("1".equalsIgnoreCase(feedKey)) {
/* 216 */                   alert.setDeliveryTo((String)part.get(2));
/* 217 */                   feedKey = "EMAIL";
/*     */                 } else {
/* 219 */                   alert.setDeliveryTo(feedUrl);
/* 220 */                   feedKey = "feed";
/*     */                 }
/* 222 */                 if ((StringUtils.isEmpty(deliveryBy)) || (deliveryBy.equalsIgnoreCase(feedKey)))
/*     */                 {
/* 225 */                   alert.setHowOften(((Long)part.get(4)).toString());
/* 226 */                   lst.add(alert);
/*     */                 }
/*     */               }
/*     */             } }
/* 230 */           break;
/*     */         }
/*     */     } catch (Exception localException) {
/*     */     }
/* 234 */     return lst;
/*     */   }
/*     */
/*     */   public boolean deleteAlert(String alertId) {
/* 238 */     if (!StringUtils.isEmpty(alertId)) {
/*     */       try {
/* 240 */         List paramList = new ArrayList();
/* 241 */         paramList.add(new BasicNameValuePair("params", "[null,\"" + alertId + "\"]"));
/* 242 */         postData("https://www.google.com/alerts/delete?", paramList);
/* 243 */         return true;
/*     */       } catch (Exception e) {
/* 245 */         return false;
/*     */       }
/*     */     }
/* 248 */     return false;
/*     */   }
/*     */
/*     */   public boolean deleteAlert(List<String> lstAlertId) {
/* 252 */     if ((lstAlertId != null) && (!lstAlertId.isEmpty())) {
/* 253 */       for (String alertId : lstAlertId) {
/* 254 */         deleteAlert(alertId);
/*     */       }
/* 256 */       return true;
/*     */     }
/* 258 */     return false;
/*     */   }
/*     */
/*     */   private String buildParamValue(Alert alert, boolean isEdit) {
/* 262 */     String domainExt = "com";
/* 263 */     String feedKey = "1";
/* 264 */     String email = this._email;
/* 265 */     if ("feed".equalsIgnoreCase(alert.getDeliveryTo())) {
/* 266 */       feedKey = "2";
/* 267 */       alert.setHowOften("1");
/* 268 */       email = "";
/*     */     }
/* 270 */     if (!StringUtils.isEmpty(email)) {
/* 271 */       domainExt = email.substring(email.lastIndexOf(".") + 1);
/*     */     }
/* 273 */     String sources = Arrays.toString(alert.getsources());
/* 274 */     if ("[]".equalsIgnoreCase(sources)) sources = "null";
/*     */ 
/* 276 */     String howOften = "";
/* 277 */     if ("1".equalsIgnoreCase(alert.getHowOften()))
/* 278 */       howOften = "[]," + alert.getHowOften();
/* 279 */     else if ("2".equalsIgnoreCase(alert.getHowOften()))
/* 280 */       howOften = "[null,null,3]," + alert.getHowOften();
/* 281 */     else if ("3".equalsIgnoreCase(alert.getHowOften())) {
/* 282 */       howOften = "[null,null,3,3]," + alert.getHowOften();
/*     */     }
/*     */ 
/* 285 */     String update = "";
/* 286 */     if (isEdit) {
/* 287 */       update = "\"" + alert.getId() + "\",";
/*     */     }
/*     */ 
/* 290 */     String flagLanguage = "0";
/* 291 */     if (alert.getLanguage() == "") {
/* 292 */       alert.setLanguage("en");
/* 293 */       flagLanguage = "1";
/*     */     }
/* 295 */     String flagRegion = "1";
/* 296 */     if ("".equalsIgnoreCase(alert.getRegion())) {
/* 297 */       alert.setRegion("US");
/* 298 */       flagRegion = "0";
/*     */     }
/* 300 */     return "[null," + update + "[null,null,null,[null,\"" + alert.getSearchQuery() + "\",\"" + domainExt + "\",[null,\"" + alert.getLanguage() + "\",\"" + alert.getRegion() + "\"],null,null,null," + flagRegion + "," + flagLanguage + "]," + sources + "," + alert.getHowMany() + ",[[null," + feedKey + ",\"" + email + "\"," + howOften + ",\"en-US\"," + (isEdit ? "1" : "null") + ",null,null,null,null,\"" + (isEdit ? (String)this.mapAlertId.get(alert.getId()) : "0") + "\"]]]]";
/*     */   }
/*     */
/*     */   private String postData(String url, List<NameValuePair> postParams)
/*     */     throws ClientProtocolException, IOException
/*     */   {
/* 313 */     if ((postParams != null) && (postParams.size() == 1) && ("params".equalsIgnoreCase(((NameValuePair)postParams.get(0)).getName()))) {
/* 314 */       url = url + "x=" + this._alert_user_id;
/*     */     }
/*     */ 
/* 317 */     HttpPost post = new HttpPost(url);
/* 318 */     post.setHeader("Content-Type", "application/x-www-form-urlencoded");
/* 319 */     if ((postParams != null) && (postParams.size() > 0)) {
/* 320 */       post.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
/*     */     }
/* 322 */     HttpResponse response = this.client.execute(post);
/* 323 */     HttpEntity entity = response.getEntity();
/* 324 */     return EntityUtils.toString(entity, "UTF-8");
/*     */   }
/*     */
/*     */   private String getPageContent(String url) throws Exception {
/* 328 */     HttpGet request = new HttpGet(url);
/* 329 */     request.setHeader("User-Agent", "Mozilla/5.0");
/* 330 */     request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
/* 331 */     request.setHeader("Accept-Language", "en-US,en;q=0.5");
/*     */ 
/* 333 */     HttpResponse response = this.client.execute(request);
/* 334 */     HttpEntity entity = response.getEntity();
/*     */ 
/* 337 */     setCookies(response.getFirstHeader("Set-Cookie") == null ? "" : response.getFirstHeader("Set-Cookie").toString());
/*     */ 
/* 339 */     return EntityUtils.toString(entity, "UTF-8");
/*     */   }
/*     */
/*     */   private List<NameValuePair> getLoginFormParams(String html, String username, String password) throws UnsupportedEncodingException {
/* 343 */     Document doc = Jsoup.parse(html);
/*     */ 
/* 345 */     Element loginform = doc.getElementById("gaia_loginform");
/* 346 */     Elements inputElements = loginform.getElementsByTag("input");
/*     */ 
/* 348 */     List paramList = new ArrayList();
/*     */ 
/* 350 */     for (Element inputElement : inputElements) {
/* 351 */       String key = inputElement.attr("name");
/* 352 */       String value = inputElement.attr("value");
/*     */
                if (StringUtils.isEmpty(key))
                    continue;

/* 354 */       if (key.equals("Email"))
/* 355 */         value = username;
/* 356 */
/* 359 */       paramList.add(new BasicNameValuePair(key, value));
/*     */     }
              paramList.add(new BasicNameValuePair("Passwd", password));

/* 361 */     return paramList;
/*     */   }
/*     */
/*     */   private String parseData(String data) {
/* 365 */     String id = "";
/*     */     try {
/* 367 */       JSONParser parser = new JSONParser();
/* 368 */       JSONArray json = (JSONArray)parser.parse(data);
/* 369 */       json = (JSONArray)((JSONArray)json.get(4)).get(0);
/* 370 */       id = (String)json.get(1);
/* 371 */       this.mapAlertId.put(id, (String)((JSONArray)((JSONArray)((JSONArray)json.get(3)).get(6)).get(0)).get(11)); } catch (Exception localException) {
/*     */     }
/* 373 */     return id;
/*     */   }
/*     */
/*     */   private String getCookies() {
/* 377 */     return this.cookies;
/*     */   }
/*     */
/*     */   private void setCookies(String cookies) {
/* 381 */     this.cookies = cookies;
/*     */   }
/*     */
/*     */   private static class GOOGLE
/*     */   {
/*     */     private static final String GOOGLE_URL = "https://www.google.com";
/*     */     private static final String LOGIN_URL = "https://accounts.google.com/ServiceLoginAuth";
/*     */     private static final String ALERT_URL = "https://www.google.com/alerts";
/*     */     private static final String CREATE_ALERT_URL = "https://www.google.com/alerts/create?";
/*     */     private static final String EDIT_ALERT_URL = "https://www.google.com/alerts/modify?";
/*     */     private static final String DELETE_ALERT_URL = "https://www.google.com/alerts/delete?";
/*     */
/*     */     private static class LOGIN_FORM
/*     */     {
/*     */       private static final String FORM_ID = "gaia_loginform";
/*     */       private static final String EMAIL = "Email";
/*     */       private static final String PASSWORD = "Passwd";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/yuval.twig/Desktop/google-alert-api.jar
 * Qualified Name:     org.nnh.service.GAService
 * JD-Core Version:    0.6.2
 */