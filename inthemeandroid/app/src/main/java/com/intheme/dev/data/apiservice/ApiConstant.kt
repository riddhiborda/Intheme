package com.intheme.dev.data.apiservice

class ApiConstant {
    companion object{
        const val V1 = "v1"

        const val ERROR_KEY_MESSAGE = "message"
        const val DEVICE = "device"
        const val AUTHORIZATION = "Authorization"
        const val KEY_BEARER = "Bearer"

        const val AUTH = "auth"
        const val ADMIN = "admin"
        const val BRANCH = "branch"
        const val BRANCH_ADMIN = "branch-admin"
        const val SERVICE_MAN = "service-man"
        const val CUSTOMER = "customer"
        const val EQUIPMENT = "equipment"

        /**Common Params*/
        const val PARAM_ID = "id"
        const val PARAM_PAGE = "currentPage"
        const val PARAM_LIMIT = "limit"
        const val PARAM_NAME = "name"
        const val PARAM_MOBILE_NO = "mobileNo"
        const val PARAM_EMAIL = "email"
        const val PARAM_BRANCH_ID = "branch_id"

        /**Login Api*/
        /*URL*/
        const val LOGIN_API_END_POINT = "${V1}/${AUTH}/login"

        const val VERIFY_OTP_END_POINT = "${V1}/${AUTH}/verifyOtp"

        const val REFRESH_TOKEN_END_POINT = "${V1}/${AUTH}/refresh-token"

        const val LOGOUT_API_END_POINT = "${V1}/${AUTH}/logout"

        const val UPDATE_ADMIN_END_POINT = "${V1}/${ADMIN}/update"

        const val GET_BRANCH_LIST_END_POINT = "${V1}/${BRANCH}/get-branch-list"

        const val UPDATE_BRANCH_END_POINT = "${V1}/${BRANCH}/update-branch"

        const val ADD_BRANCH_END_POINT = "${V1}/${BRANCH}/add-branch"

        const val DELETE_BRANCH_END_POINT = "${V1}/${BRANCH}/delete-branch"

        const val GET_BRANCH_ADMIN_LIST_END_POINT = "${V1}/${BRANCH_ADMIN}/get-list"

        const val UPDATE_BRANCH_ADMIN_END_POINT = "${V1}/${BRANCH_ADMIN}/update"

        const val ADD_BRANCH_ADMIN_END_POINT = "${V1}/${BRANCH_ADMIN}/add"

        const val DELETE_BRANCH_ADMIN_END_POINT = "${V1}/${BRANCH_ADMIN}/delete"

        const val GET_SERVICE_MAN_LIST_END_POINT = "${V1}/${SERVICE_MAN}/get-list"

        const val UPDATE_SERVICE_MAN_END_POINT = "${V1}/${SERVICE_MAN}/update"

        const val ADD_SERVICE_MAN_END_POINT = "${V1}/${SERVICE_MAN}/add"

        const val DELETE_SERVICE_MAN_END_POINT = "${V1}/${SERVICE_MAN}/delete"

        const val ADD_CUSTOMER_END_POINT = "${V1}/${CUSTOMER}/add"

        const val UPDATE_CUSTOMER_END_POINT = "${V1}/${CUSTOMER}/update"

        const val DELETE_CUSTOMER_END_POINT = "${V1}/${CUSTOMER}/delete"

        const val GET_CUSTOMER_LIST_END_POINT = "${V1}/${CUSTOMER}/get-list"

        const val GET_EQUIPMENT_LIST_END_POINT = "${V1}/${EQUIPMENT}/get-list"

        const val DELETE_EQUIPMENT_END_POINT = "${V1}/${EQUIPMENT}/delete"

    }
}