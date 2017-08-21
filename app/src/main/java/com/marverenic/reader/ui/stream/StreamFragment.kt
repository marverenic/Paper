package com.marverenic.reader.ui.stream

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marverenic.reader.ui.ToolbarFragment

private const val ARG_STREAM_NAME = "stream_name"
private const val ARG_STREAM_ID = "stream_id"

class StreamFragment : ToolbarFragment() {

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
        TODO("not implemented")
    }

}