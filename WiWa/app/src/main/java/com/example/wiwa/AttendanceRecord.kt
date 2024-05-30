package com.example.wiwa

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.wiwa.databinding.FragmentAttendanceRecordBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AttendanceRecord : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentAttendanceRecordBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var courseName : TextView
    private var arr = ArrayList<Record>()
    private lateinit var btnNav : BottomNavigationView
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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_attendance_record, container, false)
        recyclerView = binding.recyclerView2
        courseName = binding.textView17
        btnNav = binding.nv2

        val ref1 = FirebaseDatabase.getInstance().getReference("Course")
        ref1.orderByChild("courseId").equalTo(arguments!!.getString("courseId"))
            .addValueEventListener(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    for(e in p0.children){
                        val course = e.getValue(Course::class.java)
                        courseName.text = course!!.courseName
                    }
                }
            })

        val ref = FirebaseDatabase.getInstance().getReference("Record")
        ref.orderByChild("courseId").equalTo(arguments!!.getString("courseId"))
            .addValueEventListener(
                object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        arr.clear()
                        for(e in p0.children){
                            val record = e.getValue(Record :: class.java)
                            arr.add(record!!)
                        }
                        val adapter = RecordAdapter(arr, context!!)
                        recyclerView.adapter = adapter
                    }
                }
            )
        btnNav.setOnItemReselectedListener { item->
            when(item.itemId){
                R.id.backHome->{
                    if(findNavController().currentDestination?.id == R.id.attendanceRecord) {
                        val bundle:Bundle = bundleOf("courseId" to arguments!!.getString("courseId"))
                        val model = ViewModelProvider(activity!!).get(GeneralCommunicator::class.java)
                        model.setIdCommunicator(arguments!!.getString("courseId").toString())
                        findNavController().navigate(R.id.action_attendanceRecord_to_manageClasses, bundle)
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
            AttendanceRecord().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
