package com.example.wiwa

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.wiwa.databinding.FragmentTeacherHomePageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TeacherHomePage : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentTeacherHomePageBinding
    private lateinit var name : TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference : DatabaseReference
    private var temp : String?=null
    var arrayList = ArrayList<Course>()
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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_teacher_home_page, container, false)
        btnNav = binding.btnnavteacher
        name = binding.nameOfTeacher
        recyclerView = binding.recyclerView
        val model = ViewModelProvider(activity!!).get(GeneralCommunicator::class.java)
        model.message.observe(this,object: Observer<Any?> {
            override fun onChanged(t: Any?) {
                temp = t!!.toString()

                val ordersRef = FirebaseDatabase.getInstance().getReference("Teacher").orderByChild("email").equalTo(temp)
                val valueEventListener = object : ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot) {
                        for(ds in p0.children){

                            val firstName = ds.child("firstName").getValue(String::class.java)
                            val lastName = ds.child("lastName").getValue(String::class.java)
                            val fullName = firstName + " " + lastName
                            name.text = fullName
                        }
                        databaseReference = FirebaseDatabase.getInstance().getReference("Course")
                        databaseReference.orderByChild("professorName").equalTo(name.text.toString()).addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {}

                            override fun onDataChange(p0: DataSnapshot) {
                                if(p0.exists()){
                                    arrayList.clear()
                                    for(e in p0.children){
                                        val course = e.getValue(Course::class.java)
                                        arrayList.add(course!!)
                                }
                                }
                                val adapter = CourseAdapter(arrayList,"Teacher")
                                recyclerView.adapter = adapter

                                }
                        })
                    }
                    override fun onCancelled(p0: DatabaseError) {}
                }
                ordersRef.addListenerForSingleValueEvent(valueEventListener)



                binding.recyclerView.addOnItemTouchListener(RecyclerItemClickListener(context!!, binding.recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        if(findNavController().currentDestination?.id == R.id.teacherHomePage) {
                            model.setMsgCommunicator(name.text.toString())
                            // here we pass the id of the course to the manage class
                            model.setIdCommunicator(CourseAdapter(arrayList,"Teacher").getID(position))
                            var bundle: Bundle = bundleOf("courseId" to CourseAdapter(arrayList,"Teacher").getID(position))
                            view.findNavController().navigate(R.id.action_teacherHomePage_to_manageClasses3, bundle)
                        }
                    }

                    override fun onItemLongClick(view: View?, position: Int) {}
                }))
            }
        })


        btnNav.setOnItemReselectedListener { item->
            when(item.itemId){
                R.id.manageclass ->{
                    if(findNavController().currentDestination?.id == R.id.teacherHomePage) {
                        model.setMsgCommunicator(name.text.toString())
                        model.setIdCommunicator("-1.0")
                        findNavController().navigate(R.id.action_teacherHomePage_to_manageClasses3)
                    }
                }
                R.id.manageAccount->{
                    if(findNavController().currentDestination?.id == R.id.teacherHomePage){
                        findNavController().navigate(R.id.action_teacherHomePage_to_teacherAccountManagement)
                    }
                }
            }
        }
        val user = FirebaseAuth.getInstance().currentUser
        if(user==null){
            if(findNavController().currentDestination?.id == R.id.teacherHomePage) {
                findNavController().navigate(R.id.mainPage)
            }
        }

        val text = activity!!.findViewById<TextView>(R.id.textView20)
        val au = FirebaseAuth.getInstance().currentUser
        text.text = au!!.email

        val navigationView = activity!!.findViewById<NavigationView>(R.id.navView)
        val drawer = activity!!.findViewById<DrawerLayout>(R.id.drawerLayout)
        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.logout -> {
                    FirebaseAuth.getInstance().signOut()
                    text.text = "Welcome User"
                    findNavController().navigate(R.id.mainPage)
                    drawer.closeDrawers()

                }
                R.id.about ->{
                    val i = Intent(activity, AboutActivity::class.java)
                    drawer.closeDrawers()
                    startActivity(i)
                }

            }
            false
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
            TeacherHomePage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}

