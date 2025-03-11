package com.example.platepals

import ChatAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.platepals.databinding.FragmentChatbotBinding
import com.example.platepals.model.Message
import com.example.platepals.model.Model
import com.example.platepals.networking.ChatGptRequest

class ChatbotFragment : Fragment() {
    private lateinit var chatAdapter: ChatAdapter
    private var binding: FragmentChatbotBinding? = null
    private val messageHistory = mutableListOf<Message>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatbotBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupMessageInput()

        messageHistory.add(Message("You are an assistant for recipes, meal ideas, cooking techniques, and ingredients. Only respond with related information, and in plain text with no special formatting, under 300 words", "system"))
        addBotMessage("Hi there! What recipe can I help you create today?")
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding?.chatRecyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
    }

    private fun setupMessageInput() {
        binding?.messageInputLayout?.setEndIconOnClickListener {
            val message = binding?.messageInput?.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                binding?.messageInput?.text?.clear()
            }
        }
    }

    private fun sendMessage(text: String) {
        val userMessage = Message(text, "user")
        messageHistory.add(userMessage)
        chatAdapter.addMessage(userMessage)
        binding?.chatRecyclerView?.scrollToPosition(chatAdapter.itemCount - 1)

        fetchChatGptResponse()
    }

    private fun fetchChatGptResponse() {
        val request = ChatGptRequest(
            messages = messageHistory
        )

        Model.shared.fetchChatGptResponse(request) { response ->
            activity?.runOnUiThread {
                if(response != null) {
                    addBotMessage(response)
                }
            }
        }
    }

    private fun addBotMessage(text: String) {
        val botMessage = Message(text, "assistant")
        messageHistory.add(botMessage)
        chatAdapter.addMessage(botMessage)
        binding?.chatRecyclerView?.scrollToPosition(chatAdapter.itemCount - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}