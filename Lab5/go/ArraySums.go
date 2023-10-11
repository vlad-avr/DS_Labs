package main

import (
	"math/rand"
	"sync"
)

var arr_size int
var upper_bound int
var offset int

func check_completion(sums chan int, b1 *sync.WaitGroup, all_done chan bool, wg *sync.WaitGroup) {
	b1.Add(3)
	for {
		println("Checker is waiting for results\n")
		b1.Wait()
		sum1 := <-sums
		sum2 := <-sums
		sum3 := <-sums
		println("Checker recieved sums : ", sum1, " ", sum2, " ", sum3, "\n")
		b1.Add(3)
		if (sum1 == sum2) && (sum2 == sum3) {
			for i := 0; i < 3; i++ {
				all_done <- true
			}
			println("Sums are equal -> all done\n")
			break
		} else {
			for i := 0; i < 3; i++ {
				all_done <- false
			}
			println("Sums are not equal -> continue shuffling\n")
		}
	}
	wg.Done()
}

func shuffle_array(all_done chan bool, b1 *sync.WaitGroup, sums chan int, id int, wg *sync.WaitGroup) {
	println("Creating array ", id, "\n")
	arr := make([]int, arr_size)
	for i := 0; i < arr_size; i++ {
		arr[i] = rand.Intn(upper_bound) + offset
	}
	for {
		var sum int
		sum = 0

		for i := 0; i < arr_size; i++ {
			sum += arr[i]
		}
		sums <- sum
		println("Calculating sum of array ", id, " : ", sum, "\n")
		println("Array ", id, " is waiting for checker check\n")
		b1.Done()
		for {
			if len(all_done) != 0 {
				break
			}
		}
		if <-all_done {
			break
		}
		println("Shuffling elements of array ", id, " :\n")
		for i := 0; i < arr_size; i++ {
			if rand.Intn(2) == 1 {
				if arr[i] < upper_bound {
					arr[i]++
				} else {
					arr[i]--
				}
			} else {
				if arr[i] > 0 {
					arr[i]--
				} else {
					arr[i]++
				}
			}
		}
	}
	wg.Done()
}

func main() {
	var wg sync.WaitGroup
	arr_size = 5
	upper_bound = 5
	offset = 1
	wg1 := new(sync.WaitGroup)
	sums := make(chan int, 3)
	all_done := make(chan bool, 1)
	wg.Add(4)
	go shuffle_array(all_done, wg1, sums, 1, &wg)
	go shuffle_array(all_done, wg1, sums, 2, &wg)
	go shuffle_array(all_done, wg1, sums, 3, &wg)
	check_completion(sums, wg1, all_done, &wg)
	wg.Wait()
}
