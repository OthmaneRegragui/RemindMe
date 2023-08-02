package com.reot.remindme.Fragments.Pages

import TaskManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import com.reot.remindme.Adapters.ListsView.TaskListViewAdapter
import com.reot.remindme.Fragments.Popups.InfoTaskPopupFragment
import com.reot.remindme.Fragments.Popups.InstertOrEditTaskPopupFragment
import com.reot.remindme.Models.DataClasses.Task
import com.reot.remindme.Models.Interfaces.MyDialogListener
import com.reot.remindme.R
import java.text.SimpleDateFormat
import java.util.Date


class ArchiveFragment : Fragment(), MyDialogListener {
    lateinit var archiveFragment: ArchiveFragment
    lateinit var globalContext: Context
    lateinit var taskManager:TaskManager
    lateinit var tasks:MutableList<Task>
    lateinit var txt: TextView
    lateinit var archiveListView:ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view:View=inflater.inflate(R.layout.fragment_archive, container, false)
        archiveFragment = this
        archiveListView=view.findViewById(R.id.archiveListView)
        txt=view.findViewById(R.id.txt)

        getDataTask()
        archiveListView.onItemClickListener = AdapterView.OnItemClickListener { _, view, position, _ ->
            showPopupMenu(view,position)
        }
        return view
    }
    private fun showPopupMenu(view: View, position:Int) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.task_list_menu)
        popupMenu.menu.findItem(R.id.edit).isVisible=false
        popupMenu.menu.findItem(R.id.finish).isVisible=false

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.info ->{
                    val popupFragment = InfoTaskPopupFragment(archiveFragment,tasks[position])
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    popupFragment.show(transaction, "popup")
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
    fun getDataTask() {
        if (isAdded) {
            globalContext = requireContext()
            taskManager = TaskManager(globalContext)
            val tasksList = taskManager.getTasks("where isFinish=1")
            tasks = tasksList.toMutableList()
            if(tasks.size==0){
                txt.visibility=View.VISIBLE
                archiveListView.visibility=View.GONE
            }else{
                txt.visibility=View.GONE
                archiveListView.visibility=View.VISIBLE

            }
            tasks = tasks.sortedBy { it.getFinishDateAsDate() }.toMutableList()
            archiveListView.adapter = TaskListViewAdapter(globalContext, tasks)
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