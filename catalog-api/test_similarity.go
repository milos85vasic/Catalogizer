package main

import (
	"fmt"
	"strings"
)

type MediaType string
const MediaTypeVideo MediaType = "video"

type MediaMetadata struct {
	Title string
	MediaType MediaType
}

func test_similarity() {
	original := &MediaMetadata{
		Title: "The Dark Knight",
		MediaType: MediaTypeVideo,
	}

	candidates := []*MediaMetadata{
		{Title: "Completely Different Movie", MediaType: MediaTypeVideo},
	}

	similarities := make([]float64, len(candidates))
	for i, candidate := range candidates {
		similarity := 0.0
		if original.MediaType == candidate.MediaType {
			similarity += 0.5
		}

		if original.Title == candidate.Title {
			similarity += 0.5
		} else {
			originalWords := strings.Fields(strings.ToLower(original.Title))
			candidateWords := strings.Fields(strings.ToLower(candidate.Title))
			
			if len(originalWords) > 0 && len(candidateWords) > 0 {
				commonWords := 0
				for _, ow := range originalWords {
					for _, cw := range candidateWords {
						if ow == cw {
							commonWords++
							break
						}
					}
				}
				titleSimilarity := float64(commonWords) / float64(len(originalWords))
				if len(candidateWords) > len(originalWords) {
					titleSimilarity = float64(commonWords) / float64(len(candidateWords))
				}
				similarity += titleSimilarity * 0.3
			}
		}
		similarities[i] = similarity
	}

	fmt.Printf("Similarities: %v\n", similarities)
}

func main() {
	test_similarity()
}