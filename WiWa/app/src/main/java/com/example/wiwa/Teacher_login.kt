package com.example.wiwa

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.wiwa.databinding.FragmentTeacherLoginBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Teacher_login : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentTeacherLoginBinding
    lateinit var email : EditText
    lateinit var password : EditText
    private lateinit var logInBtn : TextView
    private lateinit var mAuth : FirebaseAuth
    lateinit var model : GeneralCommunicator
    private lateinit var forgetPass : TextView
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
        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_teacher_login, container, false)
        email = binding.simpleEditText
        password = binding.simpleEditText3
        logInBtn = binding.button
        forgetPass = binding.forgetPass
        nav = binding.nav4
        model = ViewModelProvider(activity!!).get(GeneralCommunicator::class.java)
        mAuth = FirebaseAuth.getInstance()

        logInBtn.setOnClickListener { view: View ->
            if(email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()){
            binding.loadingpanel1.visibility = View.VISIBLE
            val emailA = email.text.toString().trim()
            val pass = password.text.toString().trim()
            val refTeacher = FirebaseDatabase.getInstance().getReference("Teacher")
                if (emailA.isNotEmpty() && pass.isNotEmpty()) {
                this.mAuth.signInWithEmailAndPassword(emailA, pass).addOnCompleteListener { task: Task<AuthResult> ->
                        if (task.isSuccessful) {
                            refTeacher.orderByChild("email").equalTo(emailA).addValueEventListener(
                                object : ValueEventListener {
                                    override fun onDataChange(p0: DataSnapshot) {
                                        if (p0.exists()) {
                                            if (findNavController().currentDestination?.id == R.id.teacher_login) {
                                                model.setMsgCommunicator(emailA)
                                                findNavController().navigate(R.id.action_teacher_login_to_teacherHomePage)
                                            }
                                        } else {
                                           binding.loadingpanel1.visibility = View.GONE
                                            email.error = "Teacher Email Not Exists"
                                            password.error = "Teacher Email Not Exists"
                                        }
                                    }

                                    override fun onCancelled(p0: DatabaseError) {
                                    }
                                }
                            )
                        } else {
                           binding.loadingpanel1.visibility = View.GONE
                            email.error = "Incorrect Credentials"
                            password.error = "Incorrect Credentials"
                        }
                    }
            }
            }else{
                email.error = "Please enter teacher email"
                password.error = "Please enter teacher password"
            }
        }

            forgetPass.setOnClickListener{view : View->
                val bundle: Bundle = bundleOf("Type" to "Teacher")
                view.findNavController().navigate(R.id.action_teacher_login_to_resetPassword, bundle)
            }
            nav.setOnItemReselectedListener {item->
                when(item.itemId){
                    R.id.backHome->{
                        if(findNavController().currentDestination?.id == R.id.teacher_login){
                            findNavController().navigate(R.id.mainPage)
                        }
                    }
                }
            }
            return binding.root
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
            Teacher_login().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
