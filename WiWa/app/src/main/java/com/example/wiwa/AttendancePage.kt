package com.example.wiwa

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.wiwa.databinding.FragmentAttendancePageBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.app.ActivityCompat

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AttendancePage : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentAttendancePageBinding
    private lateinit var courseName : TextView
    private lateinit var courseDesc : TextView
    private lateinit var professorName : TextView
    private lateinit var attendanceBtn : TextView
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private val arr = ArrayList<String>()
    private lateinit var navBtn : BottomNavigationView
    private lateinit var model : GeneralCommunicator



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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_attendance_page,container,false)
        courseName = binding.courseName
        courseDesc = binding.courseDescrp
        attendanceBtn = binding.attendance
        professorName = binding.professorName
        navBtn = binding.btnNav3
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)

        model = ViewModelProvider(activity!!).get(GeneralCommunicator::class.java)
        model.id.observe(this,object: Observer<Any?> {
            override fun onChanged(t: Any?) {
                val id = t.toString()!!
                val courseRef = FirebaseDatabase.getInstance().getReference("Course")
                courseRef.orderByChild("courseId").equalTo(id!!).addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        for(e in p0.children){
                            val course = e.getValue(Course::class.java)
                            courseName.text = course?.courseName
                            courseDesc.text = course?.courseDescription
                            professorName.text = course?.professorName
                        }
                    }

                })
                detectAttendanceFun(id!!)
                getLocation(id!!)

                navBtn.setOnItemReselectedListener { item->
                    when(item.itemId){
                        R.id.backHome2->{
                            backFun()
                        }
                        R.id.dropCourse->{
                            dropClass(id!!)
                        }
                    }
                }


            }

        })

        val fm = fragmentManager!!.beginTransaction()
        fm.setReorderingAllowed(false)
        fm.detach(this).attach(this)


        return binding.root
    }
    private fun getLocation(courseId: String){
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity!!.applicationContext)
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if(location!= null){
                    var tLatitude = 0.0
                    var tLongtitude = 0.0

                    val ref = FirebaseDatabase.getInstance().getReference("TeacherLocation")
                    ref.orderByChild("courseId").equalTo(courseId)
                        .addValueEventListener(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {}
                            override fun onDataChange(p0: DataSnapshot) {
                                for(e in p0.children){
                                    val tLocation = e.getValue(TeacherLocation::class.java)
                                    tLatitude = tLocation?.latitude!!
                                    tLongtitude = tLocation?.longitude!!
                                }


                                val arr = FloatArray(1)
                                
                                if(arr[0] < 80.0){
                                    attendanceBtn.setOnClickListener { takeAttendance(courseId)}
                                }else{
                                    attendanceBtn.setOnClickListener {
                                    /**    val builder = AlertDialog.Builder(context)
                                        builder.setTitle("FATAL")
                                        builder.setMessage("YOU ARE NOT IN CLASSROOM!!")
                                        val alert = builder.create()
                                        alert.setIcon(R.drawable.angry)
                                        alert.show() **/
                                        takeAttendance(courseId)
                                    }
                                }


                            }
                        })

                }
                else{
                    Toast.makeText(context!!,"Hey location is null", Toast.LENGTH_LONG).show()
                }


            }

    }

   private fun takeAttendance(courseId: String){

           val model = ViewModelProvider(activity!!).get(GeneralCommunicator::class.java)
            model.name.observe(this, object : Observer<Any> {
               override fun onChanged(t: Any) {
                   val name = t.toString()!!
                   arr.add(name)
                   val attendanceResult = FirebaseDatabase.getInstance().getReference("AttendanceResult")
                   val key = attendanceResult.push().key
                   val attendance = AttendanceResult(courseId, name)
                   attendanceResult.child(key!!).setValue(attendance)
                   val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss")
                   val currentDate = sdf.format(Date())
                   val record = Record(courseId, currentDate, arr)
                   val ref1 = FirebaseDatabase.getInstance().getReference("Record")
                   val keys = ref1.push().key!!
                   ref1.child(keys).setValue(record)
               }
           })

           attendanceBtn.alpha = 0.5f
           attendanceBtn.isEnabled = false
           val builder = AlertDialog.Builder(context)
           builder.setTitle("Success")
           builder.setMessage("Your attendance is recorded!")
           builder.setPositiveButton("Ok") { dialog, which ->

           }
           val alert = builder.create()
           alert.show()


   }
   private fun detectAttendanceFun(courseId : String){
       val attendanceIndicatorRef = FirebaseDatabase.getInstance().getReference("AttendanceIndicator")
       attendanceIndicatorRef.orderByChild("courseId").equalTo(courseId).addValueEventListener(
           object : ValueEventListener{
               override fun onCancelled(p0: DatabaseError) {}
               override fun onDataChange(p0: DataSnapshot) {
                   if(p0.exists()){
                       for(e in p0.children){
                          val status = e.getValue(AttendanceIndicator::class.java)
                           if(status?.status == true){
                               attendanceBtn.setAlpha(1.0f)
                               attendanceBtn.isEnabled = true
                           }else{
                               attendanceBtn.setAlpha(.5f)
                               attendanceBtn.isEnabled = false
                           }

                       }
                   }
               }

           }
       )
   }
   private fun dropClass(courseId: String){
           val user = FirebaseAuth.getInstance().currentUser
           val studentRef = FirebaseDatabase.getInstance().getReference("Student")
           studentRef.orderByChild("email").equalTo(user!!.email).addListenerForSingleValueEvent(
               object:ValueEventListener{
                   override fun onCancelled(p0: DatabaseError) {}
                   override fun onDataChange(p0: DataSnapshot) {
                       for(e in p0.children){
                           studentRef.child(e.key!!).child("courseId").addListenerForSingleValueEvent(
                               object : ValueEventListener{
                                   override fun onDataChange(p1: DataSnapshot) {
                                       for(e1 in p1.children) {
                                           if(e1.getValue().toString() == courseId){
                                               studentRef.child(e.key!!).child("courseId").child(e1.key!!).removeValue()
                                           }
                                       }
                                   }

                                   override fun onCancelled(p1: DatabaseError) {}
                               }
                           )
                       }
                   }
               }
           )
           val builder = AlertDialog.Builder(context)
           builder.setTitle("Dropped")
           builder.setMessage("This course is successfully dropped!")
           builder.setIcon(R.drawable.sad)
           builder.setPositiveButton("Ok"){dialog, which ->
               if(findNavController().currentDestination?.id == R.id.attendancePage) {
                   val user = FirebaseAuth.getInstance().currentUser
                   val model = ViewModelProvider(activity!!).get(GeneralCommunicator::class.java)
                   model.setMsgCommunicator(user?.email!!)
                   findNavController().navigate(R.id.action_attendancePage_to_studentHomePage)
               }
           }
           val alert = builder.create()
           alert.show()


   }
   private fun backFun(){

          if(findNavController().currentDestination?.id == R.id.attendancePage) {
              val user = FirebaseAuth.getInstance().currentUser
              val model = ViewModelProvider(activity!!).get(GeneralCommunicator::class.java)
              model.setMsgCommunicator(user?.email!!)
              findNavController().navigate(R.id.action_attendancePage_to_studentHomePage)
          }

   }
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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
            AttendancePage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
