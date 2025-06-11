package com.intheme.dev.utils

enum class ConformationDlgClick {
   YES,NO
}

enum class EVENT_TYPE(val value:Int){
   ADD_BRANCH_ADMIN(1),
   UPDATE_BRANCH_ADMIN(2),
   ADD_SERVICE_MAN(3),
   UPDATE_SERVICE_MAN(4),
   ADD_UPDATE_CUSTOMER(5),

}


enum class MotionToastStyle {
   SUCCESS, ERROR, WARNING, INFO, DELETE, NO_INTERNET;

   fun getName(): String {
      if (this.name.contains("_")) {
         return this.name.replaceFirst("_", " ")
      }
      return this.name
   }
}

enum class VALIDATION_CUSTOMER {
   NAME,EMAIL,MOBILE_NO,BRANCH,APICALL
}






