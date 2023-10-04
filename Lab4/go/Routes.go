package main

import (
	"math/rand"
	"time"
)

// type Map struct {
// 	routes [][]int
// 	size   int
// }

// func (m *Map) init(N int) {
// 	seed := rand.NewSource(time.Now().Unix())
// 	rnd := rand.New(seed)
// 	m.size = N
// 	for i := 0; i < N; i++ {
// 		for j := 0; j < N; j++ {
// 			m.routes[i][j] = rnd.Intn(100) + 1
// 		}
// 	}
// }

// func (m *Map) print_map() {
// 	for i := 0; i < m.size; i++ {
// 		for j := 0; j < m.size; j++ {
// 			println("\t", m.routes[i][j])
// 		}
// 	}
// }

type Semaphore struct {
	cur_concurrency int
	max_concurrency int
}

func (s *Semaphore) write_lock() {
	s.cur_concurrency = s.max_concurrency
}

func (s *Semaphore) read_lock() {
	s.cur_concurrency++
}

func (s *Semaphore) release_write_lock() {
	s.cur_concurrency = 0
}

func (s *Semaphore) release_read_lock() {
	s.cur_concurrency--
}

func (s *Semaphore) is_full() bool {
	if s.cur_concurrency == s.max_concurrency {
		return true
	} else {
		return false
	}
}

func (s *Semaphore) is_empty() bool {
	if s.cur_concurrency == 0 {
		return true
	} else {
		return false
	}
}

func shuffle_prices(routes chan [][]int, size int, sem Semaphore, rnd rand.Rand) {
	for {
		time.Sleep(time.Millisecond * time.Duration(rnd.Intn(1000)+200))
		for !sem.is_empty() {
			continue
		}
		sem.write_lock()
		route := <-routes
		i := rnd.Intn(size)
		j := rnd.Intn(size)
		for i == j {
			j = rnd.Intn(size)
		}
		route[i][j] = rnd.Intn(100) + 1
		route[j][i] = route[i][j]
		print("\nChanged price of (", i, " , ", j, ") route to ", route[i][j])
		routes <- route
	}
}

func shuffle_routes(routes chan [][]int, size int, sem Semaphore, rnd rand.Rand) {
	for {
		time.Sleep(time.Millisecond * time.Duration(rnd.Intn(1000)+200))
		for !sem.is_empty() {
			continue
		}
		sem.write_lock()
		route := <-routes
		i := rnd.Intn(size)
		j := rnd.Intn(size)
	}
}

func main() {
	routes := make(chan [][]int, 1)
	sem := Semaphore{0, 2}
	cur_routes := <-routes
	size := 5
	seed := rand.NewSource(time.Now().Unix())
	rnd := rand.New(seed)
	for i := 0; i < size; i++ {
		for j := 0; j < size; j++ {
			cur_routes[i][j] = rnd.Intn(10)
			cur_routes[j][i] = cur_routes[i][j]
		}
	}
	routes <- cur_routes
	go shuffle_prices(routes, size, sem, *rnd)
}
