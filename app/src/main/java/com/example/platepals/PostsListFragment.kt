import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.platepals.PostsListViewModel
import com.example.platepals.databinding.FragmentPostsListBinding
import com.example.platepals.model.Model
import com.example.platepals.model.Post
import com.idz.colman24class2.adapter.PostsRecyclerAdapter

interface OnItemClickListener {
    fun onItemClick(post: Post?)
}

class PostsListFragment : Fragment() {

    private var adapter: PostsRecyclerAdapter? = null
    private var binding: FragmentPostsListBinding? = null
    private var viewModel: PostsListViewModel? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this)[PostsListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPostsListBinding.inflate(inflater, container, false)

        binding?.recyclerView?.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        binding?.recyclerView?.layoutManager = layoutManager

        adapter = PostsRecyclerAdapter(viewModel?.posts)

        adapter?.listener = object : OnItemClickListener {
            override fun onItemClick(post: Post?) {
                Log.d("TAG", "On post clicked name: ${post?.title}")
            }
        }

        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        getAllPosts()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun getAllPosts() {

        binding?.progressBar?.visibility = View.VISIBLE

        Model.shared.getAllPosts {
            viewModel?.set(posts = it)
            adapter?.set(it)
            adapter?.notifyDataSetChanged()

            binding?.progressBar?.visibility = View.GONE
        }
    }
}