package main

import (
	"container/heap"
	"math/rand"
	"sync"
	"time"
)

var routes [][]int
var size int
var max_cost int

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

func shuffle_prices(sem Semaphore, rnd rand.Rand) {
	for {
		time.Sleep(time.Millisecond * time.Duration(rnd.Intn(1000)+200))
		for !sem.is_empty() {
			continue
		}
		sem.write_lock()
		i := rnd.Intn(size)
		j := rnd.Intn(size)
		for i == j {
			j = rnd.Intn(size)
		}
		routes[i][j] = rnd.Intn(max_cost) + 1
		routes[j][i] = routes[i][j]
		print("\nChanged price of (", i, " , ", j, ") route to ", routes[i][j])
		print_routes(&routes, size)
		sem.release_write_lock()
	}
}

func shuffle_routes(sem Semaphore, rnd rand.Rand) {
	for {
		time.Sleep(time.Millisecond * time.Duration(rnd.Intn(1000)+200))
		for !sem.is_empty() {
			continue
		}
		sem.write_lock()
		i := rnd.Intn(size)
		j := rnd.Intn(size)
		for i == j {
			j = rnd.Intn(size)
		}
		if routes[i][j] == 0 {
			routes[i][j] = rnd.Intn(max_cost) + 1
			routes[j][i] = routes[i][j]
			print("\nAdded route from ", i, " to ", j, " with price ", routes[i][j])
		} else {
			routes[i][j] = 0
			routes[j][i] = 0
			print("\nRemoved route from ", i, " to ", j)
		}
		print_routes(&routes, size)
		sem.release_write_lock()
	}
}

func shuffle_cities(sem Semaphore, rnd rand.Rand) {
	for {
		time.Sleep(time.Millisecond * time.Duration(rnd.Intn(3000)+1000))
		for !sem.is_empty() {
			continue
		}
		sem.write_lock()
		if size <= 3 {

			new_route := make([][]int, size+1)
			for i := 0; i < size+1; i++ {
				new_route[i] = make([]int, size+1)
			}
			for i := 0; i < size; i++ {
				for j := i; j < size; j++ {
					new_route[i][j] = routes[i][j]
					new_route[j][i] = routes[j][i]
				}
			}
			size++
			for i := 0; i < size; i++ {
				flip := rnd.Intn(2)
				if flip == 0 {
					new_route[i][size-1] = rnd.Intn(max_cost) + 1
				} else {
					new_route[i][size-1] = 0
				}
				new_route[size-1][i] = new_route[i][size-1]
			}
			routes = new_route
			print("\n Added 1 city, number of cities : ", size)
			print_routes(&new_route, size)
		} else {
			flip := rnd.Intn(2)
			if flip == 0 {
				new_route := make([][]int, size+1)
				for i := 0; i < size+1; i++ {
					new_route[i] = make([]int, size+1)
				}
				for i := 0; i < size; i++ {
					for j := i; j < size; j++ {
						new_route[i][j] = routes[i][j]
						new_route[j][i] = routes[j][i]
					}
				}
				size++
				for i := 0; i < size; i++ {
					if flip == 0 {
						new_route[i][size-1] = rnd.Intn(max_cost) + 1
					} else {
						new_route[i][size-1] = 0
					}
					new_route[size-1][i] = new_route[i][size-1]
				}
				routes = new_route
				print("\n Added 1 city, number of cities : ", size)
				print_routes(&new_route, size)
			} else {
				deleted_city := rnd.Intn(size)
				new_route := make([][]int, size-1)
				for i := 0; i < size-1; i++ {
					new_route[i] = make([]int, size-1)
				}
				// for i := 0; i < deleted_city; i++ {
				// 	for j := i; j < deleted_city; j++ {
				// 		new_route[i][j] = routes[i][j]
				// 		new_route[j][i] = routes[j][i]
				// 	}
				// 	for j := deleted_city; j < size-1; j++ {
				// 		new_route[i][j] = routes[i][j+1]
				// 		new_route[j][i] = routes[j+1][i]
				// 	}
				// }
				// for i := deleted_city; i < size-1; i++ {
				// 	for j := i; j < size-1; j++ {
				// 		new_route[i][j] = routes[i+1][j+1]
				// 		new_route[j][i] = routes[j+1][i+1]
				// 	}
				// }
				for i := 0; i < size; i++ {
					if i < deleted_city {
						for j := 0; j < size; j++ {
							if j < deleted_city {
								new_route[i][j] = routes[i][j]
							} else if j > deleted_city {
								new_route[i][j-1] = routes[i][j]
							}
						}
					} else if i > deleted_city {
						for j := 0; j < size; j++ {
							if j < deleted_city {
								new_route[i-1][j] = routes[i][j]
							} else if j > deleted_city {
								new_route[i-1][j-1] = routes[i][j]
							}
						}
					}
				}
				size--
				routes = new_route
				print("\n Deleted city ", deleted_city, ", number of cities : ", size)
				print_routes(&new_route, size)
			}
		}
		sem.release_write_lock()
	}

}

