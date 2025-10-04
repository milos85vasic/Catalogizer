import '@testing-library/jest-dom'

// Mock Tauri API
const mockInvoke = vi.fn()
const mockOpen = vi.fn()
const mockSave = vi.fn()

vi.mock('@tauri-apps/api/core', () => ({
  invoke: mockInvoke,
}))

vi.mock('@tauri-apps/plugin-dialog', () => ({
  open: mockOpen,
  save: mockSave,
}))

vi.mock('@tauri-apps/plugin-fs', () => ({
  readTextFile: vi.fn(),
  writeTextFile: vi.fn(),
}))

// Global test utilities
global.mockInvoke = mockInvoke
global.mockOpen = mockOpen
global.mockSave = mockSave

// Reset mocks before each test
beforeEach(() => {
  vi.clearAllMocks()
})