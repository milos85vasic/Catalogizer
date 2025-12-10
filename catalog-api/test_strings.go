package main

import (
	"fmt"
	"strings"
)

func main() {
	title := "Test Title"
	words := strings.Fields(strings.ToLower(title))
	fmt.Printf("Words: %v\n", words)
}