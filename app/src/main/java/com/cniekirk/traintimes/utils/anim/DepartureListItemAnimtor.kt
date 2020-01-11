package com.cniekirk.traintimes.utils.anim

import android.view.View

class DepartureListItemAnimtor(itemViewElevation: Int): SlideAlphaAnimator<DepartureListItemAnimtor>(itemViewElevation) {


    override fun getAnimationTranslationY(itemView: View): Float {

        return dpToPx(32f, itemView.context).toFloat()

    }

    override fun getAnimationTranslationX(itemView: View?): Float {

        return 0f

    }

}