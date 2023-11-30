package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"os"
)

func main() {
	var name, address string
	fmt.Println("Enter name:")

	scanner := bufio.NewScanner(os.Stdin)
	scanner.Scan()
	name = scanner.Text()

	fmt.Println("Enter address:")
	scanner.Scan()
	address = scanner.Text()

	users_map := map[string]string{
		"name":    name,
		"address": address,
	}
	json, error_occurred := json.Marshal(users_map)
	if error_occurred != nil {
		fmt.Println("Error while marshalling", error_occurred)
		return
	}
	fmt.Println(string(json))
}
