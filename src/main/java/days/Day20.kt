package days

import utils.InputReader.readInput

object Day20 {

    private const val MODULE_PATTERN = "^([%|&]?)(.*) -> (.*)"

    @JvmStatic
    fun main(args: Array<String>) {
        puzzle1()
        // Help received: completing the image on my brain
        puzzle2()
    }

    private fun puzzle1() {
        readInput("day20Input") { lines ->
            val modules = lines.toList().parseModules()

            repeat(1000) {
                propagatePulse(modules, stopOnRx = false)
            }
            println("Low: $lowCounter")
            println("High: $highCounter")
            println("Day 20 puzzle 1 result is: ${lowCounter * highCounter}")
        }
    }

    private fun puzzle2() {
        readInput("day20Input") { lines ->
            val modules = lines.toList().parseModules()

            repeat(100000) {
                if (!found) {
                    propagatePulse(modules, stopOnRx = true)
                }
            }
            println("pm on HIGH on 3881")
            println("mk on HIGH on 3889")
            println("pk on HIGH on 4021")
            println("hf on HIGH on 4013")
            val result = listOf(3881L, 3889L, 4021L, 4013L).reduce { acc, i -> lcm(acc, i) }
            println("Day 20 puzzle 2 result is: $result")
        }
    }

    private fun lcm(a: Long, b: Long) = a / gcd(a, b) * b

    private fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

    private var lowCounter = 0
    private var highCounter = 0
    private var counter = 0
    private var found = false

    private fun propagatePulse(modules: List<Module>, stopOnRx: Boolean) {
        if (stopOnRx) {
            counter++
        }
        val broadcaster = modules.firstOrNull { it.name == "broadcaster" } ?: error("No broadcaster")
        val queue =
            mutableListOf<Pair<Pair<Pulse?, String>, List<Module>>>((Pulse.LOW to "button") to listOf(broadcaster))

        while (queue.isNotEmpty()) {
            val (pulseAndOrigin, affectedModules) = queue.removeFirst()
            val (currentPulse, origin) = pulseAndOrigin

            if (currentPulse == null) continue

            affectedModules.forEach { module ->
                val outputs = modules.filter { module.outputs.contains(it.name) }

                println("$origin - $currentPulse - ${module.name}")
                incrementCounter(currentPulse)
                val nextPulse = module.processPulse(currentPulse, origin)

                // Improve to get the correct four values dynamically
                if (stopOnRx) {
                    if (origin == "pk" && currentPulse == Pulse.HIGH) {
                        found = true
                        return
                    }
                }

                if (outputs.isNotEmpty()) {
                    queue.add((nextPulse to module.name) to outputs)
                } else {
                    module.outputs.forEach {
                        //println("${module.name} - $nextPulse - $it")
                        nextPulse?.let { incrementCounter(nextPulse) }
                    }
                }
            }
        }
    }

    private fun incrementCounter(pulse: Pulse) {
        if (pulse == Pulse.HIGH) {
            highCounter++
        } else {
            lowCounter++
        }
    }

    private fun List<String>.parseModules(): List<Module> {
        val modules = map { line ->
            val groups = Regex(MODULE_PATTERN).matchEntire(line)?.groups ?: error("Not valid input")
            val type = groups[1]?.value.orEmpty()
            val name = groups[2]?.value ?: error("Not valid name")
            val outputs = groups[3]?.value?.split(", ") ?: error("Not valid outputs")

            when (type) {
                "%" -> FlipFlop(name, emptyList(), outputs)
                "&" -> Conjunction(name, emptyList(), outputs)
                else -> Broadcaster(name, outputs)
            }
        }

        return modules.map { module ->
            val inputs = modules.filter { it.outputs.contains(module.name) }.map { it.name }

            when (module) {
                is FlipFlop -> module.copy(inputs = inputs)
                is Conjunction -> module.copy(inputs = inputs)
                else -> module
            }
        }
    }

    private enum class Pulse {
        LOW,
        HIGH
    }

    private enum class State {
        ON,
        OFF
    }

    private sealed class Module(open val name: String, open val outputs: List<String>) {

        abstract fun processPulse(pulse: Pulse, inputId: String): Pulse?
    }

    private data class Broadcaster(override val name: String, override val outputs: List<String>) :
        Module(name, outputs) {

        override fun processPulse(pulse: Pulse, inputId: String): Pulse = pulse
    }

    private data class FlipFlop(
        override val name: String,
        val inputs: List<String>,
        override val outputs: List<String>
    ) : Module(name, outputs) {

        private var state: State = State.OFF

        override fun processPulse(pulse: Pulse, inputId: String): Pulse? {
            if (pulse == Pulse.HIGH) return null

            state = if (state == State.OFF) State.ON else State.OFF

            return if (state == State.ON) Pulse.HIGH else Pulse.LOW
        }
    }

    private data class Conjunction(
        override val name: String,
        val inputs: List<String>,
        override val outputs: List<String>
    ) : Module(name, outputs) {

        private val lastPulses: MutableMap<String, Pulse> = inputs.associateWith { Pulse.LOW }.toMutableMap()

        override fun processPulse(pulse: Pulse, inputId: String): Pulse {
            lastPulses[inputId] = pulse

            return if (lastPulses.values.all { it == Pulse.HIGH }) Pulse.LOW else Pulse.HIGH
        }
    }
}
