package com.reot.remindme.Fragments.Popups

import TaskManager
import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.reot.remindme.Models.DataClasses.Task
import com.reot.remindme.Models.Interfaces.MyDialogListener
import com.reot.remindme.R
import java.text.SimpleDateFormat
import java.util.Date

class InfoTaskPopupFragment(private val listener: MyDialogListener,var task:Task) : DialogFragment() {
    lateinit var taskName:EditText
    lateinit var taskDescription:EditText
    lateinit var taskStart:EditText
    lateinit var taskEnd:EditText
    lateinit var txtTaskActuallyEnd:TextView
    lateinit var taskActuallyEnd:EditText
    lateinit var btnEndTask:Button
    lateinit var btnClose:Button
    lateinit var taskManager:TaskManager
    @SuppressLint("SimpleDateFormat", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view=inflater.inflate(R.layout.fragment_info_task_popup, container, false)

        taskName=view.findViewById(R.id.taskName)
        taskDescription=view.findViewById(R.id.taskDescription)
        taskStart=view.findViewById(R.id.taskStart)
        taskEnd=view.findViewById(R.id.taskEnd)
        txtTaskActuallyEnd=view.findViewById(R.id.txtTaskActuallyEnd)
        taskActuallyEnd=view.findViewById(R.id.taskActuallyEnd)
        btnEndTask=view.findViewById(R.id.btnEndTask)
        btnClose=view.findViewById(R.id.btnClose)

        taskName.setText(task.taskName)
        taskDescription.setText(task.taskDescription)
        taskStart.setText(task.insertDate)
        taskEnd.setText(task.finishDate)


        if(task.isFinish){
            txtTaskActuallyEnd.visibility=View.VISIBLE
            taskActuallyEnd.visibility=View.VISIBLE
            taskActuallyEnd.setText(task.actuallyFinishDay)
            btnEndTask.isVisible=false

        }

        taskManager = TaskManager(view.context)
        btnEndTask.setOnClickListener{
            val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
            val currentDate = Date()
            val formattedDate = dateFormat.format(currentDate)
            task.isFinish=true
            task.actuallyFinishDay=formattedDate
            taskManager.update(task)

            dismiss()
        }


        btnClose.setOnClickListener{
            dismiss()
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
        val desiredHeight = (screenHeight * 0.8).toInt()

        // Set the dialog size
        dialog?.window?.setLayout(desiredWidth, desiredHeight)
    }
    override fun onStop() {
        super.onStop()
        listener.onCloseDialog()
    }
}