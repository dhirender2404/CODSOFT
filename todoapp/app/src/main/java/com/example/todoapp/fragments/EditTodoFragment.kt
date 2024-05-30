package com.example.todoapp.fragments


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentEditTodoBinding
import com.example.todoapp.model.Todo
import com.example.todoapp.viewmodel.TodoViewModel
import java.util.Calendar
import com.example.todoapp.OneActivity


class EditTodoFragment : Fragment(R.layout.fragment_edit_todo), MenuProvider, View.OnClickListener {

    private var editTodoBinding: FragmentEditTodoBinding? = null
    private val binding get() = editTodoBinding!!

    private lateinit var  todoViewModel: TodoViewModel
    private lateinit var currentTodo: Todo

    private lateinit var myCalendar: Calendar

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    private lateinit var finalDate :String
    private lateinit var finalTime : String

    private val args: EditTodoFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        editTodoBinding = FragmentEditTodoBinding.inflate(inflater,container,false)
        binding.editdateEdt.setOnClickListener(this)
        binding.edittimeEdt.setOnClickListener(this)
        setUpSpinner()
        return binding.root
    }

    private fun setUpSpinner() {
        val adapter =
            context?.let { ArrayAdapter<String>(it, android.R.layout.simple_spinner_dropdown_item, AddTodoFragment.labels) }
        binding.editspinnerCategory.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this,viewLifecycleOwner, Lifecycle.State.RESUMED)

        todoViewModel = (activity as OneActivity).todoViewModel
        currentTodo = args.todo!!

        binding.editTodoTitle.setText(currentTodo.todoTitle)
        binding.editTodoDesc.setText(currentTodo.todoDesc)
        binding.editdateEdt.setText(currentTodo.date)
        binding.edittimeEdt.setText(currentTodo.time)
        var index = AddTodoFragment.labels.indexOf(currentTodo.priori)
        binding.editspinnerCategory.setSelection(index)

        binding.editTodoFab.setOnClickListener{
            val  priori = binding.editspinnerCategory.selectedItem.toString()
            val todoTitle =  binding.editTodoTitle.text.toString().trim()
            val todoDesc = binding.editTodoDesc.text.toString().trim()
            val finalDate = binding.editdateEdt.text.toString().trim()
            val finalTime = binding.edittimeEdt.text.toString().trim()
            val statU = "Not Completed"



            if (todoTitle.isNotEmpty() && todoDesc.isNotEmpty() && finalTime.isNotBlank() && finalDate.isNotBlank() && statU.isNotBlank() && priori.isNotBlank()){
                val todo = Todo(currentTodo.id, todoTitle, todoDesc, finalDate, finalTime, statU, priori)
                todoViewModel.updateTodo(todo)
                view.findNavController().popBackStack(R.id.homeFragment, false)
            }else{
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun deleteTodo(){
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Task")
            setMessage("Do you want to delete this task?")
            setPositiveButton("Delete"){_,_ ->
                todoViewModel.deleteTodo(currentTodo)
                Toast.makeText(context, "Task Deleted",Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("cancel", null)
        }.create().show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_todo, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.deleteMenu -> {
                deleteTodo()
                true
            }else -> false
        }
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.editdateEdt -> {
                setListener()
            }
            R.id.edittimeEdt -> {
                setTimeListener()
            }

        }

    }


    private fun setTimeListener() {
        myCalendar = Calendar.getInstance()

        timeSetListener =
            TimePickerDialog.OnTimeSetListener() { _: TimePicker, hourOfDay: Int, min: Int ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar.set(Calendar.MINUTE, min)
                updateTime(hourOfDay,min)
            }

        val timePickerDialog = TimePickerDialog(context, timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY),
            myCalendar.get(Calendar.MINUTE), true
        )
        timePickerDialog.show()
    }

    private fun updateTime(hourOfDay: Int, min: Int) {
        finalTime = "$hourOfDay:$min"
        binding.edittimeEdt.setText(finalTime)

    }

    private fun setListener() {
        myCalendar = Calendar.getInstance()

        dateSetListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDate(year,month,dayOfMonth)

            }

        val datePickerDialog = DatePickerDialog(requireContext(), dateSetListener, myCalendar.get(
            Calendar.YEAR),
            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }


    private fun updateDate(year: Int, month: Int, dayOfMonth: Int) {
        var mon = month +1
        finalDate = "$dayOfMonth/$mon/$year"
        binding.editdateEdt.setText(finalDate)

        binding.timeInptLay.visibility = View.VISIBLE

    }


    override fun onDestroy() {
        super.onDestroy()
        editTodoBinding= null
    }

}