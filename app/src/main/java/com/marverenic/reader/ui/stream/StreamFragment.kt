package com.marverenic.reader.ui.stream

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marverenic.reader.R
import com.marverenic.reader.databinding.FragmentStreamBinding
import com.marverenic.reader.ui.ToolbarFragment

private const val ARG_STREAM_NAME = "stream_name"
private const val ARG_STREAM_ID = "stream_id"

class StreamFragment : ToolbarFragment() {

    private lateinit var binding: FragmentStreamBinding

    override val title: String
        get() = arguments.getString(ARG_STREAM_NAME).orEmpty()

    companion object {
        @SuppressLint("NewApi")
        fun newInstance(streamName: String, streamId: String) = StreamFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_STREAM_NAME, streamName)
                putString(ARG_STREAM_ID, streamId)
            }
        }
    }

    override fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stream, container, false)
        binding.viewModel = StreamViewModel(context)

        return binding.root
    }

}