package com.example.psmart.ui.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.psmart.R
import com.example.psmart.databinding.FragmentAccountBinding
import com.example.psmart.ui.login.Account
import com.example.psmart.ui.login.User


class AccountFragment : Fragment() {

    private lateinit var accountViewModel: AccountViewModel
    private var _binding: FragmentAccountBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    //a list to store all the products
    private var accountList: MutableList<Account>? = null

    //the recyclerview
    @SuppressLint("StaticFieldLeak")
    var recyclerView: RecyclerView? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        accountViewModel =
            ViewModelProvider(this)[AccountViewModel::class.java]

        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        val view: View = inflater.inflate(R.layout.fragment_account, container, false)

        val schoolName = view?.findViewById<TextView>(R.id.school_name)
            schoolName.text = User.getName()

        val schoolEmail = view?.findViewById<TextView>(R.id.school_email)
            schoolEmail.text = User.getEmail()




        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}