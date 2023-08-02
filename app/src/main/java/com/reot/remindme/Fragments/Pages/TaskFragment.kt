package com.reot.remindme.Fragments.Pages

import TaskManager
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.reot.remindme.Adapters.ListsView.TaskListViewAdapter
import com.reot.remindme.Fragments.Popups.InfoTaskPopupFragment
import com.reot.remindme.Fragments.Popups.InstertOrEditTaskPopupFragment
import com.reot.remindme.Models.DataClasses.Task
import com.reot.remindme.Models.Interfaces.MyDialogListener
import com.reot.remindme.R
import java.text.SimpleDateFormat
import java.util.Date


class TaskFragment : Fragment(), MyDialogListener {
    lateinit var taskFragment: TaskFragment
    lateinit var addTask: Button
    lateinit var txt:TextView
    lateinit var taskListView:ListView
    lateinit var taskManager:TaskManager
    lateinit var tasks:MutableList<Task>
    lateinit var globalContext:Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view:View =inflater.inflate(R.layout.fragment_task, container, false)
        taskFragment = this
        globalContext = requireContext()
        taskListView=view.findViewById(R.id.taskListView)
        txt=view.findViewById(R.id.txt)
        getDataTask()

        addTask=view.findViewById(R.id.addTask)
        addTask.setOnClickListener{
            val popupFragment = InstertOrEditTaskPopupFragment(taskFragment,"Add Task")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            popupFragment.show(transaction, "popup")
        }

        taskListView.onItemClickListener = AdapterView.OnItemClickListener { _, view, position, _ ->
            showPopupMenu(view,position)
        }
        return view
    }


    @SuppressLint("SimpleDateFormat")
    private fun showPopupMenu(view: View, position:Int) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.task_list_menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.finish -> {
                    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
                    val currentDate = Date()
                    val formattedDate = dateFormat.format(currentDate)
                    tasks[position].isFinish=true
                    tasks[position].actuallyFinishDay=formattedDate
                    taskManager.update(tasks[position])

                    getDataTask()
                    true
                }
                R.id.info ->{
                    val popupFragment = InfoTaskPopupFragment(taskFragment,tasks[position])
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    popupFragment.show(transaction, "popup")
                    true
                }
                R.id.edit -> {
                    val popupFragment = InstertOrEditTaskPopupFragment(taskFragment,"Edit Task",tasks[position])
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    popupFragment.show(transaction, "popup")
                    getDataTask()
                    true
                }
                R.id.delete -> {
                    taskManager.delete(tasks[position].id!!)
                    getDataTask()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun getDataTask() {
        if (isAdded) {
            globalContext = requireContext()
            taskManager = TaskManager(globalContext)
            val tasksList = taskManager.getTasks("where isFinish=0")
            tasks = tasksList.toMutableList()
            if(tasks.size==0){
                txt.visibility=View.VISIBLE
                taskListView.visibility=View.GONE
            }else{
                txt.visibility=View.GONE
                taskListView.visibility=View.VISIBLE

            }
            tasks = tasks.sortedBy { it.getFinishDateAsDate() }.toMutableList()
            taskListView.adapter = TaskListViewAdapter(globalContext, tasks)
        }
    }
    override fun onResume() {
        super.onResume()
        getDataTask()
    }
    override fun onCloseDialog() {
        getDataTask()
    }

}