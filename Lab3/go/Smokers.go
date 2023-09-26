package main

import (
	"math/rand"
	"sync"
)

type Smoker struct {
	item string
}

type Table struct {
	table [2]string
}

func select_components() (int, int) {
	first := rand.Intn(3)
	second := rand.Intn(3)
	for first == second {
		second = rand.Intn(3)
	}
	return first, second
}

func smoke(wg *sync.WaitGroup, is_smoking chan bool, table *[]string) {
	for {
		is_smoking <- false
	}
}

func start_session(smoker1 chan Smoker, smoker2 chan Smoker, smoker3 chan Smoker, table chan Table) {
	for {
		var wg sync.WaitGroup
	}
}

func main() {
	var smoker1 Smoker
	smoker1.item = "tobacco"
	start_session()
}
