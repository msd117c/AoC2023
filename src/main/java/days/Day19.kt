package days

import utils.InputReader.readInput

object Day19 {

    private const val WORKFLOW_PATTERN = "^(.*)\\{(.*)}"
    private const val CONDITION_PATTERN = "^(.)(.)(\\d+):([^,;]+),(.*)"
    private const val PART_PATTERN = "^\\{.=(\\d+),.=(\\d+),.=(\\d+),.=(\\d+)}"

    @JvmStatic
    fun main(args: Array<String>) {
        puzzle1()
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
        val test = conditionValue[3]?.value?.toLong() ?: error("Bad test number")
        val positive = conditionValue[4]?.value ?: error("Bad test number")
        val negative = conditionValue[5]?.value?.takeUnless { it.contains(":") }
        val negativeConditionValue = conditionValue[5]?.value?.takeIf { it.contains(":") }

        return Condition(spec, operation, test, positive, negativeConditionValue?.parseCondition(), negative)
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

    private data class Condition(
        val spec: Char,
        val operation: Char,
        val test: Long,
        val positive: String,
        val negativeCondition: Condition?,
        val negative: String?
    )

    private data class Workflow(val id: String, val condition: Condition)
    private data class Part(val x: Long, val m: Long, val a: Long, val s: Long)
}
