package utils

object InputReader {

    fun readInput(fileName: String, block: (Sequence<String>) -> Unit) {
        val inputStream =
            javaClass.classLoader.getResource(fileName)?.openStream() ?: throw IllegalStateException("No input!")
        inputStream.bufferedReader().useLines { lines -> block(lines) }
    }
}

