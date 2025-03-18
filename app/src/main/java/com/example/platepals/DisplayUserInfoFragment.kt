package com.example.platepals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class DisplayUserInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_display_user_info, container, false)

        val username = arguments?.getString(USERNAME)
        val avatarUrl = arguments?.getString(AVATAR_URL)

        val usernameTextView = view.findViewById<TextView>(R.id.usernameText)
        val avatarImageView: ImageView = view.findViewById(R.id.avatarImage)

        usernameTextView.text = username

        avatarUrl?.let {
            if (it.isNotBlank()) {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.empty_user_icon)
                    .into(avatarImageView)
            }
        }

        val editButton: Button = view.findViewById(R.id.editProfile)
        editButton.setOnClickListener {
            val editFragment = EditUserInfoFragment.newInstance(username ?: "", avatarUrl ?: "")

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, editFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    companion object {
        private const val USERNAME = "username"
        private const val AVATAR_URL = "avatarUrl"

        fun newInstance(username: String, avatarUrl: String): DisplayUserInfoFragment {
            val fragment = DisplayUserInfoFragment()
            val args = Bundle()
            args.putString(USERNAME, username)
            args.putString(AVATAR_URL, avatarUrl)
            fragment.arguments = args
            return fragment
        }
    }
}
