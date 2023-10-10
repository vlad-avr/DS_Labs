package main

import (
	"math/rand"
	"sync"
)

var arr_size int
var upper_bound int
var offset int

type Barrier struct {
	wait_queue chan bool
	sem        chan bool
	id         int
}

func new_barrier(maxThreads int) *Barrier {
	return &Barrier{
		wait_queue: make(chan bool, 4),
		sem:        make(chan bool, 1),
		id:         maxThreads,
	}
}

func (b *Barrier) acquire() {
	for len(b.sem) == 0 {
		continue
	}
	<-b.sem
}

func (b *Barrier) release() {
	b.sem <- true
}

func (b *Barrier) await(id int) {
	b.trip(id)
	for len(b.wait_queue) != 0 {
		continue
	}
	//print("\nBarrier ", b.id, " breached by ", id, " : ", len(b.wait_queue), "\n")
}

func (b *Barrier) trip(id int) {
	b.acquire()
	<-b.wait_queue
	//print("\nBarrier ", b.id, " tripped by ", id, " : ", len(b.wait_queue), "\n")
	b.release()
}
func (b *Barrier) reset() {
	if len(b.wait_queue) == 0 {
		b.acquire()
		for i := 0; i < 4; i++ {
			b.wait_queue <- true
		}
		print("\nBarrier ", b.id, " reset : ", len(b.wait_queue), "\n")
		b.release()
	}
}

func check_completion(sums chan int, b1 *Barrier, b2 *Barrier, all_done chan bool, wg *sync.WaitGroup) {
	for {
		println("Checker is waiting for results\n")
		b1.await(0)
		b2.reset()
		sum1 := <-sums
		sum2 := <-sums
		sum3 := <-sums
		println("Checker recieved sums : ", sum1, " ", sum2, " ", sum3, "\n")
		if (sum1 == sum2) && (sum2 == sum3) {
			for i := 0; i < 3; i++ {
				all_done <- true
			}
			println("Sums are equal -> all done\n")
			break
		} else {
			println("Sums are not equal -> continue shuffling\n")
			b1.reset()
			b2.await(0)
		}
	}
	wg.Done()
}

func shuffle_array(all_done chan bool, b1 *Barrier, b2 *Barrier, sums chan int, id int, wg *sync.WaitGroup) {
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
		b1.await(id)
		println("Array ", id, " is waiting for checker check\n")
		b2.await(id)
		println("Shuffling elements of array ", id, " :\n")
		if len(all_done) != 0 {
			break
		}
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
		// print("\n Doing ", id, "\n")
	}
	wg.Done()
}

func main() {
	var wg sync.WaitGroup
	arr_size = 5
	upper_bound = 3
	offset = 1
	b1 := new_barrier(1)
	b2 := new_barrier(2)
	b1.sem <- true
	b2.sem <- true
	b1.reset()
	b2.reset()
	sums := make(chan int, 3)
	all_done := make(chan bool, 1)
	wg.Add(4)
	go shuffle_array(all_done, b1, b2, sums, 1, &wg)
	go shuffle_array(all_done, b1, b2, sums, 2, &wg)
	go shuffle_array(all_done, b1, b2, sums, 3, &wg)
	check_completion(sums, b1, b2, all_done, &wg)
	wg.Wait()
}
