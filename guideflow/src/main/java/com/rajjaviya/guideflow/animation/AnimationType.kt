package com.rajjaviya.guideflow.animation

/**
 * Entry / exit animation applied to the overlay and tooltip when a step transitions.
 */
enum class AnimationType {
    /** Alpha fade in / out (default). */
    FADE,

    /** Slide in from the bottom. */
    SLIDE_UP,

    /** Slide in from the top. */
    SLIDE_DOWN,

    /** Scale + bounce overshoot on entry. */
    BOUNCE,

    /** No animation — instant transition. */
    NONE,
}
