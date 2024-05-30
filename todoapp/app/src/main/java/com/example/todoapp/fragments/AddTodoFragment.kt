package com.example.todoapp.fragments

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
import com.example.todoapp.OneActivity
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentAddTodoBinding
import com.example.todoapp.viewmodel.TodoViewModel
import java.util.Calendar
class AddTodoFragment : Fragment(R.layout.fragment_add_todo), MenuProvider, View.OnClickListener{


    companion object{
        val labels = arrayListOf<String>("Low", "Medium", "High")
    }

    private var addTodoBinding: FragmentAddTodoBinding? =null
    private val binding get() = addTodoBinding!!
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var addTodoView: View
    private lateinit var myCalendar: Calendar

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    private lateinit var finalDate : String
    private lateinit var finalTime : String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addTodoBinding = FragmentAddTodoBinding.inflate(inflater, container, false)


        binding.dateEdt.setOnClickListener(this)
        binding.timeEdt.setOnClickListener(this)
        setUpSpinner()
        return binding.root
    }

    private fun setUpSpinner() {

        val adapter =
            context?.let { ArrayAdapter<String>(it, android.R.layout.simple_spinner_dropdown_item, labels) }



        binding.spinnerCategory.adapter = adapter

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this,viewLifecycleOwner, Lifecycle.State.RESUMED)

        todoViewModel = (activity as OneActivity).todoViewModel
        addTodoView = view
    }
    private fun saveTodo(view : View){
        val  priori = binding.spinnerCategory.selectedItem.toString()
        val todoTitle= binding.addTodoTitle.text.toString().trim()
        val todoDesc = binding.addTodoDesc.text.toString().trim()
        val finalDate = binding.dateEdt.text.toString().trim()
        val finalTime = binding.timeEdt.text.toString().trim()
        val statU = "Not Completed"






        if(todoTitle.isNotEmpty() && todoDesc.isNotEmpty() && finalTime.isNotBlank() && finalDate.isNotBlank() && statU.isNotBlank() && priori.isNotBlank()) {
            val todo = com.example.todoapp.model.Todo(0, todoTitle, todoDesc, finalDate, finalTime, statU, priori)

            todoViewModel.addTodo(todo)

            Toast.makeText(addTodoView.context, "Task Saved", Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack(R.id.homeFragment, false)
        }else{
            Toast.makeText(addTodoView.context,"Please fill all the fields",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_todo, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId) {
            R.id.saveMenu -> {
                saveTodo(addTodoView)
                true
            }

            else -> false
        }

    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.dateEdt -> {
                setListener()
            }
            R.id.timeEdt -> {
                setTimeListener()
            }
        }

    }
    //set time and date

    private fun setTimeListener() {
        myCalendar = Calendar.getInstance()

        timeSetListener =
            TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, min: Int->
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
        //Mon, 5 Jan 2020
        finalTime = "$hourOfDay:$min"
        binding.timeEdt.setText(finalTime)

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

        val datePickerDialog = DatePickerDialog(requireContext(), dateSetListener, myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }


    private fun updateDate(year: Int, month: Int, dayOfMonth: Int) {
        var mon = month + 1
        finalDate = "$dayOfMonth/$mon/$year"
        binding.dateEdt.setText(finalDate)

        binding.timeInptLay.visibility = View.VISIBLE

    }

    override fun onDestroy() {
        super.onDestroy()
        addTodoBinding = null
    }


}