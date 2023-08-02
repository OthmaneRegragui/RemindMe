package com.reot.remindme.Fragments.Popups

import TaskManager
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.reot.remindme.Models.DataClasses.Task
import com.reot.remindme.Models.Interfaces.MyDialogListener
import com.reot.remindme.Notification.Broadcasts.NotificationReceiver
import com.reot.remindme.Notification.Services.NotificationService
import com.reot.remindme.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class InstertOrEditTaskPopupFragment(private val listener: MyDialogListener, var Title:String, private val task:Task?=null) : DialogFragment() {
    lateinit var txtTitle:TextView
    lateinit var taskName:EditText
    lateinit var taskDescription:EditText
    lateinit var btnTime: Button
    private lateinit var selectedDateTime: Calendar
    lateinit var btnSave: Button


    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view:View=inflater.inflate(R.layout.fragment_instert_or_edit_task_popup, container, false)

        txtTitle=view.findViewById(R.id.title)
        taskName=view.findViewById(R.id.taskName)
        taskDescription=view.findViewById(R.id.taskDescription)
        btnTime=view.findViewById(R.id.btnTime)
        btnSave=view.findViewById(R.id.btnSave)
        txtTitle.text=Title
        if(task!=null){
            btnSave.text="Edit"
            taskName.setText(task.taskName)
            taskDescription.setText(task.taskDescription)
            btnTime.text = task.finishDate
        }

        btnTime.setOnClickListener{
            showDateTimePicker(view.context)
        }

        btnSave.setOnClickListener{
            var txtTaskName=taskName.text.toString()
            var txtTaskDescription=taskDescription.text.toString()
            var txtBtnTime=btnTime.text.toString()
            val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
            val currentDate = Date()
            val formattedDate = dateFormat.format(currentDate)

            if (txtTaskName.trim().isEmpty()) {
                txtTaskName = " " // Fill with a space
                showToast(view.context,"Task name cannot be empty")
            }else if (txtTaskDescription.trim().isEmpty()) {
                txtTaskDescription = " " // Fill with a space
                showToast(view.context,"Description cannot be empty")
            }else if (txtBtnTime.trim().isEmpty() || txtBtnTime=="Set Time") {
                txtBtnTime = " " // Fill with a space
                showToast(view.context,"Time cannot be empty")
            }else{
                val taskManager:TaskManager=TaskManager(view.context)
                if (task==null){
                    val t:Task=Task(null,txtTaskName,txtTaskDescription,formattedDate,txtBtnTime,"",false)
                    taskManager.insert(t)
                    showToast(view.context,"Task inserted")
                    scheduleNotification(view.context,txtTaskName,txtTaskDescription,formattedDate)
                    dismiss()
                }else{
                    task.taskName=txtTaskName
                    task.taskDescription=txtTaskDescription
                    task.finishDate=txtBtnTime
                    taskManager.update(task)
                    showToast(view.context,"Task updated")
                    updateServiceNotification(view.context,txtTaskName,txtTaskDescription,formattedDate)
                    dismiss()
                }


            }

        }

        return view
    }
    override fun onStart() {
        super.onStart()

        // Get the size of the screen
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val screenWidth = size.x
        val screenHeight = size.y

        val desiredWidth = (screenWidth * 0.9).toInt()
        val desiredHeight = (screenHeight * 0.6).toInt()

        // Set the dialog size
        dialog?.window?.setLayout(desiredWidth, desiredHeight)
    }
    private fun showToast(context: Context,message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDateTimePicker(context:Context) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)

        // Show DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            context,
            { _, yearSelected, monthOfYear, dayOfMonthSelected ->
                // Update the selected date
                selectedDateTime = Calendar.getInstance()
                selectedDateTime.set(yearSelected, monthOfYear, dayOfMonthSelected)

                // Show TimePickerDialog
                showTimePicker(context)
            },
            year,
            month,
            dayOfMonth
        )

        // Set a minimum date to restrict previous dates
        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
        datePickerDialog.show()
    }

    private fun showTimePicker(context: Context) {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedDateTime.set(Calendar.MINUTE, minute)
                formatAndDisplayDateTime(selectedDateTime.time)
            },
            currentHour,
            currentMinute,
            false
        )
        timePickerDialog.show()
    }

    private fun formatAndDisplayDateTime(dateTime: Date) {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        val formattedDateTime = dateFormat.format(dateTime)
        btnTime.text = formattedDateTime
    }

    fun scheduleNotification(context: Context, serviceName: String,taskDescription:String, dateTime: String) {
        val intent = Intent(context, NotificationService::class.java)
        intent.putExtra("service_name", serviceName)
        intent.putExtra("task_description", taskDescription)
        intent.putExtra("date_time", dateTime)
        context.startService(intent)
    }
    fun updateServiceNotification(context: Context, serviceName: String,taskDescription:String, dateTime: String) {
        // First, cancel the existing notification (if any)
        cancelServiceNotification(context)

        // Schedule a new notification with the updated date and time
        scheduleNotification(context, serviceName, taskDescription, dateTime)
    }
    @SuppressLint("ServiceCast")
    fun cancelServiceNotification(context: Context) {
        val notificationIntent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        // If the pending intent exists, cancel the scheduled notification
        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
    override fun onStop() {
        super.onStop()
        listener.onCloseDialog()
    }
}