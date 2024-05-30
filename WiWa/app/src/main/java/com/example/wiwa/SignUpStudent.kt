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
import com.example.wiwa.databinding.FragmentSignUpStudentBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SignUpStudent : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding: FragmentSignUpStudentBinding
    private lateinit var submitBtn : TextView
    private lateinit var fName : EditText
    private lateinit var lName : EditText
    private lateinit var pWord : EditText
    lateinit var email : EditText
    private lateinit var databaseReference: DatabaseReference
    lateinit var database : FirebaseDatabase
    private lateinit var mAuth : FirebaseAuth
    private lateinit var studentId : String
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
    ): View? {
        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_sign_up_student, container, false)
        submitBtn = binding.button
        fName = binding.simpleEditText
        lName = binding.simpleEditText3
        pWord = binding.simpleEditText6
        email = binding.simpleEditText4
        nav = binding.nav
        databaseReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        submitBtn.setOnClickListener{ view : View ->
            if(fName.text.toString().isNotEmpty()
                && lName.text.toString().isNotEmpty()
                && email.text.toString().isNotEmpty()
                && pWord.text.toString().isNotEmpty()
                && isEmailValid(email.text.toString())) {
                saveStudent()
                if(findNavController().currentDestination?.id == R.id.signUpStudent) {
                    view.findNavController().navigate(R.id.action_signUpStudent_to_logIn)
                }
            }else{
                val builder = AlertDialog.Builder(context)
                builder.setTitle("ERROR")
                if(!isEmailValid(email.text.toString())){
                    builder.setMessage("Please enter a valid email address")
                }else {
                    builder.setMessage("Please fill in all the fields!")
                }
                builder.setPositiveButton("Ok"){ _, _ ->
                }
                val alert = builder.create()
                alert.show()

            }
        }
        nav.setOnItemReselectedListener { item->
            when(item.itemId){
                R.id.backHome->{
                    if(findNavController().currentDestination?.id == R.id.signUpStudent){
                        findNavController().navigate(R.id.mainPage)
                    }
                }
            }
        }

        return binding.root
    }

    private fun saveStudent() {
        val firstName = fName.text.toString().trim()
        val lastName = lName.text.toString().trim()
        val password = pWord.text.toString().trim()
        val emailA = email.text.toString().trim()

        if(isEmailValid(emailA)) {
            val ref = FirebaseDatabase.getInstance().getReference("Student")
            studentId = ref.push().key!!
            val student = Student(studentId,firstName,lastName, emailA, HashMap())
            ref.child(studentId).setValue(student)
            mAuth.createUserWithEmailAndPassword(emailA, password).addOnCompleteListener{ task: Task<AuthResult> ->
                    if (task.isSuccessful) {
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
            builder.setPositiveButton("Ok"){ _, _ ->
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
            SignUpStudent().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
