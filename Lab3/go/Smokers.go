package main

import (
	"sync"
	"time"
)

type Smoker struct {
	item string
}

type Semaphore struct {
	sem chan struct{}
}

func (s *Semaphore) acquire() {
	s.sem <- struct{}{}
}

func (s *Semaphore) release() {
	<-s.sem
}

func new_semaphore(max_concurrency int) *Semaphore {
	return &Semaphore{
		sem: make(chan struct{}, max_concurrency),
	}
}

func put_part(smoker *Smoker, table chan string, counter chan int, semaphore *Semaphore, wg *sync.WaitGroup) {
	defer wg.Done()
	time.Sleep(time.Second)
	semaphore.acquire()
	i := <-counter
	if i == 2 {
		part1 := <-table
		part2 := <-table
		print("Smoker with " + smoker.item + " has taken " + part1 + " and " + part2 + " and smoking `dat za rn!\n")
		i = 0
		counter <- i
		time.Sleep(time.Second * 2)
		print("Smoker has stopped smoking `dat za, feels good :) He has returned the items: " + part1 + " and " + part2 + "\n")
	} else {
		print("Smoker has put "+smoker.item+" on the table, there are ", i+1, " items on the table now\n")
		table <- smoker.item
		i++
		counter <- i
	}
	semaphore.release()
}

func make_smokers(smokers chan Smoker) {
	smokers <- Smoker{item: "tobacco"}
	smokers <- Smoker{item: "paper"}
	smokers <- Smoker{item: "matches"}
}

func start_session() {
	for {
		var wg sync.WaitGroup
		print("\n Nobody is smoking `dat za rn :(\n")
		smokers := make(chan Smoker, 3)
		make_smokers(smokers)
		table := make(chan string, 2)
		part_counter := make(chan int, 1)
		part_counter <- 0
		semaphore := new_semaphore(1)
		smoker1 := <-smokers
		smoker2 := <-smokers
		smoker3 := <-smokers
		wg.Add(3)
		go put_part(&smoker1, table, part_counter, semaphore, &wg)
		go put_part(&smoker2, table, part_counter, semaphore, &wg)
		go put_part(&smoker3, table, part_counter, semaphore, &wg)
		wg.Wait()
	}
}

func main() {
	start_session()
}
