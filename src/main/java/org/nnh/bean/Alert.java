/*     */ package org.nnh.bean;
/*     */ 
/*     */ public class Alert
/*     */ {
/*     */   private String id;
/*  25 */   private String searchQuery = "";
/*     */ 
/*  27 */   private String howOften = "2";
/*     */ 
/*  29 */   private String[] sources = { "" };
/*     */ 
/*  31 */   private String language = "en";
/*     */ 
/*  33 */   private String region = "US";
/*     */ 
/*  35 */   private String howMany = "3";
/*     */ 
/*  37 */   private String deliveryTo = "EMAIL";
/*     */ 
/*     */   public String getId() {
/*  40 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(String id) {
/*  44 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public String getSearchQuery() {
/*  48 */     return this.searchQuery;
/*     */   }
/*     */ 
/*     */   public void setSearchQuery(String searchQuery) {
/*  52 */     this.searchQuery = searchQuery;
/*     */   }
/*     */ 
/*     */   public String[] getsources() {
/*  56 */     return this.sources;
/*     */   }
/*     */ 
/*     */   public void setSources(String[] sources) {
/*  60 */     this.sources = sources;
/*     */   }
/*     */ 
/*     */   public String getHowMany() {
/*  64 */     return this.howMany;
/*     */   }
/*     */ 
/*     */   public void setHowMany(String howMany) {
/*  68 */     this.howMany = howMany;
/*     */   }
/*     */ 
/*     */   public String getHowOften() {
/*  72 */     return this.howOften;
/*     */   }
/*     */ 
/*     */   public void setHowOften(String howOften) {
/*  76 */     this.howOften = howOften;
/*     */   }
/*     */ 
/*     */   public String getDeliveryTo() {
/*  80 */     return this.deliveryTo;
/*     */   }
/*     */ 
/*     */   public void setDeliveryTo(String deliveryTo) {
/*  84 */     this.deliveryTo = deliveryTo;
/*     */   }
/*     */ 
/*     */   public String getLanguage() {
/*  88 */     return this.language;
/*     */   }
/*     */ 
/*     */   public void setLanguage(String language) {
/*  92 */     this.language = language;
/*     */   }
/*     */ 
/*     */   public String getRegion() {
/*  96 */     return this.region;
/*     */   }
/*     */ 
/*     */   public void setRegion(String region) {
/* 100 */     this.region = region;
/*     */   }
/*     */ }

/* Location:           /Users/yuval.twig/Desktop/google-alert-api.jar
 * Qualified Name:     org.nnh.bean.Alert
 * JD-Core Version:    0.6.2
 */