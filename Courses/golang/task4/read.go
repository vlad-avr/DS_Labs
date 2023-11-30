package main

import (
	"fmt"
	"os"
)

const Len = 20

type Name struct {
	fname string
	lname string
}

func main() {
	var filename string
	fmt.Println("Enter existing file name:")
	fmt.Scanln(&filename)

	file, error_occurred := os.Open(filename)
	if error_occurred != nil {
		fmt.Println("Unable to open file!")
	}
	defer file.Close()

	var names []Name
	var name_read Name
	for {
		_, error_occurred := fmt.Fscanln(file, &name_read.fname, &name_read.lname)
		if error_occurred != nil {
			break
		}
		if len(name_read.fname) > Len {
			name_read.fname = name_read.fname[:Len]
		}
		if len(name_read.lname) > Len {
			name_read.lname = name_read.lname[:Len]
		}

		names = append(names, name_read)
	}

	for _, name := range names {
		fmt.Println("Firstname : " + name.fname + " \tLastname : " + name.lname)
	}
}
