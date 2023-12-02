import days.DayOne
import days.DayTwo

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println("----- Day 1 -----")
        DayOne.puzzle1()
        DayOne.puzzle2()
        println("-----------------")
        println("----- Day 2 -----")
        DayTwo.puzzle1()
        DayTwo.puzzle2()
    }
}
