package com.ipvio.stream

/** Shared manual moves for the merged Live TV catalogue, replayed over the current provider order. */
object LiveChannelOrderLogic {
    data class Move(val key: String, val delta: Int)

    fun key(item: Item): String = "${item.src}|${item.id}"

    fun apply(items: List<Item>, moves: List<Move>): List<Item> {
        if (items.size < 2 || moves.isEmpty()) return items
        val ordered = items.toMutableList()
        val positions = ordered.withIndex().associate { key(it.value) to it.index }.toMutableMap()
        moves.forEach { move ->
            val from = positions[move.key] ?: return@forEach
            val to = (from + move.delta.coerceIn(-1, 1)).coerceIn(0, ordered.lastIndex)
            if (from == to) return@forEach
            val other = ordered[to]
            ordered[to] = ordered[from]
            ordered[from] = other
            positions[move.key] = to
            positions[key(other)] = from
        }
        return ordered
    }

    fun move(items: List<Item>, moves: List<Move>, wanted: String, delta: Int): List<Move> {
        val ordered = apply(items, moves)
        val from = ordered.indexOfFirst { key(it) == wanted }
        if (from < 0 || ordered.size < 2 || from + delta.coerceIn(-1, 1) !in ordered.indices) return moves
        return moves + Move(wanted, delta.coerceIn(-1, 1))
    }
}
