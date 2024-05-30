package com.example.wiwa

class Student(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email : String,
    val courseId : HashMap<String,String>
   ){
    constructor():this("","","","",HashMap()){
    }

}