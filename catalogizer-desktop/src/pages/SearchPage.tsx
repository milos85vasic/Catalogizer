import { useState } from "react";
import { Search } from "lucide-react";

export default function SearchPage() {
  const [query, setQuery] = useState("");

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-foreground mb-2">Search</h1>
        <p className="text-muted-foreground">
          Search across your entire media library
        </p>
      </div>

      <div className="max-w-2xl">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-muted-foreground" />
          <input
            type="text"
            placeholder="Search for movies, TV shows, music..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-3 border border-input bg-background rounded-lg text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent text-lg"
          />
        </div>
      </div>

      {!query && (
        <div className="text-center py-12 text-muted-foreground">
          Enter a search term to find media in your library
        </div>
      )}
    </div>
  );
}