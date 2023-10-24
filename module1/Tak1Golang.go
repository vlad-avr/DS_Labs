package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

func main() {
	books := make([]string, 0)
	semaphore := make(chan struct{}, 1)
	rand.Seed(time.Now().UnixNano())
	NBooks := 20
	NReaders := 5
	var wg sync.WaitGroup
	wg.Add(NReaders)
	for i := 0; i < NBooks; i++ {
		books = append(books, fmt.Sprintf("Book %d", i))
	}

	for i := 0; i < NReaders; i++ {
		// Goroutines will read books indefinitely (stop the program manually because these guys are real bookworms))))
		go func(readerID int) {
			// Number of times a reader can take books from the library
			maxTakes := 10
			curTake := 0

			for curTake < maxTakes {
				if rand.Intn(2) == 0 {
					// Take home (take fewer books for a longer period of time)
					booksTaken := make([]string, 0)
					booksNum := rand.Intn(NBooks / 4)
					booksStr := fmt.Sprintf("\nReader %d is reading books at home:", readerID)
					semaphore <- struct{}{}

					for i := 0; i < booksNum && len(books) > 0; i++ {
						index := rand.Intn(len(books))
						booksTaken = append(booksTaken, books[index])
						booksStr += " " + booksTaken[i] + ","
						books = append(books[:index], books[index+1:]...)
					}
					<-semaphore

					if len(booksTaken) > 0 {
						fmt.Println(booksStr)
					}

					time.Sleep(time.Duration(len(booksTaken)*800) * time.Millisecond)

					semaphore <- struct{}{}
					for _, book := range booksTaken {
						books = append(books, book)
					}
					<-semaphore

					if len(booksTaken) > 0 {
						curTake++
						fmt.Printf("\nReader %d has returned all the books!\n", readerID)
					}

					time.Sleep(time.Duration(rand.Intn(1000)+1000) * time.Millisecond)
				} else {
					// Take to reading room (more books - less time)
					booksTaken := make([]string, 0)
					booksStr := fmt.Sprintf("\nReader %d is reading books at the library:", readerID)
					booksNum := rand.Intn(NBooks / 2)
					semaphore <- struct{}{}

					for i := 0; i < booksNum && len(books) > 0; i++ {
						index := rand.Intn(len(books))
						booksTaken = append(booksTaken, books[index])
						booksStr += " " + booksTaken[i] + ","
						books = append(books[:index], books[index+1:]...)
					}
					<-semaphore

					if len(booksTaken) > 0 {
						fmt.Println(booksStr)
					}

					time.Sleep(time.Duration(len(booksTaken)*400) * time.Millisecond)

					semaphore <- struct{}{}
					for _, book := range booksTaken {
						books = append(books, book)
					}
					<-semaphore

					if len(booksTaken) > 0 {
						curTake++
						fmt.Printf("\nReader %d has returned all the books!\n", readerID)
					}

					time.Sleep(time.Duration(rand.Intn(1000)+1000) * time.Millisecond)
				}
			}
			wg.Done()
		}(i)
	}
	wg.Wait()
}
