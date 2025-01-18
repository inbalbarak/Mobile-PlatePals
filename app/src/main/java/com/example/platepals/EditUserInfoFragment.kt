package com.example.platepals

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.platepals.model.Model
import com.example.platepals.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class EditUserInfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_user_info, container, false)

        val email = arguments?.getString(EMAIL)
        val avatarUrl = arguments?.getString(AVATAR_URL)

        val updatedEmail = view.findViewById<TextView>(R.id.updateEmailText)

        updatedEmail.text = email
        // TODO: Add functionality for changing user profile image

        val auth = Firebase.auth

        val updateButton: Button = view.findViewById(R.id.updateProfileBtn)
        updateButton.setOnClickListener {

            Model.shared.getUserByEmail(email ?: "") { user ->
                Model.shared.upsertUser(User(updatedEmail.text.toString(), user?.password ?: ""), email) { success ->
                    if (success) {
                        auth.createUserWithEmailAndPassword(updatedEmail.text.toString(), user?.password ?: "")
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val rating = if(user?.ratingCount?.toInt() == 0)  0 else (user?.ratingSum?.toInt() ?: 1) / (user?.ratingCount?.toInt() ?: 1)

                                    val displayFragment = DisplayUserInfoFragment.newInstance(
                                        updatedEmail.text.toString(),
                                        rating,
                                        avatarUrl ?: ""
                                    )
                                    (activity as? PersonalInfoActivity)?.showFragment(displayFragment)  // Use activity method to switch fragments
                                }
                            }

                    } else {
                        Toast.makeText(requireContext(), "Failed to update user info", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

        return view
    }

    companion object {
        private const val EMAIL = "email"
        private const val AVATAR_URL = "avatarUrl"

        fun newInstance(email: String, avatarUrl: String): EditUserInfoFragment {
            val fragment = EditUserInfoFragment()
            val args = Bundle()
            args.putString(EMAIL, email)
            args.putString(AVATAR_URL, avatarUrl)
            fragment.arguments = args
            return fragment
        }
    }
}
