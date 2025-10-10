import React, { useState, useEffect, useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { MediaGrid } from '@/components/media/MediaGrid'
import { MediaFilters } from '@/components/media/MediaFilters'
import { Button } from '@/components/ui/Button'
import { Input } from '@/components/ui/Input'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card'
import { mediaApi } from '@/lib/mediaApi'
import { debounce } from '@/lib/utils'
import type { MediaSearchRequest, MediaItem } from '@/types/media'
import {
  Search,
  Grid,
  List,
  SlidersHorizontal,
  Download,
  RefreshCw,
  ChevronLeft,
  ChevronRight
} from 'lucide-react'
import { motion, AnimatePresence } from 'framer-motion'

export const MediaBrowser: React.FC = () => {
  const [filters, setFilters] = useState<MediaSearchRequest>({
    limit: 24,
    offset: 0,
    sort_by: 'updated_at',
    sort_order: 'desc',
  })
  const [showFilters, setShowFilters] = useState(false)
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid')
  const [searchQuery, setSearchQuery] = useState('')

  const debouncedSearch = useMemo(
    () => debounce((query: string) => {
      setFilters(prev => ({
        ...prev,
        query: query || undefined,
        offset: 0,
      }))
    }, 300),
    []
  )

  useEffect(() => {
    debouncedSearch(searchQuery)
  }, [searchQuery, debouncedSearch])

  const {
    data: searchResults,
    isLoading,
    isError,
    refetch
  } = useQuery({
    queryKey: ['media-search', filters],
    queryFn: () => mediaApi.searchMedia(filters),
    staleTime: 1000 * 60 * 5,
  })

  const { data: stats } = useQuery({
    queryKey: ['media-stats'],
    queryFn: mediaApi.getMediaStats,
    staleTime: 1000 * 60 * 15,
  })

  const handleFiltersChange = (newFilters: MediaSearchRequest) => {
    setFilters({
      ...newFilters,
      offset: 0, // Reset pagination when filters change
    })
  }

  const handleResetFilters = () => {
    setFilters({
      limit: 24,
      offset: 0,
      sort_by: 'updated_at',
      sort_order: 'desc',
    })
    setSearchQuery('')
  }

  const handleMediaView = (media: MediaItem) => {
    console.log('View media:', media)
    // TODO: Implement media detail modal or navigation
  }

  const handleMediaDownload = (media: MediaItem) => {
    console.log('Download media:', media)
    // TODO: Implement download functionality
  }

  const handlePageChange = (direction: 'prev' | 'next') => {
    const currentPage = Math.floor((filters.offset || 0) / (filters.limit || 24))
    const newOffset = direction === 'next'
      ? (currentPage + 1) * (filters.limit || 24)
      : Math.max(0, (currentPage - 1) * (filters.limit || 24))

    setFilters(prev => ({ ...prev, offset: newOffset }))
  }

  const currentPage = Math.floor((filters.offset || 0) / (filters.limit || 24)) + 1
  const totalPages = searchResults ? Math.ceil(searchResults.total / (filters.limit || 24)) : 0

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
          Media Browser
        </h1>
        <p className="text-gray-600 dark:text-gray-400">
          Explore and discover your media collection
        </p>
      </div>

      {/* Stats Cards */}
      {stats && (
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card>
            <CardContent className="p-4">
              <div className="text-2xl font-bold text-gray-900 dark:text-white">
                {stats.total_items.toLocaleString()}
              </div>
              <div className="text-sm text-gray-600 dark:text-gray-400">Total Items</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4">
              <div className="text-2xl font-bold text-gray-900 dark:text-white">
                {Object.keys(stats.by_type).length}
              </div>
              <div className="text-sm text-gray-600 dark:text-gray-400">Media Types</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4">
              <div className="text-2xl font-bold text-gray-900 dark:text-white">
                {(stats.total_size / (1024 ** 3)).toFixed(1)} GB
              </div>
              <div className="text-sm text-gray-600 dark:text-gray-400">Total Size</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4">
              <div className="text-2xl font-bold text-gray-900 dark:text-white">
                {stats.recent_additions}
              </div>
              <div className="text-sm text-gray-600 dark:text-gray-400">Recent Additions</div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Search and Controls */}
      <div className="flex flex-col lg:flex-row gap-6 mb-8">
        <div className="flex-1">
          <Input
            type="text"
            placeholder="Search your media collection..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            icon={<Search className="h-4 w-4" />}
            className="w-full"
          />
        </div>

        <div className="flex items-center space-x-3">
          <Button
            variant="outline"
            onClick={() => setShowFilters(!showFilters)}
            className="flex items-center"
          >
            <SlidersHorizontal className="h-4 w-4 mr-2" />
            Filters
          </Button>

          <div className="flex border border-gray-300 rounded-lg dark:border-gray-600">
            <Button
              variant={viewMode === 'grid' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setViewMode('grid')}
              className="rounded-r-none"
            >
              <Grid className="h-4 w-4" />
            </Button>
            <Button
              variant={viewMode === 'list' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setViewMode('list')}
              className="rounded-l-none"
            >
              <List className="h-4 w-4" />
            </Button>
          </div>

          <Button
            variant="outline"
            onClick={() => refetch()}
            disabled={isLoading}
          >
            <RefreshCw className={`h-4 w-4 ${isLoading ? 'animate-spin' : ''}`} />
          </Button>
        </div>
      </div>

      <div className="flex gap-8">
        {/* Sidebar Filters */}
        <AnimatePresence>
          {showFilters && (
            <motion.aside
              initial={{ width: 0, opacity: 0 }}
              animate={{ width: 320, opacity: 1 }}
              exit={{ width: 0, opacity: 0 }}
              transition={{ duration: 0.3 }}
              className="overflow-hidden"
            >
              <MediaFilters
                filters={filters}
                onFiltersChange={handleFiltersChange}
                onReset={handleResetFilters}
                className="sticky top-24"
              />
            </motion.aside>
          )}
        </AnimatePresence>

        {/* Main Content */}
        <div className="flex-1 min-w-0">
          {/* Results Header */}
          {searchResults && (
            <div className="flex items-center justify-between mb-6">
              <div className="text-sm text-gray-600 dark:text-gray-400">
                Showing {searchResults.items.length} of {searchResults.total.toLocaleString()} results
                {filters.query && (
                  <span> for "{filters.query}"</span>
                )}
              </div>

              {totalPages > 1 && (
                <div className="flex items-center space-x-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => handlePageChange('prev')}
                    disabled={currentPage === 1}
                  >
                    <ChevronLeft className="h-4 w-4" />
                  </Button>
                  <span className="text-sm text-gray-600 dark:text-gray-400">
                    Page {currentPage} of {totalPages}
                  </span>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => handlePageChange('next')}
                    disabled={currentPage === totalPages}
                  >
                    <ChevronRight className="h-4 w-4" />
                  </Button>
                </div>
              )}
            </div>
          )}

          {/* Error State */}
          {isError && (
            <Card className="text-center py-12">
              <CardContent>
                <div className="text-red-500 mb-4">
                  <svg className="mx-auto h-12 w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <h3 className="text-sm font-medium text-gray-900 dark:text-white">
                  Failed to load media
                </h3>
                <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                  There was an error loading your media collection.
                </p>
                <Button
                  variant="outline"
                  onClick={() => refetch()}
                  className="mt-4"
                >
                  Try again
                </Button>
              </CardContent>
            </Card>
          )}

          {/* Media Grid */}
          {!isError && (
            <MediaGrid
              media={searchResults?.items || []}
              loading={isLoading}
              onMediaView={handleMediaView}
              onMediaDownload={handleMediaDownload}
            />
          )}

          {/* Pagination */}
          {searchResults && totalPages > 1 && (
            <div className="flex justify-center mt-12">
              <div className="flex items-center space-x-2">
                <Button
                  variant="outline"
                  onClick={() => handlePageChange('prev')}
                  disabled={currentPage === 1}
                >
                  <ChevronLeft className="h-4 w-4 mr-1" />
                  Previous
                </Button>
                <span className="px-4 py-2 text-sm text-gray-600 dark:text-gray-400">
                  Page {currentPage} of {totalPages}
                </span>
                <Button
                  variant="outline"
                  onClick={() => handlePageChange('next')}
                  disabled={currentPage === totalPages}
                >
                  Next
                  <ChevronRight className="h-4 w-4 ml-1" />
                </Button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}