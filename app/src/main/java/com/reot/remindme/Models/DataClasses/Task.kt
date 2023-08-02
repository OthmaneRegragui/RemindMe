package com.reot.remindme.Models.DataClasses

import java.text.SimpleDateFormat
import java.util.Date

data class Task(var id:Int?, var taskName:String, var taskDescription:String, var insertDate: String, var finishDate: String, var actuallyFinishDay: String, var isFinish:Boolean){
    fun getFinishDateAsDate(): Date {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
        return dateFormat.parse(finishDate) ?: Date()
    }
}