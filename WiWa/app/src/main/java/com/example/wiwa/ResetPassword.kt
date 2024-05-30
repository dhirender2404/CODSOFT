package com.example.wiwa

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wiwa.databinding.FragmentResetPasswordBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ResetPassword : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentResetPasswordBinding
    private lateinit var email : EditText
    private lateinit var sendBtn : Button
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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_reset_password,container, false)
        email = binding.enteredEmail
        sendBtn = binding.submit
        nav = binding.nav6
        sendBtn.setOnClickListener {  reset() }
        nav.setOnItemReselectedListener { item->
            when(item.itemId){
                R.id.backHome->{
                    if(arguments?.getString("Type").toString() == "Student") {
                        findNavController().navigate(R.id.action_resetPassword_to_logIn)
                    }
                    if(arguments?.getString("Type").toString() == "Teacher"){
                        findNavController().navigate(R.id.action_resetPassword_to_teacher_login)
                    }
                }
            }
        }
        return binding.root
    }
    private fun reset(){
        val emailA = email.text.toString().trim()

            val mAuth = FirebaseAuth.getInstance()
            mAuth!!.sendPasswordResetEmail(emailA).addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        if(arguments?.getString("Type").toString() == "Student") {
                            findNavController().navigate(R.id.action_resetPassword_to_logIn)
                        }
                        if(arguments?.getString("Type").toString() == "Teacher"){
                            findNavController().navigate(R.id.action_resetPassword_to_teacher_login)
                        }

                        Toast.makeText(context,"email successful sent",Toast.LENGTH_LONG).show()

                    }
                    else{
                        Toast.makeText(context,"email not sent",Toast.LENGTH_LONG).show()
                    }

                }

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
            ResetPassword().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
