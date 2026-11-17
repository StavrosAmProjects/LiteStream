package com.ipvio.stream

object CountryBrowseOrderLogic {
    fun apply(codes: List<String>, saved: List<String>): List<String> {
        val available = codes.toSet()
        return saved.filter { it in available }.distinct() + codes.filter { it !in saved }
    }

    fun move(codes: List<String>, saved: List<String>, code: String, delta: Int): List<String> {
        val order = apply(codes, saved).toMutableList()
        val from = order.indexOf(code)
        val to = from + delta
        if (from < 0 || to !in order.indices) return order
        order.add(to, order.removeAt(from))
        return order + saved.filter { it !in codes && it !in order }
    }
}
