package com.example.wiwa

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.wiwa.databinding.FragmentStudentHomePageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StudentHomePage : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentStudentHomePageBinding
    private lateinit var name : TextView
    private var temp : String?= null
    private lateinit var courseList : RecyclerView
    private var arrayList = ArrayList<Course>()
    private lateinit var btmNav : BottomNavigationView

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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_student_home_page,container,false)
        name = binding.nameOfStudent
        courseList = binding.courseList
        btmNav = binding.bottomNav
        val model = ViewModelProvider(activity!!).get(GeneralCommunicator::class.java)
        model.message.observe(this,object: Observer<Any?> {
            override fun onChanged(t: Any?) {
                temp = t!!.toString()
                var key = ""
                val ref = FirebaseDatabase.getInstance().reference
                val ordersRef = ref.child("Student").orderByChild("email").equalTo(temp)
                val valueEventListener = object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()) {
                            for (ds in p0.children) {
                                val nameTemp =
                                    ds.child("firstName").getValue(String::class.java) + " " + ds.child("lastName").getValue(String::class.java)
                                key = ds.key!!
                                name.text = nameTemp
                            }
                            sendKeyToEnrollment(key)


                            val courseRef = FirebaseDatabase.getInstance().getReference("Course")
                            val databaseReference = FirebaseDatabase.getInstance().getReference("Student")
                            ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {}
                                override fun onDataChange(p0: DataSnapshot) {
                                    for (e in p0.children) {
                                        arrayList.clear()
                                            databaseReference.child(e.key!!).child("courseId").addValueEventListener(
                                                object : ValueEventListener {
                                                    override fun onDataChange(p2: DataSnapshot) {
                                                        for (e2 in p2.children) {
                                                            courseRef.orderByChild("courseId").equalTo(e2.getValue(String::class.java)).addListenerForSingleValueEvent(
                                                                object :ValueEventListener {
                                                                    override fun onCancelled(
                                                                        p3: DatabaseError
                                                                    ) {}

                                                                    override fun onDataChange(
                                                                        p3: DataSnapshot
                                                                    ) {

                                                                        for (e3 in p3.children) {
                                                                            val course = e3.getValue(Course::class.java)
                                                                            arrayList.add(course!!)
                                                                        }
                                                                        val adapter = CourseAdapter(arrayList,"Student")
                                                                        courseList.adapter = adapter
                                                                    }

                                                                })

                                                        }
                                                    }

                                                    override fun onCancelled(p2: DatabaseError) {}
                                                }
                                            )
                                    }
                                }
                                })
                            }


                    }
                    override fun onCancelled(p0: DatabaseError) {
                    }

                }
                ordersRef.addListenerForSingleValueEvent(valueEventListener)
            }
        })

        courseList.addOnItemTouchListener(RecyclerItemClickListener(context!!, courseList, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
            if(findNavController().currentDestination?.id == R.id.studentHomePage) {
                model.setIdCommunicator(CourseAdapter(arrayList,"Student").getID(position))
                model.setNameCommunicator(name.text.toString())
                view.findNavController().navigate(R.id.action_studentHomePage_to_attendancePage)
            } }
            override fun onItemLongClick(view: View?, position: Int) {}
        }))

        val user = FirebaseAuth.getInstance().currentUser
        if(user==null){
            if(findNavController().currentDestination?.id == R.id.studentHomePage) {
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

        activity!!.actionBar?.setDisplayHomeAsUpEnabled(false)
        activity!!.actionBar?.setHomeButtonEnabled(false)

        return binding.root
    }
    private fun sendKeyToEnrollment(str : String){
        btmNav.setOnItemReselectedListener {item->
            when(item.itemId) {
                R.id.add -> {
                    if (findNavController().currentDestination?.id == R.id.studentHomePage) {
                        var bundle: Bundle = bundleOf("key" to str)
                        findNavController().navigate(R.id.action_studentHomePage_to_studentEnroll, bundle)
                    }
                }
                R.id.manageAccount ->{
                    if(findNavController().currentDestination?.id == R.id.studentHomePage){
                        findNavController().navigate(R.id.action_studentHomePage_to_studentAccountManagement2)
                    }
                }
            }
        }




    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu!!, inflater!!)
        inflater?.inflate(R.menu.menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(R.id.mainPage)
            }
        }
        return super.onOptionsItemSelected(item)
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
            StudentHomePage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
