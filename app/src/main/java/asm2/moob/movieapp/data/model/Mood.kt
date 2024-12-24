package asm2.moob.movieapp.data.model

enum class Mood(val title: String, val genreIds: List<Int>) {
    HAPPY("Happy", listOf(35, 16)), // Comedy, Animation
    SAD("Need Cheering Up", listOf(35, 10751)), // Comedy, Family
    EXCITED("Thrilled", listOf(28, 12)), // Action, Adventure
    RELAXED("Calm & Peaceful", listOf(99, 18)), // Documentary, Drama
    ROMANTIC("Romantic", listOf(10749)), // Romance
    THOUGHTFUL("Deep & Meaningful", listOf(18, 36)) // Drama, History
} 