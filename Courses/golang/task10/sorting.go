package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func merge(arr1, arr2 []int) []int {
	fmt.Println("Merging arrays : ", arr1, arr2)

	arr := make([]int, len(arr1)+len(arr2))
	i, j, k := 0, 0, 0

	for i < len(arr1) && j < len(arr2) {
		if arr1[i] < arr2[j] {
			arr[k] = arr1[i]
			i++
		} else {
			arr[k] = arr2[j]
			j++
		}
		k++
	}

	for i < len(arr1) {
		arr[k] = arr1[i]
		i++
		k++
	}

	for j < len(arr2) {
		arr[k] = arr2[j]
		j++
		k++
	}

	return arr
}

func qsort(arr []int, values_channel chan []int) {
	if len(arr) <= 1 {
		values_channel <- arr
		return
	}

	pivot := arr[0]
	l_partition, r_partition := []int{}, []int{}

	for _, v := range arr[1:] {
		if v < pivot {
			l_partition = append(l_partition, v)
		} else {
			r_partition = append(r_partition, v)
		}
	}

	left_channel := make(chan []int)
	right_channel := make(chan []int)

	go qsort(l_partition, left_channel)
	go qsort(r_partition, right_channel)

	values_channel <- append(append(<-left_channel, pivot), <-right_channel...)
}

func concurrent_qsort(seq []int, parts int) []int {
	values_channel := make(chan []int)
	chunk := (len(seq) + 1) / parts

	for i := 0; i < parts; i++ {
		start := i * chunk
		end := (i + 1) * chunk

		if end > len(seq) {
			end = len(seq)
		}

		go func() {
			fmt.Println("Sorting : ", seq[start:end])
			qsort(seq[start:end], values_channel)
		}()
	}

	sorted := []int{}
	for i := 0; i < parts; i++ {
		sorted = merge(sorted, <-values_channel)
	}

	return sorted
}

func main() {
	fmt.Println("Enter a sequence of integers separated by ' ': ")
	scanner := bufio.NewScanner(os.Stdin)
	scanner.Scan()
	seq := []int{}
	for _, v := range strings.Split(scanner.Text(), " ") {
		n, _ := strconv.Atoi(v)
		seq = append(seq, n)
	}

	fmt.Println("Input Sequence :", seq)

	fmt.Println("Sorted Sequence :", concurrent_qsort(seq, 4))
}
