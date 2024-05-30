package com.example.wiwa

class TeacherLocation(
    val courseId : String,
    val longitude: Double,
    val latitude : Double
) {
    constructor():this("",0.0,0.0)
}