package com.reot.remindme.Adapters.ListsView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.reot.remindme.Models.DataClasses.Task
import com.reot.remindme.R
import kotlin.random.Random

class TaskListViewAdapter(private val context:Context, private val taskList:MutableList<Task>):BaseAdapter() {
    override fun getCount(): Int {
        return taskList.size
    }

    override fun getItem(p0: Int): Task {
        return taskList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder", "MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view:View=LayoutInflater.from(context).inflate(R.layout.task_list_view,parent,false)


        val colorIds = listOf(
            R.color.orange,
            R.color.yellow,
            R.color.white_blue
        )

        val linearLayout: LinearLayout = view.findViewById(R.id.linearLayout)
        val randomColorId = colorIds[Random.nextInt(colorIds.size)]
        val randomColor = ContextCompat.getColor(context, randomColorId)
        val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.stroke) as GradientDrawable
        backgroundDrawable.setColor(randomColor)
        linearLayout.background = backgroundDrawable

        val taskName:TextView=view.findViewById(R.id.taskName)
        val taskDescription:TextView=view.findViewById(R.id.taskDescription)
        val finishDate:TextView=view.findViewById(R.id.finishDate)
        taskName.text=taskList[position].taskName
        taskDescription.text=taskList[position].taskDescription
        finishDate.text=taskList[position].finishDate

        return view
    }
}