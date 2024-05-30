package com.example.wiwa

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.wiwa.databinding.FragmentSeeAttendanceResultBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SeeAttendanceResult : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentSeeAttendanceResultBinding
    private lateinit var attendedStudentList : RecyclerView
    private var arrayList = ArrayList<Student>()
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
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_see_attendance_result,container,false)
        attendedStudentList = binding.attendanceListView
        btnNav = binding.nav2
        val model = ViewModelProvider(activity!!).get(GeneralCommunicator::class.java)

        model.id.observe(this, object : Observer<Any?>{
            override fun onChanged(t: Any?) {
                val courseId = t.toString()!!
                val databaseReference = FirebaseDatabase.getInstance().getReference("Student")
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {

                        for (e in p0.children) {

                            databaseReference.child(e.key!!).child("courseId")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(p1: DataSnapshot) {
                                        arrayList.clear()
                                        for (e1 in p1.children) {
                                            val query = databaseReference.orderByChild("courseId/" + e1.key).equalTo(courseId)
                                            query.addListenerForSingleValueEvent(
                                                object : ValueEventListener {
                                                    override fun onDataChange(p2: DataSnapshot) {
                                                        if (p2.exists()) {
                                                            for (e2 in p2.children) {
                                                                val student = e2.getValue(Student::class.java)
                                                                arrayList.add(student!!)

                                                            }
                                                            val adapter = ResultAdapter(arrayList,courseId)
                                                            attendedStudentList.adapter = adapter
                                                        }
                                                    }

                                                    override fun onCancelled(p2: DatabaseError) {
                                                    }
                                                }
                                            )

                                        }
                                    }

                                    override fun onCancelled(p0: DatabaseError) {
                                    }
                                })

                            btnNav.setOnItemReselectedListener { item->
                                when(item.itemId){
                                    R.id.backHome4->{
                                        if(findNavController().currentDestination?.id == R.id.seeAttendanceResult) {
                                            val user = FirebaseAuth.getInstance().currentUser
                                            model.setMsgCommunicator(user?.email!!)
                                            findNavController().navigate(R.id.teacherHomePage)
                                        }
                                    }
                                    R.id.share->{
                                        shareFun(courseId!!)
                                    }
                                }
                            }

                        }
                    }
                })
            }})
        return binding.root
    }

  fun findAbsences(courseId: String, courseName: String){
      val list = ArrayList<String>()
      val list2 = ArrayList<String>()
      val databaseReference = FirebaseDatabase.getInstance().getReference("Student")
      databaseReference.addValueEventListener(object : ValueEventListener {
          override fun onCancelled(p0: DatabaseError) {}
          override fun onDataChange(p0: DataSnapshot) {

              for (e in p0.children) {
                  databaseReference.child(e.key!!).child("courseId")
                      .addListenerForSingleValueEvent(object : ValueEventListener {
                          override fun onDataChange(p1: DataSnapshot) {
                              arrayList.clear()
                              for (e1 in p1.children) {
                                  val query = databaseReference.orderByChild("courseId/" + e1.key).equalTo(courseId)
                                  query.addListenerForSingleValueEvent(
                                      object : ValueEventListener {
                                          override fun onDataChange(p2: DataSnapshot) {
                                              if (p2.exists()) {
                                                  for (e2 in p2.children) {
                                                      val student = e2.getValue(Student::class.java)
                                                      list.add("Name: "+student?.firstName + " " + student?.lastName + "\n CIN: " + student?.id)
                                                      val attendanceRef = FirebaseDatabase.getInstance().getReference("AttendanceResult")
                                                      attendanceRef.orderByChild("courseId").equalTo(courseId).addValueEventListener(
                                                          object : ValueEventListener{
                                                              override fun onCancelled(p3: DatabaseError) {}
                                                              override fun onDataChange(p3: DataSnapshot) {
                                                                  if(p3.exists()){
                                                                      for(e3 in p3.children) {
                                                                          val attendanted = e3.getValue(AttendanceResult::class.java)
                                                                          list2.add("Name: "+attendanted!!.name + "\n CIN: " + student?.id)
                                                                      }

                                                                  }
                                                                  list.removeAll(list2)
                                                                  val current = Date()
                                                                  val formatter = SimpleDateFormat("yyyy-MM-dd")
                                                                  val final = formatter.format(current)
                                                                  val user = FirebaseAuth.getInstance().currentUser
                                                                  val mIntent = Intent(Intent.ACTION_SEND)
                                                                  mIntent.data = Uri.parse("mailto:")
                                                                  mIntent.type = "text/plain"
                                                                  mIntent.putExtra(Intent.EXTRA_EMAIL,arrayOf<String>(user?.email!!))
                                                                  mIntent.putExtra(Intent.EXTRA_SUBJECT,"Absent Student for " + courseName + " on " + final.toString())

                                                                  val sb = StringBuilder()
                                                                  for (s in list) {
                                                                      sb.append(s)
                                                                      sb.append("\n")
                                                                  }
                                                                      mIntent.putExtra(Intent.EXTRA_TEXT,sb.toString())

                                                                  try {
                                                                      startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
                                                                  }
                                                                  catch(_: Exception){

                                                                  }
                                                              }
                                                          })

                                                  }

                                              }
                                          }

                                          override fun onCancelled(p2: DatabaseError) {
                                          }
                                      }
                                  )

                              }
                          }

                          override fun onCancelled(p0: DatabaseError) {
                          }


                      })

              }
          }


      })






  }
   fun shareFun(courseId : String){
           val attendanceRef = FirebaseDatabase.getInstance().getReference("AttendanceResult")
           attendanceRef.orderByChild("courseId").equalTo(courseId).addValueEventListener(
               object:ValueEventListener{
                   override fun onCancelled(p0: DatabaseError) {}
                   override fun onDataChange(p0: DataSnapshot) {
                       for(e in p0.children){
                           val courseRef = FirebaseDatabase.getInstance().getReference("Course")
                           courseRef.orderByChild("courseId").equalTo(courseId).addValueEventListener(
                               object: ValueEventListener{
                                   override fun onCancelled(p1: DatabaseError) {}
                                   override fun onDataChange(p1: DataSnapshot) {

                                       for(e1 in p1.children){
                                           val course = e1.getValue(Course::class.java)!!
                                           findAbsences(courseId,course.courseName)
                                       }

                                   }
                               }
                           )


                       }
                   }
               }
           )

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
            SeeAttendanceResult().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
