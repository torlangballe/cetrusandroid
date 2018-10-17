package com.github.torlangballe.cetrusandroid

enum class ZTransitionType(val rawValue: Int) {
    none(0), fromLeft(1), fromRight(2), fromTop(3), fromBottom(4), fade(5), reverse(6);
}

data class Attributes(
        var duration: Double = 0.0,
        var transition: ZTransitionType = ZTransitionType.none,
        var oldTransition: ZTransitionType = ZTransitionType.none,
        var lightContent: Boolean = true,
        var useableArea: Boolean = true,
        var portraitOnly:Boolean = false,
        var view: ZView? = null
) {}

var stack = mutableListOf<Attributes>()

fun ZPresentView(view: ZView, duration: Double = 0.5, transition: ZTransitionType = ZTransitionType.none, fadeToo: Boolean = false, oldTransition: ZTransitionType = ZTransitionType.reverse, makeFull: Boolean = true, useableArea: Boolean = false, deleteOld: Boolean = false, lightContent: Boolean = true, portraitOnly: Boolean? = null, done: (() -> Unit)? = null) {
    if (view is ZContainerView) {
        val a = Attributes()
        a.duration = duration
        a.transition = transition
        a.oldTransition = oldTransition
        a.lightContent = lightContent
        a.useableArea = !makeFull
        a.portraitOnly = portraitOnly ?: false
        a.view = view
        stack.append(a)
        view.SetAsFullView(useableArea = false)
        view.ArrangeChildren()
        zMainActivity!!.setContentView(view.View())
    }
}

fun ZPopTopView(namedView: String = "", animated: Boolean = true, overrideDuration: Float = -1f, overrideTransition: ZTransitionType = ZTransitionType.none, done: (() -> Unit)? = null) {
    var p = stack.popLast()
    if (p != null) {
        if (p.view != null) {
        }
    }
    p = stack.last()
    if (p != null) {
        zMainActivity!!.setContentView(p.view!!.View())
    }
}


