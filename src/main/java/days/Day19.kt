package days

import utils.InputReader.readInput

object Day19 {

    private const val WORKFLOW_PATTERN = "^(.*)\\{(.*)}"
    private const val CONDITION_PATTERN = "^(.)(.)(\\d+):([^,;]+),(.*)"
    private const val PART_PATTERN = "^\\{.=(\\d+),.=(\\d+),.=(\\d+),.=(\\d+)}"

    @JvmStatic
    fun main(args: Array<String>) {
        puzzle1()
        puzzle2()
    }

    private fun puzzle1() {
        readInput("day19Input") { lines ->
            val input = lines.toList().joinToString("\n").split(Regex("\n\n"))
            val workflows = input[0].lines().parseWorkflows()
            val parts = input[1].lines().parseParts()

            val acceptedParts = parts.process(workflows)
            val rating = acceptedParts.sumOf { it.x + it.m + it.a + it.s }
            println("Day 19 puzzle 1 result is: $rating")
        }
    }

    private fun List<String>.parseParts(): List<Part> {
        return map { line ->
            val groups = Regex(PART_PATTERN).matchEntire(line)?.groups ?: error("Bad part")
            val x = groups[1]?.value?.toLong() ?: error("Bad x")
            val m = groups[2]?.value?.toLong() ?: error("Bad m")
            val a = groups[3]?.value?.toLong() ?: error("Bad a")
            val s = groups[4]?.value?.toLong() ?: error("Bad s")

            Part(x, m, a, s)
        }
    }

    private fun List<Part>.process(workflows: List<Workflow>): List<Part> {
        val firstWorkflow = workflows.first { it.id == "in" }

        return mapNotNull { part -> part.processPart(firstWorkflow, workflows) }
    }

    private fun Part.processPart(workflow: Workflow, workflows: List<Workflow>): Part? {
        val condition = workflow.condition
        val result = getConditionResult(condition)

        return processResult(result, condition, workflows)
    }

    private fun Part.getConditionResult(condition: Condition): Boolean {
        val specToCheck = when (condition.spec) {
            'x' -> x
            'm' -> m
            'a' -> a
            's' -> s
            else -> error("Bad spec to check")
        }

        return when (condition.operation) {
            '>' -> specToCheck > condition.test
            '<' -> specToCheck < condition.test
            else -> error("Bad operation to test")
        }
    }

    private fun Part.processResult(
        result: Boolean,
        condition: Condition,
        workflows: List<Workflow>
    ): Part? {
        return if (result) {
            when (condition.positive) {
                "A" -> this
                "R" -> null
                else -> processPart(workflows.first { it.id == condition.positive }, workflows)
            }
        } else {
            if (condition.negative != null) {
                when (condition.negative) {
                    "A" -> this
                    "R" -> null
                    else -> processPart(workflows.first { it.id == condition.negative }, workflows)
                }
            } else {
                processResult(getConditionResult(condition.negativeCondition!!), condition.negativeCondition, workflows)
            }
        }
    }

    private fun puzzle2() {
        readInput("day19Input") { lines ->
            val workflows = lines.toList().joinToString("\n").split(Regex("\n\n"))[0].lines().parseWorkflows()

            val acceptedPaths =
                workflows.getConditionsToAccepted().filterKeys { it.last().startsWith("A", ignoreCase = false) }
            val partRanges = acceptedPaths.mapValues { it.value.getPartRanges() }.map { it.value }

            val combinations = partRanges.sumOf {
                it.x.count().toLong() * it.m.count().toLong() * it.s.count().toLong() * it.a.count().toLong()
            }
            println("Day 19 puzzle 2 result is. $combinations")
        }
    }

    private fun List<String>.parseWorkflows(): List<Workflow> {
        return map { line ->
            val groups = Regex(WORKFLOW_PATTERN).matchEntire(line)?.groups ?: error("Bad workflow")
            val workflowId = groups[1]?.value ?: error("Bad id")
            val condition = groups[2]?.value?.parseCondition() ?: error("Bad condition")

            Workflow(workflowId, condition)
        }
    }

