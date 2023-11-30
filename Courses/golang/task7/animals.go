package main

import "fmt"

type Animal struct {
	food, locomotion, sound string
}

func (animal *Animal) Eat() {
	println(animal.food)
}

func (animal *Animal) Move() {
	println(animal.locomotion)
}

func (animal *Animal) Speak() {
	println(animal.sound)
}

func NewAnimal(animalType string) *Animal {
	switch animalType {
	case "cow":
		return &Animal{"grass", "walk", "moo"}
	case "bird":
		return &Animal{"worms", "fly", "peep"}
	case "snake":
		return &Animal{"mice", "slither", "hsss"}
	default:
		return nil
	}
}

func (animal *Animal) Act(action string) {
	switch action {
	case "eat":
		animal.Eat()
	case "move":
		animal.Move()
	case "speak":
		animal.Speak()
	default:
		fmt.Println("Please, enter a valid action and try again.")
	}
}

func main() {
	var animalType, animalAction string
	println("Enter animal and action (like 'bird move') or 'X X' to exit : ")
	for {
		_, invalid_input := fmt.Scan(&animalType, &animalAction)
		if invalid_input != nil {
			fmt.Println("Invalid input")
			continue
		}
		if animalType == "X" {
			break
		}
		animal := NewAnimal(animalType)
		if animal == nil {
			fmt.Println("Invalid animal type")
			continue
		}
		animal.Act(animalAction)
	}
}
