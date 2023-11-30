package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

func main() {
	fmt.Println("Please, enter a string:")
	scan := bufio.NewScanner(os.Stdin)
	scan.Scan()

	text := scan.Text()
	text = strings.ToLower(text)

	if strings.HasPrefix(text, "i") && strings.Contains(text, "a") && strings.HasSuffix(text, "n") {
		fmt.Println("Found")
	} else {
		fmt.Println("Not Found")
	}
}
