package com.example.platepals

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.platepals.model.Model
import com.example.platepals.model.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {
    private val selectedTagIds = mutableSetOf<String>()
    private var selectedSort = R.id.topButton
    private val tagViews = mutableListOf<TextView>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadTags(view)
        selectSort(view)
        observePosts()

        val topButton: Button = view.findViewById(R.id.topButton)
        val newButton: Button = view.findViewById(R.id.newButton)

        topButton.setOnClickListener {
            selectedSort = R.id.topButton
            selectSort(view)
            filterAndShowPosts(Model.shared.posts.value ?: listOf())
        }

        newButton.setOnClickListener {
            selectedSort = R.id.newButton
            selectSort(view)
            filterAndShowPosts(Model.shared.posts.value ?: listOf())
        }

        val auth = Firebase.auth
        val greeting: TextView = view.findViewById(R.id.username_text)
        val profileImage: ImageView = view.findViewById(R.id.profile_image)

        Model.shared.getUserByEmail(auth.currentUser?.email ?: "") { user ->
            activity?.runOnUiThread {
                greeting.text = "Hello, ${user?.username}"

                user?.avatarUrl?.let {
                    if (it.isNotBlank()) {
                        Picasso.get()
                            .load(it)
                            .placeholder(R.drawable.empty_user_icon)
                            .into(profileImage)
                    }
                }
            }
        }
    }

    private fun observePosts() {
        Model.shared.posts.observe(viewLifecycleOwner, Observer { posts ->
            filterAndShowPosts(posts)
        })
    }

    private fun filterAndShowPosts(allPosts: List<Post>) {
        var filteredPosts = allPosts.filter { post ->
            selectedTagIds.isEmpty() || post.tags?.any { tag -> selectedTagIds.contains(tag) } == true
        }

        filteredPosts = if (selectedSort == R.id.newButton) {
            filteredPosts.sortedByDescending { it.createdAt }
        } else {
            filteredPosts.sortedByDescending { it.rating }
        }

        showPostsFragment(filteredPosts)
    }

    private fun showPostsFragment(posts: List<Post>) {
        val fragment = PostsListFragment.newInstance(posts, false)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    private fun loadTags(view: View) {
        val flow = view.findViewById<androidx.constraintlayout.helper.widget.Flow>(R.id.tagsFlow)
        val mainLayout = view.findViewById<ConstraintLayout>(R.id.main)

        Model.shared.getAllTags { tags ->
            activity?.runOnUiThread {
                val tagIds = mutableListOf<Int>()

                for (tag in tags) {
                    val tagView = createTagView(tag.name, tag.id)
                    mainLayout.addView(tagView)
                    tagIds.add(tagView.id)
                    tagViews.add(tagView)
                }

                flow.referencedIds = tagIds.toIntArray()
                selectTags()
            }
        }
    }

    private fun selectTags() {
        for (tagView in tagViews) {
            val tagId = tagView.tag.toString()
            if (selectedTagIds.contains(tagId)) {
                tagView.setBackgroundResource(R.drawable.orange_filled_rounded_text_field)
                tagView.setTextColor(Color.WHITE)
            } else {
                tagView.setBackgroundResource(R.drawable.orange_rounded_text_field)
                tagView.setTextColor(Color.parseColor("#FF9B05"))
            }
        }
    }

    private fun createTagView(tagName: String, tagId: String): TextView {
        val tagView = layoutInflater.inflate(R.layout.item_tag, null) as TextView
        tagView.id = ViewCompat.generateViewId()
        tagView.text = tagName
        tagView.tag = tagId

        tagView.setOnClickListener {
            if (selectedTagIds.contains(tagId)) {
                selectedTagIds.remove(tagId)
                tagView.setBackgroundResource(R.drawable.orange_rounded_text_field)
                tagView.setTextColor(Color.parseColor("#FF9B05"))
            } else {
                selectedTagIds.add(tagId)
                tagView.setBackgroundResource(R.drawable.orange_filled_rounded_text_field)
                tagView.setTextColor(Color.WHITE)
            }
            filterAndShowPosts(Model.shared.posts.value ?: listOf())
        }
        return tagView
    }

    private fun selectSort(view: View) {
        val topButton: Button = view.findViewById(R.id.topButton)
        val newButton: Button = view.findViewById(R.id.newButton)

        when (selectedSort) {
            R.id.topButton -> {
                topButton.setBackgroundResource(R.drawable.orange_black_border_button)
                newButton.setBackgroundResource(R.drawable.white_black_border_button)
            }
            R.id.newButton -> {
                newButton.setBackgroundResource(R.drawable.orange_black_border_button)
                topButton.setBackgroundResource(R.drawable.white_black_border_button)
            }
        }
    }
}