package com.example.platepals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class DisplayUserInfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_display_user_info, container, false)

        val username = arguments?.getString(USERNAME)
        val rating = arguments?.getInt(RATING)
        val avatarUrl = arguments?.getString(AVATAR_URL)

        val ratingTextView = view.findViewById<TextView>(R.id.ratingText)
        val usernameTextView = view.findViewById<TextView>(R.id.usernameText)
        val avatar: ImageView = view.findViewById(R.id.avatarImage)

        ratingTextView.text = rating?.toString() ?: "0"
        usernameTextView.text = username

        // TODO: Set avatar image

        val editButton: Button = view.findViewById(R.id.editProfile)
        editButton.setOnClickListener {
            val editFragment = EditUserInfoFragment.newInstance(username ?: "", avatarUrl ?: "")
            (activity as? PersonalInfoActivity)?.showFragment(editFragment)
        }

        return view
    }

    companion object {
        private const val USERNAME = "username"
        private const val RATING = "rating"
        private const val AVATAR_URL = "avatarUrl"

        fun newInstance(username: String, rating: Int, avatarUrl: String): DisplayUserInfoFragment {
            val fragment = DisplayUserInfoFragment()
            val args = Bundle()
            args.putString(USERNAME, username)
            args.putInt(RATING, rating)
            args.putString(AVATAR_URL, avatarUrl)
            fragment.arguments = args
            return fragment
        }
    }
}
