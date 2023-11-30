package main

import (
	"fmt"
)

func main() {
	var f float64
	fmt.Println("Enter float:")
	fmt.Scan(&f)
	fmt.Println("Integer part:", int64(f))
}