type Item struct {
	value    int
	priority int
	index    int
}

type PriorityQueue []*Item

func (piq PriorityQueue) Len() int {
	return len(piq)
}
func (piq PriorityQueue) Less(i, j int) bool {

	return piq[i].priority < piq[j].priority
}
func (piq PriorityQueue) Swap(i, j int) {
	piq[i], piq[j] = piq[j], piq[i]
	piq[i].index = i
	piq[j].index = j
}
func (piq *PriorityQueue) Push(x interface{}) {
	n := len(*piq)
	item := x.(*Item)
	item.index = n
	*piq = append(*piq, item)
}
func (piq *PriorityQueue) Pop() interface{} {
	old := *piq
	n := len(old)
	item := old[n-1]
	item.index = -1
	*piq = old[0 : n-1]
	return item
}

func (piq *PriorityQueue) Update(item *Item, value int, priority int) {
	item.value = value
	item.priority = priority
	heap.Fix(piq, item.index)
}

func calculate_path(sem Semaphore, rnd rand.Rand) {
	for {
		time.Sleep(time.Millisecond * time.Duration(rnd.Intn(1000)+500))
		for sem.is_full() {
			print("\n", sem.cur_concurrency, "\n")
		}
		sem.read_lock()
		start := rnd.Intn(size)
		end := rnd.Intn(size)
		for start == end || routes[start][end] == 0 {
			end = rnd.Intn(size)
		}
		print("\nFiding cheapest path from ", start, " to ", end)
		piq := make(PriorityQueue, 0)
		heap.Push(&piq, &Item{value: start, priority: 0})
		dist := make([]int, size)
		dist[start] = 0
		for i := 0; i < size; i++ {
			if i != start {
				dist[i] = 1000000
			}
		}
		for len(piq) != 0 {
			v := heap.Pop(&piq).(*Item)
			for i := 0; i < size; i++ {
				if routes[v.value][i] != 0 {
					if dist[i] > dist[v.value]+routes[v.value][i] {
						dist[i] = dist[v.value] + routes[v.value][i]
						heap.Push(&piq, &Item{value: i, priority: dist[i]})
					}
				}
			}
		}
		if dist[end] == 1000000 {
			print("\n The route from ", start, " to ", end, " does not exist ")
		} else {
			print("\n The cheapest route from ", start, " to ", end, " is ", dist[end])
		}
		sem.release_read_lock()
	}
}

func print_routes(routes *[][]int, size int) {
	print("\n{")
	for i := 0; i < size; i++ {
		print("\n{")
		for j := 0; j < size; j++ {
			print(" {", i, " <-", (*routes)[i][j], "-> ", j, "} ")
		}
		print("}")
	}
	print("}")
}

func main() {
	var wg sync.WaitGroup
	sem := Semaphore{0, 2}
	size = 5
	max_cost = 50
	routes = make([][]int, size)
	for i := 0; i < size; i++ {
		routes[i] = make([]int, size)
	}
	seed := rand.NewSource(time.Now().Unix())
	rnd := rand.New(seed)
	for i := 0; i < size; i++ {
		for j := i + 1; j < size; j++ {
			flip := rnd.Intn(2)
			if flip == 0 {
				routes[i][j] = 0
			} else {
				routes[i][j] = rnd.Intn(max_cost) + 1
			}
			routes[j][i] = routes[i][j]
		}
	}
	print_routes(&routes, size)
	wg.Add(1)
	go shuffle_prices(sem, *rnd)
	go shuffle_routes(sem, *rnd)
	go shuffle_cities(sem, *rnd)
	go calculate_path(sem, *rnd)
	wg.Wait()
}
