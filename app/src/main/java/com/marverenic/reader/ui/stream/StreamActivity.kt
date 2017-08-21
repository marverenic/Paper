package com.marverenic.reader.ui.stream

import android.content.Context
import android.content.Intent
import com.marverenic.reader.ui.SingleFragmentActivity

private const val EXTRA_STREAM_NAME = "stream_name"
private const val EXTRA_STREAM_ID = "stream_id"

class StreamActivity : SingleFragmentActivity() {

    companion object {
        fun newIntent(context: Context, streamName: String, streamId: String) =
                Intent(context, StreamActivity::class.java).apply {
                    putExtra(EXTRA_STREAM_NAME, streamName)
                    putExtra(EXTRA_STREAM_ID, streamId)
                }
    }

    override fun onCreateFragment() = StreamFragment.newInstance(
            intent.getStringExtra(EXTRA_STREAM_NAME),
            intent.getStringExtra(EXTRA_STREAM_ID))

}