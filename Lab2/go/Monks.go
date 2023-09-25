package main

import (
	"math"
	"math/rand"
	"sync"
	"time"
)

type Monk struct {
	energy    int
	monastery string
}

func make_monks(number int, max_energy int) chan Monk {
	monks := make(chan Monk, number)
	seed := rand.NewSource(time.Now().Unix())
	rnd := rand.New(seed)
	print("Contestants: \n")
	for i := 0; i < number; i++ {
		energy := rnd.Intn(max_energy) + 1
		coin_flip := rnd.Intn(2)
		monastery := ""
		if coin_flip == 0 {
			monastery = "Guan In`"
		} else {
			monastery = "Guan Yan`"
		}
		monks <- Monk{energy: energy, monastery: monastery}
		print("Monk from ", monastery, " with ", energy, " energy \n")
	}
	return monks
}

func fight(monk_pool chan Monk, winner_pool chan Monk, wg *sync.WaitGroup) {
	defer wg.Done()
	fighter1 := <-monk_pool
	fighter2 := <-monk_pool
	winner_id := 0
	fighter_winner := fighter1
	if fighter1.energy <= fighter2.energy {
		winner_id = 2
		winner_pool <- fighter2
		fighter_winner = fighter2
	} else {
		winner_id = 1
		winner_pool <- fighter1
	}
	print("Fighter 1: Monk from ", fighter1.monastery, " monastery with ", fighter1.energy, " energy of The Fist points\nFighter 2: Monk from ",
		fighter2.monastery, " monastery with ", fighter2.energy,
		" energy of The Fist points\nAnd the winner is FIGHTER ", winner_id, " with respectable ", fighter_winner.energy, " energy from ", fighter_winner.monastery, " monastery\n\n")

}

func conduct_competition(monks chan Monk, fights int, rounds int) chan Monk {
	var wg sync.WaitGroup
	for i := 1; i <= rounds; i++ {
		contestants := make(chan Monk, fights)
		for j := 0; j < fights; j++ {
			wg.Add(1)
			go fight(monks, contestants, &wg)
		}
		fights /= 2
		monks = contestants

	}
	wg.Wait()
	return monks
}

func main() {
	const contestants_number = 16
	const maximum_energy = 300
	monks := make_monks(contestants_number, maximum_energy)
	winner := <-conduct_competition(monks, contestants_number/2, int(math.Log2(contestants_number)))
	print("And the Winner of The Fisting competition is Monk from ", winner.monastery, " with massive ", winner.energy, " energy. Congratularions to new Fisting Master!")
}
