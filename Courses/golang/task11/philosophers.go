package main

import (
	"fmt"
	"sync"
	"time"
)

// Synchronizer
var wg sync.WaitGroup

// Philosopher struct
type Philosopher struct {
	id              int
	number_of_meals int
	left_chstick    *Chopstick
	right_chstick   *Chopstick
}

// Chopstick struct (contains mutext that is locked when chopstick is acquired)
type Chopstick struct {
	sync.Mutex
}

// Host of the feast (manages philosophers)
type Host struct {
	philosophers []*Philosopher
}

func NewHost(philosophers []*Philosopher) *Host {
	return &Host{philosophers: philosophers}
}

func NewPhilosopher(id int, leftChopstick, rightChopstick *Chopstick) *Philosopher {
	return &Philosopher{
		id:            id,
		left_chstick:  leftChopstick,
		right_chstick: rightChopstick,
	}
}

// Feast activity
func (host *Host) feast() {
	wg.Add(1)

	selected_eaters := make(chan struct{}, 2)

	var wait_for_eaters sync.WaitGroup

	for _, philosopher := range host.philosophers {
		philosopher := philosopher

		// Start eating activity for philosopher
		go func() {
			//Eating limitted to 3 times
			for i := 0; i < 3; i++ {
				selected_eaters <- struct{}{}
				philosopher.eat()
				<-selected_eaters
			}
			wait_for_eaters.Done()
		}()
		wait_for_eaters.Add(1)
	}

	// Wait eaters to finish eating
	wait_for_eaters.Wait()
	wg.Done()
}

// Eating function for philosopher
func (philosopher *Philosopher) eat() {
	philosopher.left_chstick.Lock()
	philosopher.right_chstick.Lock()

	fmt.Println("starting to eat", philosopher.id)
	time.Sleep(time.Millisecond * 50)
	philosopher.number_of_meals++

	philosopher.right_chstick.Unlock()
	philosopher.left_chstick.Unlock()
	fmt.Println("finishing eating", philosopher.id)
}

func main() {
	chopsticks := make([]*Chopstick, 5)
	for i := 0; i < 5; i++ {
		chopsticks[i] = &Chopstick{}
	}

	philosophers := make([]*Philosopher, 5)
	for i := 0; i < 5; i++ {
		philosophers[i] = NewPhilosopher(i+1, chopsticks[i], chopsticks[(i+1)%5])
	}

	host := NewHost(philosophers)

	go host.feast()

	time.Sleep(time.Millisecond * 10)
	wg.Wait()
}
