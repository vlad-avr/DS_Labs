package main

/*
    Explanation of the Race Condition:

The race condition in this example occurs due to the concurrent access and modification of the shared variable counter without proper synchronization.
Both goroutines are reading the current value of counter, performing some computation, and then updating the value.
Since these operations are not atomic and there is no explicit synchronization, a race condition can occur.

The race condition manifests because the sequence of operations is interleaved in an unpredictable manner between the two goroutines.
For example, if both goroutines read the counter value simultaneously and then increment it, the final value may not reflect the actual increment from both goroutines.
The interleaving of read-modify-write operations by multiple goroutines can lead to unexpected and incorrect results.
*/

import (
	"fmt"
	"sync"
)

var res int
var wg sync.WaitGroup

func main() {
	wg.Add(2)
	go increase()
	go decrease()
	wg.Wait()
	fmt.Println("Result:", res)
}

func increase() {
	for i := 0; i < 1000; i++ {
		res++
	}
	wg.Done()
}

func decrease() {
	for i := 0; i < 1000; i++ {
		res--
	}
	wg.Done()
}

/*
The race condition occurs because both goroutines are accessing and modifying the counter
variable simultaneously without any synchronization mechanism to protect it.
As a result, the final value of counter is unpredictable, and running the program multiple
times may yield different results.
*/
