package com.example.platepals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.platepals.model.Model
import com.example.platepals.model.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class UploadedRecipesFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadUserPosts()
        return inflater.inflate(R.layout.fragment_uploaded_recipes, container, false)
    }

    private fun loadUserPosts() {
        val auth = Firebase.auth
        val email = auth.currentUser?.email ?: ""

        Model.shared.getUserByEmail(email){user->
            Model.shared.getAllPosts { allPosts ->
                activity?.runOnUiThread {
                    var filteredPosts = allPosts.filter { post ->
                        post.author == user?.username
                    }

                    showPostsFragment(filteredPosts)
                }
            }
        }


    }

    private fun showPostsFragment(posts: List<Post>) {
        val fragment = PostsListFragment.newInstance(posts, true)
        parentFragmentManager.beginTransaction()
            .replace(R.id.recipesFragmentContainerView, fragment)
            .commit()
    }
}