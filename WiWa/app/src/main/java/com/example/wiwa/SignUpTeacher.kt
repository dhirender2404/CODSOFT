package com.example.wiwa

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.wiwa.databinding.FragmentSignUpTeacherBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SignUpTeacher : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding: FragmentSignUpTeacherBinding
    private lateinit var fname : EditText
    private lateinit var lname : EditText
    lateinit var email : EditText
    lateinit var password : EditText
    private lateinit var submitBtn : TextView
    private lateinit var databaseReference: DatabaseReference
    lateinit var database : FirebaseDatabase
    private lateinit var mAuth : FirebaseAuth
    private lateinit var teacherId : String
    private lateinit var nav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_sign_up_teacher, container, false)
        submitBtn = binding.button
        fname = binding.simpleEditText
        lname = binding.simpleEditText2
        email = binding.simpleEditText5
        password = binding.simpleEditText7
        nav = binding.nav5
        databaseReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()




        submitBtn.setOnClickListener{ view : View ->
            if(fname.text.toString().isNotEmpty()
                && lname.text.toString().isNotEmpty()
                && email.text.toString().isNotEmpty()
                && password.text.toString().isNotEmpty()
                && isEmailValid(email.text.toString())) {

                saveTeacher()
                if(findNavController().currentDestination?.id == R.id.signUpTeacher) {
                    view.findNavController().navigate(R.id.action_signUpTeacher_to_teacher_login)
                }
            }
            else{
                val builder = AlertDialog.Builder(context)
                builder.setTitle("ERROR")
                if(!isEmailValid(email.text.toString())){
                    builder.setMessage("Please enter a valid email address")
                }else {
                    builder.setMessage("Please fill in all the fields!")
                }
                builder.setPositiveButton("Ok"){_, _ ->

                }
                val alert = builder.create()
                alert.show()

            }

            }
        nav.setOnItemReselectedListener {
            item->
            when(item.itemId){
                R.id.backHome->{
                    if(findNavController().currentDestination?.id == R.id.signUpTeacher){
                        findNavController().navigate(R.id.mainPage)
                    }
                }
            }
        }

        return binding.root
    }
    private fun saveTeacher() {
        val firstName = fname.text.toString().trim()
        val lastName = lname.text.toString().trim()
        val emailA = email.text.toString().trim()
        val pass = password.text.toString().trim()


        if(isEmailValid(emailA)) {
            mAuth.createUserWithEmailAndPassword(emailA, pass)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        val ref = FirebaseDatabase.getInstance().getReference("Teacher")
                        teacherId = ref.push().key!!
                        val teacher = Teacher(0,firstName,lastName, emailA)
                        ref.child(teacherId).setValue(teacher)
                        Toast.makeText(context, "register successfully", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "register unsuccessfully", Toast.LENGTH_LONG).show()

                    }
                }
        }
        else{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("ERROR")
            builder.setMessage("Please Enter a valid email!")
            builder.setPositiveButton("Ok"){_, _ ->

            }
            val alert = builder.create()
            alert.show()

        }

    }


    val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    fun isEmailValid(email: String): Boolean {
        return EMAIL_REGEX.toRegex().matches(email)
    }
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpTeacher().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
