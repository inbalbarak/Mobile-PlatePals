package com.example.platepals

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.platepals.model.Model
import com.example.platepals.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PersonalInfoFragment : Fragment() {

    private var userInfo: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_personal_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = Firebase.auth

        val logout = view.findViewById<Button>(R.id.logoutBtn)
        logout.setOnClickListener {
            auth.signOut()

            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        val backButton: Button = view.findViewById(R.id.backBtn)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        Model.shared.getUserByEmail(auth.currentUser?.email ?: "") { user ->
            userInfo = user

            val rating = if (user?.ratingCount?.toInt() == 0) {
                0
            } else {
                (user?.ratingSum?.toInt() ?: 1) / (user?.ratingCount?.toInt() ?: 1)
            }

            val displayFragment = DisplayUserInfoFragment.newInstance(
                user?.username ?: "",
                rating,
                user?.avatarUrl ?: ""
            )

            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, displayFragment)
                .commit()
        }
    }
}
