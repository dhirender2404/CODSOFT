package com.example.wiwa

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.wiwa.databinding.FragmentMainPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MainPage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding: FragmentMainPageBinding
    private lateinit var logInBtn : TextView
    private lateinit var signUpBtn : TextView
    private lateinit var sw : SwitchCompat
    private lateinit var afterAnimationn : ConstraintLayout
    private lateinit var model : GeneralCommunicator

    private var isCheck : Boolean? = null
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

        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_main_page, container, false)
        signUpBtn = binding.button
        logInBtn = binding.button1
        sw = binding.switch1
        afterAnimationn = binding.afterAnimation
        sw.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Teacher" else "Student"
            sw.text = msg
            isCheck = isChecked
        }

        signUpBtn.setOnClickListener{ view : View ->

                if(isCheck == true){
                    if(findNavController().currentDestination?.id == R.id.mainPage) {
                        view.findNavController().navigate(R.id.signUpTeacher)
                    }
                }
                else{
                   if(findNavController().currentDestination?.id == R.id.mainPage) {
                        view.findNavController().navigate(R.id.signUpStudent)
                    }
                }



        }
        logInBtn.setOnClickListener {view:View->

                if(isCheck == true) {
                   if(findNavController().currentDestination?.id == R.id.mainPage) {
                        view.findNavController().navigate(R.id.teacher_login)
                    }
                    }
                else{
                    if(findNavController().currentDestination?.id == R.id.mainPage) {
                        view.findNavController().navigate(R.id.logIn)
                    }
                }

        }

        val user = FirebaseAuth.getInstance().currentUser
        if(user!=null){
            val refStudent = FirebaseDatabase.getInstance().getReference("Student")
            val refTeacher = FirebaseDatabase.getInstance().getReference("Teacher")
            refStudent.orderByChild("email").equalTo(user.email).addValueEventListener(object:
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        if(findNavController().currentDestination?.id == R.id.mainPage){
                           model = ViewModelProvider(activity!!).get(GeneralCommunicator::class.java)
                            model.setMsgCommunicator(user.email!!)
                            findNavController().navigate(R.id.action_mainPage_to_studentHomePage)
                        }}
                }
                     })
            refTeacher.orderByChild("email").equalTo(user.email).addValueEventListener(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()) {
                            if (findNavController().currentDestination?.id == R.id.mainPage) {
                                model = ViewModelProvider(activity!!).get(GeneralCommunicator::class.java)
                                model.setMsgCommunicator(user.email!!)
                                findNavController().navigate(R.id.action_mainPage_to_teacherHomePage)
                            }
                        }
                    }
                }
            )

        }else {


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
            MainPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}