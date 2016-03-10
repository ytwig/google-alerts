/*    */ package org.nnh.utils;
/*    */ 
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ public class StringUtils
/*    */ {
/*    */   public static boolean isEmpty(String string)
/*    */   {
/* 26 */     if (string == null) {
/* 27 */       return true;
/*    */     }
/* 29 */     if ("".equals(string.trim())) {
/* 30 */       return true;
/*    */     }
/* 32 */     return false;
/*    */   }
/*    */ 
/*    */   public static boolean isValidEmailAddress(String emailAddress)
/*    */   {
/* 42 */     if (isEmpty(emailAddress)) {
/* 43 */       return false;
/*    */     }
/*    */ 
/* 46 */     if (emailAddress.indexOf("@") < 0) {
/* 47 */       return false;
/*    */     }
/*    */ 
/* 50 */     if (emailAddress.indexOf(".") < 0) {
/* 51 */       return false;
/*    */     }
/* 53 */     if (!lastEmailFieldTwoCharsOrMore(emailAddress)) {
/* 54 */       return false;
/*    */     }
/* 56 */     if (!isWellFormEmail(emailAddress)) {
/* 57 */       return false;
/*    */     }
/* 59 */     return true;
/*    */   }
/*    */ 
/*    */   private static boolean lastEmailFieldTwoCharsOrMore(String emailAddress)
/*    */   {
/* 68 */     if (emailAddress == null)
/* 69 */       return false;
/* 70 */     StringTokenizer st = new StringTokenizer(emailAddress, ".");
/* 71 */     String lastToken = null;
/* 72 */     while (st.hasMoreTokens()) {
/* 73 */       lastToken = st.nextToken();
/*    */     }
/*    */ 
/* 76 */     if (lastToken.length() >= 2) {
/* 77 */       return true;
/*    */     }
/* 79 */     return false;
/*    */   }
/*    */ 
/*    */   public static boolean isWellFormEmail(String email)
/*    */   {
/* 89 */     String regex = "^[A-Za-z0-9!#$%&'*\\+\\-/=?^_`{|}~-]+(\\.[A-Za-z0-9!#$%&'*\\+\\-/=?^_`{|}~-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z]{2,}){1}$)";
/* 90 */     return email.matches(regex);
/*    */   }
/*    */ }

/* Location:           /Users/yuval.twig/Desktop/google-alert-api.jar
 * Qualified Name:     org.nnh.utils.StringUtils
 * JD-Core Version:    0.6.2
 */