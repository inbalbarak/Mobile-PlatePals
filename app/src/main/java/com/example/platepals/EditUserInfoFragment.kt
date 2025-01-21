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


        val username = arguments?.getString(USERNAME)
        val avatarUrl = arguments?.getString(AVATAR_URL)

        val updatedUsername = view.findViewById<TextView>(R.id.updateUsernameText)

        updatedUsername.text = username
        // TODO: Add functionality for changing user profile image

        val auth = Firebase.auth

        val email = auth.currentUser?.email ?: ""

        val updateButton: Button = view.findViewById(R.id.updateProfileBtn)
        updateButton.setOnClickListener {

            Model.shared.getUserByEmail(email) { user ->
                Model.shared.upsertUser(User(email, user?.password ?: "",updatedUsername.text.toString())) { success ->
                    if (success) {
                        val rating = if(user?.ratingCount?.toInt() == 0)  0 else (user?.ratingSum?.toInt() ?: 1) / (user?.ratingCount?.toInt() ?: 1)

                        val displayFragment = DisplayUserInfoFragment.newInstance(
                            updatedUsername.text.toString(),
                            rating,
                            avatarUrl ?: ""
                        )
                        (activity as? PersonalInfoActivity)?.showFragment(displayFragment)

                    } else {
                        Toast.makeText(requireContext(), "Failed to update user info", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

        return view
    }

    companion object {
        private const val USERNAME = "username"
        private const val AVATAR_URL = "avatarUrl"

        fun newInstance(username: String, avatarUrl: String): EditUserInfoFragment {
            val fragment = EditUserInfoFragment()
            val args = Bundle()
            args.putString(USERNAME, username)
            args.putString(AVATAR_URL, avatarUrl)
            fragment.arguments = args
            return fragment
        }
    }
}
