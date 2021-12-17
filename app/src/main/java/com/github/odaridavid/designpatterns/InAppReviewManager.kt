package com.github.odaridavid.designpatterns

import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.android.play.core.tasks.RuntimeExecutionException

interface ReviewManager {
    fun promptForReview(context: Context): Any?

    fun instance(context: Context): Any
}

internal class InAppReviewManager : ReviewManager {

    private var reviewInfo: ReviewInfo? = null

    override fun promptForReview(context: Context): ReviewInfo? {
        if (reviewInfo == null) {
            val reviewManager =
                instance(context = context)
            val reviewRequest = reviewManager.requestReviewFlow()
            reviewRequest.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reviewInfo = task.result
                } else {
                    @ReviewErrorCode val reviewErrorCode =
                        (task.exception as RuntimeExecutionException).errorCode
                    // Log errors
                    Log.e("ReviewError", "$reviewErrorCode")
                }
            }
        }
        return reviewInfo
    }

    override fun instance(context: Context): com.google.android.play.core.review.ReviewManager = ReviewManagerFactory.create(context)

}
