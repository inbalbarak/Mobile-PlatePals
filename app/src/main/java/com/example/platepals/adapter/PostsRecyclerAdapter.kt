import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.platepals.R
import com.example.platepals.databinding.PostListRowBinding
import com.example.platepals.model.Post

class PostsRecyclerAdapter(
    private var posts: List<Post>,
    private val editable: Boolean,
    private val onDelete: (postId: String) -> Unit // Delete function callback
) : RecyclerView.Adapter<PostsRecyclerAdapter.PostViewHolder>() {

    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post, editable, onDelete)
    }

    fun updatePosts(newPosts: List<Post>) {
        this.posts = newPosts
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(private val binding: PostListRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, editable: Boolean, onDelete: (postId: String) -> Unit) {
            binding.postRowTitle.text = post.title
            binding.postRowRating.text = post.rating.toString()
            binding.postRowAuthor.text = post.author

            // Show/hide buttons based on "editable"
            binding.editBtn.visibility = if (editable) View.VISIBLE else View.GONE
            binding.deleteBtn.visibility = if (editable) View.VISIBLE else View.GONE

            binding.editBtn.setOnClickListener { view ->
                val navController = view.findNavController()
                val bundle = Bundle()
                bundle.putString("postId", post.id)
                navController.navigate(R.id.action_myRecipesFragment_to_editPostFragment, bundle)
            }

            // Click to delete post
            binding.deleteBtn.setOnClickListener {
                onDelete(post.id)
                posts = posts?.filter { existPost -> existPost.id != post.id } ?: emptyList()
            }

            binding.root.setOnClickListener {
                listener?.onItemClick(post)
            }
        }
    }
}
