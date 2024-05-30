package com.example.wiwa

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.wiwa.databinding.FragmentStudentEnrollBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StudentEnroll : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentStudentEnrollBinding
    private lateinit var addCourseBtn : TextView
    private lateinit var btnNav : BottomNavigationView
    private lateinit var spinnerList : Spinner
    private var arr = ArrayList<String>()

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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_student_enroll,container,false)
        addCourseBtn = binding.addClassBtn
        spinnerList = binding.courseInput
        btnNav = binding.bottomNavigationView
                val courseRef = FirebaseDatabase.getInstance().getReference("Course")
                courseRef.addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        arr.clear()

                        for(e in p0.children){
                            val course = e.getValue(Course::class.java)
                            arr.add(course!!.courseName + " -- " + course!!.professorName)
                            val studentCourseRef = FirebaseDatabase.getInstance().getReference("Student")
                            val user = FirebaseAuth.getInstance().currentUser
                            studentCourseRef.orderByChild("email").equalTo(user?.email).addValueEventListener(
                                object : ValueEventListener{
                                    override fun onCancelled(p1: DatabaseError) {}
                                    override fun onDataChange(p1: DataSnapshot) {
                                        for(e1 in p1.children){
                                            val studentCourseList = e1.getValue(Student::class.java)!!.courseId.values
                                            if(studentCourseList.contains(course!!.courseId)){
                                                arr.remove(course!!.courseName + " -- " + course!!.professorName)

                                            }

                                        }
                                        val array_adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, arr)
                                        array_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        spinnerList.adapter = array_adapter
                                    }
                                })
                        }


                    }
                })


        spinnerList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val ref = FirebaseDatabase.getInstance().getReference("Course")
                val selected = arr[position].split(" -- ")
                ref.orderByChild("courseName").equalTo(selected[0]).addValueEventListener(
                    object : ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            for(e in p0.children){
                                val f = e.getValue(Course::class.java)!!.courseId
                                addClassFun(f)
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {}
                    }
                )
            }


        }

        btnNav.setOnItemReselectedListener { item ->
            when(item.itemId){
                R.id.backHome ->{
                    if(findNavController().currentDestination?.id == R.id.studentEnroll){
                        val model = ViewModelProvider(activity!!).get(GeneralCommunicator::class.java)
                        val user = FirebaseAuth.getInstance().currentUser
                        model.setMsgCommunicator(user?.email!!)
                        findNavController().navigate(R.id.action_studentEnroll_to_studentHomePage)
                    }
                }
            }
        }


        return binding.root
    }

    private fun addClassFun(courseInput: String){
        addCourseBtn.setOnClickListener {view:View->
            if(findNavController().currentDestination?.id==R.id.studentEnroll) {
                val model = ViewModelProvider(activity!!).get(GeneralCommunicator::class.java)
                val user = FirebaseAuth.getInstance().currentUser
                model.setMsgCommunicator(user?.email!!)
                val databaseReference = FirebaseDatabase.getInstance().reference
                databaseReference.child("Student").child(arguments!!.getString("key").toString()).child("courseId").push().setValue(courseInput)
                view.findNavController().navigate(R.id.action_studentEnroll_to_studentHomePage)
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
            StudentEnroll().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}