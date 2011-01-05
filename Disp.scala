package com.sukarabu.sgsoccer

object Main{
    def main(args:Array[String]) {
        clearScreen
        showBoard
    }

    def clearScreen = {
        print("[2J")
    }

    def showBoard = {
        println("==== SHO-GUN SOCCER ====")
        println("          1         2         3")
        println(" 123456789012345678901234567890")
        println("+------------------------------+")
        for (i <- 1 to 12) {
            print("|..............................|")
            println(format("%2d", i))
        }
        println("+------------------------------+")
    }
}

// vim: set ts=4 sw=4 et:
