package com.example.wiwa

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.wiwa.databinding.FragmentTeacherAccountManagementBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TeacherAccountManagement : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentTeacherAccountManagementBinding
    private lateinit var enteredCurrentPass : EditText
    private lateinit var enteredNewPass: EditText
    private lateinit var enteredConfirm : EditText
    private lateinit var submit : TextView
    private lateinit var auth : FirebaseAuth
    private lateinit var email : TextView
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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_teacher_account_management, container, false)
        enteredCurrentPass = binding.editText2
        enteredNewPass = binding.editText3
        enteredConfirm = binding.editText4
        submit = binding.button2
        auth = FirebaseAuth.getInstance()
        email = binding.textView
        nav = binding.bottomNavigationView
        submit.setOnClickListener { changePassword() }
        email.text = FirebaseAuth.getInstance().currentUser!!.email

        nav.setOnItemReselectedListener { item->
            when(item.itemId){
                R.id.backHome->{
                    if(findNavController().currentDestination?.id == R.id.teacherAccountManagement){
                        val model = ViewModelProvider(activity!!).get(GeneralCommunicator::class.java)
                        model.setNameCommunicator(FirebaseAuth.getInstance().currentUser?.email!!)
                        findNavController().navigate(R.id.action_teacherAccountManagement_to_teacherHomePage)
                    }
                }
            }
        }



        return binding.root
    }

    private fun changePassword(){

        if(enteredCurrentPass.text.isNotEmpty()&&
            enteredNewPass.text.isNotEmpty()&&
            enteredConfirm.text.isNotEmpty()){
            if(enteredNewPass.text.toString().equals(enteredConfirm.text.toString())){
                val user = auth.currentUser
                if(user!=null){
                    val credential = EmailAuthProvider
                        .getCredential(user.email!!, enteredCurrentPass.text.toString())
                    user?.reauthenticate(credential)?.addOnCompleteListener{
                        if(it.isSuccessful){
                            user?.updatePassword(enteredNewPass.text.toString())
                                ?.addOnCompleteListener{task->
                                    if(task.isSuccessful){
                                        auth.signOut()
                                        if(findNavController().currentDestination?.id == R.id.teacherAccountManagement){
                                            findNavController().navigate(R.id.mainPage)
                                        }
                                    }
                                }
                        }
                    }
                }

            }else{
                enteredConfirm.setError("Password mismatching")

            }


        }else{
            if(enteredCurrentPass.text.isEmpty()){
                enteredCurrentPass.setError("Please enter current password.")
            }
            if(enteredNewPass.text.isEmpty()){
                enteredCurrentPass.setError("Please enter new password.")
            }
            if(enteredConfirm.text.isEmpty()){
                enteredConfirm.setError("Please re-enter password to confirm.")
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
            TeacherAccountManagement().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
