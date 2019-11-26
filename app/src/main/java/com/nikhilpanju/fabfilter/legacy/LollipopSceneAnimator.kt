//package com.nikhilpanju.fabfilter.utils
//
//import android.content.Context
//import androidx.annotation.LayoutRes
//import androidx.annotation.TransitionRes
//import android.transition.Scene
//import android.transition.Transition
//import android.transition.TransitionInflater
//import android.transition.TransitionManager
//import android.view.View
//import android.view.ViewGroup
//
//import com.nikhilpanju.fabfilter.R
//
//internal class LollipopSceneAnimator private constructor(private val transitionManager: TransitionManager) {
//    private var scene1: Scene? = null
//    private var scene2: Scene? = null
//
//    private fun sceneTransition(from: Scene) {
//        if (from == scene1) {
//            transitionManager.transitionTo(scene2)
//        } else {
//            transitionManager.transitionTo(scene1)
//        }
//    }
//
//    private class EnterAction constructor(private val sceneAnimator: LollipopSceneAnimator, private val scene: Scene) : Runnable, View.OnClickListener {
//
//        override fun run() {
//            val sceneRoot = scene.sceneRoot
//            val view:View  = sceneRoot.findViewById(R.id.fab)
//            view.setOnClickListener(this)
//        }
//
//        override fun onClick(v: View) {
//            sceneAnimator.sceneTransition(scene)
//        }
//    }
//
//    companion object {
//
//        fun newInstance(context: Context, container: ViewGroup,
//                        @LayoutRes layout1Id: Int, @LayoutRes layout2Id: Int, @TransitionRes transitionId: Int): LollipopSceneAnimator {
//            val transitionManager = TransitionManager()
//            val sceneAnimator = LollipopSceneAnimator(transitionManager)
//            val scene1 = createScene(sceneAnimator, context, container, layout1Id)
//            val scene2 = createScene(sceneAnimator, context, container, layout2Id)
//            val transition = TransitionInflater.from(context).inflateTransition(transitionId)
//            transitionManager.setTransition(scene1, scene2, transition)
//            transitionManager.setTransition(scene2, scene1, transition)
//            transitionManager.transitionTo(scene1)
//            sceneAnimator.scene1 = scene1
//            sceneAnimator.scene2 = scene2
//            return sceneAnimator
//        }
//
//        private fun createScene(sceneAnimator: LollipopSceneAnimator, context: Context,
//                                container: ViewGroup, @LayoutRes layoutId: Int): Scene {
//            val scene = Scene.getSceneForLayout(container, layoutId, context)
//            scene.setEnterAction(EnterAction(sceneAnimator, scene))
//            return scene
//        }
//    }
//}