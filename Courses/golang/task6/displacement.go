package main

import "fmt"

func GetParams() (float64, float64, float64) {
	var a, v, s float64
	fmt.Println("Enter acceleration:")
	fmt.Scan(&a)
	fmt.Println("Enter velocity:")
	fmt.Scan(&v)
	fmt.Println("Enter starting displacement:")
	fmt.Scan(&s)
	return a, v, s
}

func GetTime() float64 {
	var t float64
	fmt.Println("Enter time:")
	fmt.Scan(&t)
	return t
}

func GenDisplaceFn(a, v, s float64) func(float64) float64 {
	return func(t float64) float64 {
		return 0.5*a*t*t + v*t + s
	}
}

func main() {
	a, v, s := GetParams()
	fn := GenDisplaceFn(a, v, s)

	t := GetTime()
	fmt.Println("Displacement in ", t, " s :", fn(t))
}
