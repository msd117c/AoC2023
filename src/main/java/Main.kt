import days.DayFour
import days.DayOne
import days.DayThree
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
        println("-----------------")
        println("----- Day 3 -----")
        DayThree.puzzle1()
        DayThree.puzzle2()
        println("-----------------")
        println("----- Day 4 -----")
        DayFour.puzzle1()
        DayFour.puzzle2()
    }
}
