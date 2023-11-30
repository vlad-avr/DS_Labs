package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"strconv"
	"strings"
)

func Swap(slice []int, index int) {
	temp := slice[index]
	slice[index] = slice[index+1]
	slice[index+1] = temp
}

func BubbleSort(slice []int) {
	for i := 0; i < len(slice)-1; i++ {
		for j := 0; j < len(slice)-1-i; j++ {
			if slice[j] > slice[j+1] {
				Swap(slice, j)
			}
		}
	}
}

func main() {
	fmt.Println("Enter 10 or less integers:")
	slice := make([]int, 0, 10)
	scan := bufio.NewScanner(os.Stdin)
	scan.Scan()

	input := strings.Fields(scan.Text())
	for _, num := range input {
		integer_num, invalid_input := strconv.Atoi(num)
		if invalid_input != nil {
			log.Fatal("Invalid input!")
		}
		slice = append(slice, integer_num)
	}
	BubbleSort(slice)
	fmt.Println("Sorted sequence:")
	for _, num := range slice {
		fmt.Print(num, ", ")
	}
}
