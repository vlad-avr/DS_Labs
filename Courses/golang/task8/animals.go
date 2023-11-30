package main

import "fmt"

type Animal interface {
	Eat()
	Move()
	Speak()
}

type Cow struct {
	name, food, locomotion, sound string
}

func (c Cow) Eat() {
	fmt.Println(c.food)
}

func (c Cow) Move() {
	fmt.Println(c.locomotion)
}

func (c Cow) Speak() {
	fmt.Println(c.sound)
}

type Bird struct {
	name, food, locomotion, sound string
}

func (b Bird) Eat() {
	fmt.Println(b.food)
}

func (b Bird) Move() {
	fmt.Println(b.locomotion)
}

func (b Bird) Speak() {
	fmt.Println(b.sound)
}

type Snake struct {
	name, food, locomotion, sound string
}

func (s Snake) Eat() {
	fmt.Println(s.food)
}

func (s Snake) Move() {
	fmt.Println(s.locomotion)
}

func (s Snake) Speak() {
	fmt.Println(s.sound)
}

func getCommand(command string, animal Animal) {
	switch command {
	case "eat":
		animal.Eat()
	case "move":
		animal.Move()
	case "speak":
		animal.Speak()
	default:
		fmt.Println("Invalid command")
	}

}

func main() {
	var animal, command, name string
	var animal_records []Animal

	for {
		fmt.Println("Enter animal 'newaimal name type' to create and 'query name action' to query: ")
		fmt.Print("> ")
		fmt.Scan(&command, &name, &animal)

		switch command {
		case "newanimal":
			switch animal {
			case "cow":
				animal_records = append(animal_records, Cow{name, "grass", "walk", "moo"})
				fmt.Println("Created it!")
			case "bird":
				animal_records = append(animal_records, Bird{name, "worms", "fly", "peep"})
				fmt.Println("Created it!")
			case "snake":
				animal_records = append(animal_records, Snake{name, "mice", "slither", "hsss"})
				fmt.Println("Created it!")
			default:
				fmt.Println("Uknown type")
			}
		case "query":
			for _, a := range animal_records {
				if an, e := a.(Cow); e {
					if an.name == name {
						getCommand(animal, an)
						break
					}
				} else if an, e := a.(Bird); e {
					if an.name == name {
						getCommand(animal, an)
						break
					}
				} else if an, e := a.(Snake); e {
					if an.name == name {
						getCommand(animal, an)
						break
					}
				}
			}
		default:
			fmt.Println("Invalid command")
		}
	}
}
