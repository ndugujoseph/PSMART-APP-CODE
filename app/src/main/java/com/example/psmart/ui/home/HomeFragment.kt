package com.example.psmart.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.psmart.FingerPrintRegisterActivity
import com.example.psmart.R
import com.example.psmart.RegisterWelcome
import com.example.psmart.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {



    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null


//    val register = findViewById<TextView>(R.id.register_attendance)
//    register.setOnClickListener {
//        val intent = Intent(this, ForgotPasswordActivity::class.java);
//        startActivity(intent)
//    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        val report = view.findViewById<Button>(R.id.reports)
        report.setOnClickListener {
        }


      //registering attendance
            val scan = view.findViewById<Button>(R.id.register_attendance)

            scan.setOnClickListener {
                val intent = Intent(activity, FingerPrintRegisterActivity::class.java)
                startActivity(intent)
            }

      //registering new records
        val registerNew = view.findViewById<Button>(R.id.register_new)
        registerNew.setOnClickListener {
            val intent = Intent(activity, RegisterWelcome::class.java)
            startActivity(intent)
        }


        // Return the fragment view/layout
          return view


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}