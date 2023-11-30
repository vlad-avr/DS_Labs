package main

import (
	"fmt"
	"sort"
	"strconv"
)

func main() {
	var input string
	var input_invalid error
	const init_size = 3
	slice := make([]int, init_size)

	for i := 0; ; i++ {
		fmt.Println("Enter an integer (or X to exit):")
		fmt.Scan(&input)
		if input == "X" {
			fmt.Println("You have exited the loop!")
			break
		}
		slice[i], input_invalid = strconv.Atoi(input)
		if input_invalid != nil {
			fmt.Println("Error: invalid input - must be integer or letter X")
			i--
			continue
		}
		if i+1 == len(slice) {
			slice = append(slice, make([]int, init_size)...)
		}

		sort.Ints(slice[:i+1])
		fmt.Println("Sorted slice: ")
		fmt.Println(slice[:i+1])
	}
}
