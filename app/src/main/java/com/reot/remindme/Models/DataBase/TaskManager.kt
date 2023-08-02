import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.reot.remindme.Models.DataBase.OpenOrCreateDataBase
import com.reot.remindme.Models.DataClasses.Task


class TaskManager(context: Context) {
    private val dbHelper = OpenOrCreateDataBase(context)
    private val database: SQLiteDatabase = dbHelper.writableDatabase

    @SuppressLint("Range")
    fun getTasks(where:String=""): MutableList<Task> {
        val taskList = mutableListOf<Task>()
        val cursor: Cursor = database.rawQuery("select * from tasks $where", null)
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val id:Int=cursor.getInt(cursor.getColumnIndex("id"))
                val taskName:String = cursor.getString(cursor.getColumnIndex("taskName"))
                val taskDescription:String = cursor.getString(cursor.getColumnIndex("taskDescription"))
                val insertDate:String = cursor.getString(cursor.getColumnIndex("insertDate"))
                val finishDate:String = cursor.getString(cursor.getColumnIndex("finishDate"))
                val actuallyFinishDay:String = cursor.getString(cursor.getColumnIndex("actuallyFinishDay"))
                val isFinish:Boolean = cursor.getInt(cursor.getColumnIndex("isFinish"))==1
                val task: Task = Task(id,taskName,taskDescription,insertDate,finishDate,actuallyFinishDay,isFinish)
                taskList.add(task)
                cursor.moveToNext()
            }
        }
        return taskList
    }


    private fun getContentValues(t: Task):ContentValues{
        val contentValues:ContentValues = ContentValues().apply {
            put("taskName", t.taskName)
            put("taskDescription", t.taskDescription)
            put("insertDate", t.insertDate)
            put("finishDate", t.finishDate)
            put("actuallyFinishDay", t.actuallyFinishDay)
            put("isFinish", t.isFinish)
        }
        return  contentValues
    }

    fun insert(t: Task){
        val contentValues:ContentValues = getContentValues(t)
        database.insert("tasks",null,contentValues)
    }
    fun delete(id:Int){
       var x: Int = database.delete("tasks", "id = '$id'", null)
    }
    fun update(t: Task) {
        val contentValues:ContentValues = getContentValues(t)
        database.update("tasks", contentValues, "id = '${t.id}'", null)
    }

}