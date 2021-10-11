package com.mahdikh.vision.arrowpanel.touchanimator

abstract class RotationTouchAnimator : PivotTouchAnimator {
    var factor: Float = 30.0F

    constructor() : super()

    constructor(factor: Float) : super() {
        this.factor = factor
    }
}