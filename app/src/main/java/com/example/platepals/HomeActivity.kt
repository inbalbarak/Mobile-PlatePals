package com.example.platepals
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentTransaction
import com.example.platepals.model.Model
import com.example.platepals.model.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

//interface OnItemClickListener {
//    fun onItemClick(post: Post?)
//}


class HomeActivity : AppCompatActivity() {
    private val selectedTagIds = mutableSetOf<String>()
    private var selectedSort = R.id.topButton
    private val tagViews = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadTags()
        selectSort()
        loadFilteredPosts()

        val topButton: Button = findViewById(R.id.topButton);
        val newButton: Button = findViewById(R.id.newButton);

        topButton.setOnClickListener {
            selectedSort = R.id.topButton
            selectSort()
            loadFilteredPosts()
        }

        newButton.setOnClickListener {
            selectedSort = R.id.newButton
            selectSort()
            loadFilteredPosts()
        }

        val auth = Firebase.auth

        val greeting: TextView = findViewById(R.id.username_text);

        Model.shared.getUserByEmail(auth.currentUser?.email?:"") { user ->

            greeting.text = "Hello, ${user?.username}"

        }
    }

    private fun loadFilteredPosts() {
        Model.shared.getAllPosts { allPosts ->
            runOnUiThread {
                var filteredPosts = allPosts.filter { post ->
                    selectedTagIds.isEmpty() || post.tags.any { tag -> selectedTagIds.contains(tag) }
                }

                filteredPosts = if (selectedSort == R.id.newButton) {
                    filteredPosts.sortedByDescending { it.createdAt }
                } else {
                    filteredPosts.sortedByDescending { it.rating }
                }

                Log.i("yahli", filteredPosts.toString());

                showPostsFragment(filteredPosts)
            }
        }
    }

    private fun showPostsFragment(posts: List<Post>) {
        val fragment = PostsListFragment.newInstance(posts)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    fun onPostItemClick(post: Post?) {
        post?.let {
            val detailsFragment = RecipeDetailsFragment.newInstance(it)

            supportFragmentManager.beginTransaction()
                .replace(R.id.detailsFragmentContainerView, detailsFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit()
        }
    }

    private fun loadTags() {
        val flow = findViewById<androidx.constraintlayout.helper.widget.Flow>(R.id.tagsFlow)
        val mainLayout = findViewById<ConstraintLayout>(R.id.main)

        Model.shared.getAllTags { tags ->
            runOnUiThread {
                val tagIds = mutableListOf<Int>()

                for (tag in tags) {
                    val tagView = createTagView(tag.name, tag.id)
                    mainLayout.addView(tagView)
                    tagIds.add(tagView.id)
                    tagViews.add(tagView)  // Add the tag view to the list
                }

                flow.referencedIds = tagIds.toIntArray()
                selectTags()  // Call selectTags after loading the tags
            }
        }
    }

    private fun selectTags() {
        // Loop through the tag views in the list
        for (tagView in tagViews) {
            val tagId = tagView.tag.toString()  // Assuming you set the tagId as the tag in the view

            // Check if the tag is in the selectedTagIds set
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
        val tagView = LayoutInflater.from(this).inflate(R.layout.item_tag, null) as TextView
        tagView.id = ViewCompat.generateViewId()
        tagView.text = tagName
        tagView.tag = tagId  // Store the tag ID as a tag

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

            loadFilteredPosts()
        }

        return tagView
    }

    private fun selectSort() {
        val topButton: Button = findViewById(R.id.topButton);
        val newButton: Button = findViewById(R.id.newButton);

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