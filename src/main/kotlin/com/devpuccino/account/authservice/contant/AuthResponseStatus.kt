package com.devpuccino.account.authservice.contant

enum class AuthResponseStatus (val code:String, val message:String){
    SUCCESS("200-000","Success"),
    API_FORBIDDEN_ERROR("403-001","Url is forbidden"),
    UNEXPECTED_ERROR("500-001","Unexpected error"),
}