    private fun String.parseCondition(): Condition {
        val conditionValue = Regex(CONDITION_PATTERN).matchEntire(this)?.groups ?: error("Bad condition")
        val spec = conditionValue[1]?.value?.firstOrNull() ?: error("Bad spec")
        val operation = conditionValue[2]?.value?.firstOrNull() ?: error("Bad operation")
        val test = conditionValue[3]?.value?.toInt() ?: error("Bad test number")
        val positive = conditionValue[4]?.value ?: error("Bad test number")
        val negative = conditionValue[5]?.value?.takeUnless { it.contains(":") }
        val negativeConditionValue = conditionValue[5]?.value?.takeIf { it.contains(":") }

        return Condition(spec, operation, test, positive, negativeConditionValue?.parseCondition(), negative)
    }

    private fun List<Workflow>.getConditionsToAccepted(): Map<List<String>, List<Constraint>> {
        val graph = associate { it.id to it.condition.getPaths(emptyList()) }
        val origin = "in"
        val queue = mutableListOf(listOf(origin) to emptyList<Constraint>())
        val paths = mutableMapOf(listOf(origin) to emptyList<Constraint>())

        while (queue.isNotEmpty()) {
            val (workflows, constraints) = queue.removeFirst()
            val current = workflows.last()

            if (current.startsWith("A", ignoreCase = false)) continue
            if (current == "R") continue

            val neighbors = graph[current]

            neighbors?.forEach { path ->
                val nextId = path.workflowId

                val newConstraints = constraints + path.constraints

                val nextValue = if (nextId == "A") {
                    val lastAIndex = paths.filterKeys { it.last().startsWith("A", ignoreCase = false) }.count()
                    paths[workflows + listOf("$nextId$lastAIndex")] = newConstraints
                    workflows + listOf("$nextId$lastAIndex") to newConstraints
                } else {
                    workflows + listOf(nextId) to newConstraints
                }

                queue.add(nextValue)
            }
        }

        return paths
    }

    private fun Condition.getPaths(negativeConstraints: List<Constraint>): List<Path> {
        val positiveRange = when (operation) {
            '>' -> IntRange(test + 1, 4000)
            '<' -> IntRange(1, test - 1)
            else -> error("Bad positive range")
        }
        val positiveConstraint = Constraint(spec, positiveRange)

        val negativeRange = when (operation) {
            '>' -> IntRange(1, test)
            '<' -> IntRange(test, 4000)
            else -> error("Bad negative range")
        }
        val negativeConstraint = Constraint(spec, negativeRange)

        return listOf(Path(positive, negativeConstraints + listOf(positiveConstraint))) + if (negative != null) {
            listOf(Path(negative, negativeConstraints + listOf(negativeConstraint)))
        } else {
            negativeCondition?.getPaths(listOf(negativeConstraint)).orEmpty()
        }
    }

    private fun List<Constraint>.getPartRanges(): PartRange {
        val xRanges = filter { it.spec == 'x' }.map { it.range }
        val xRange = if (xRanges.isEmpty()) {
            1..4000
        } else {
            val maxX = xRanges.minOf { it.last }
            val minX = xRanges.maxOf { it.first }

            minX..maxX
        }

        val mRanges = filter { it.spec == 'm' }.map { it.range }
        val mRange = if (mRanges.isEmpty()) {
            1..4000
        } else {
            val maxM = mRanges.minOf { it.last }
            val minM = mRanges.maxOf { it.first }

            minM..maxM
        }

        val aRanges = filter { it.spec == 'a' }.map { it.range }
        val aRange = if (aRanges.isEmpty()) {
            1..4000
        } else {
            val maxA = aRanges.minOf { it.last }
            val minA = aRanges.maxOf { it.first }

            minA..maxA
        }

        val sRanges = filter { it.spec == 's' }.map { it.range }
        val sRange = if (sRanges.isEmpty()) {
            1..4000
        } else {
            val maxS = sRanges.minOf { it.last }
            val minS = sRanges.maxOf { it.first }

            minS..maxS
        }

        return PartRange(xRange, mRange, aRange, sRange)
    }

    private data class PartRange(val x: IntRange, val m: IntRange, val a: IntRange, val s: IntRange)
    private data class Path(val workflowId: String, val constraints: List<Constraint>)
    private data class Constraint(val spec: Char, val range: IntRange)
    private data class Condition(
        val spec: Char,
        val operation: Char,
        val test: Int,
        val positive: String,
        val negativeCondition: Condition?,
        val negative: String?
    )

    private data class Workflow(val id: String, val condition: Condition)
    private data class Part(val x: Long, val m: Long, val a: Long, val s: Long)
}
