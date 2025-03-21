package com.example.platepals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.platepals.model.Model
import com.example.platepals.model.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class UploadedRecipesFragment : Fragment() {
    private var userUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_uploaded_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserInfo()
        observePosts()
    }

    private fun loadUserInfo() {
        val auth = Firebase.auth
        val email = auth.currentUser?.email ?: ""

        Model.shared.getUserByEmail(email) { user ->
            userUsername = user?.username
            Model.shared.posts.value?.let { posts ->
                formatPosts(posts)
            }
        }
    }

    private fun observePosts() {
        Model.shared.posts.observe(viewLifecycleOwner, Observer { posts ->
            formatPosts(posts)
        })
    }

    private fun formatPosts(posts: List<Post>){
        Model.shared.getAllUsers({users->
            val formattedPosts = posts.map{post->
                val user = users.find { it?.email == post.author }
                post.copy(author = user?.username ?: post.author)
            }

            filterAndShowUserPosts(formattedPosts)
        })
    }

    private fun filterAndShowUserPosts(allPosts: List<Post>) {
            val filteredPosts = allPosts.filter { post ->
                post.author == userUsername
            }
            showPostsFragment(filteredPosts)
    }

    private fun showPostsFragment(posts: List<Post>) {
        val fragment = PostsListFragment.newInstance(posts, true)
        parentFragmentManager.beginTransaction()
            .replace(R.id.recipesFragmentContainerView, fragment)
            .commit()
    }
}