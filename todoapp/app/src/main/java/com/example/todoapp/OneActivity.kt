package com.example.todoapp

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.todoapp.database.TodoDatabase
import com.example.todoapp.fragments.HomeFragment
import com.example.todoapp.repository.TodoRepository
import com.example.todoapp.viewmodel.TodoViewModel
import com.example.todoapp.viewmodel.TodoViewModelFactory


class OneActivity : AppCompatActivity() {

    public lateinit var todoViewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one)

        setupViewModel()
    }

    private fun setupViewModel(){
        val todoRepository = TodoRepository(TodoDatabase(this))
        val viewModelProviderFactory= TodoViewModelFactory(application, todoRepository)
        todoViewModel = ViewModelProvider(this,viewModelProviderFactory)[TodoViewModel::class.java]
    }

    override fun onBackPressed() {
        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val fragment = navHostFragment!!.childFragmentManager.fragments[0]
        if(fragment is HomeFragment){
            showdialog()

        }
        else
        {
            super.onBackPressed()
        }
    }

    private fun showdialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("EXIT")
        builder.setMessage("Do you want to exit?")
        builder.setPositiveButton("Yes") { _, _ ->
            finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}