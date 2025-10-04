import { describe, it, expect, vi } from 'vitest'
import { TauriService } from '../tauri'

describe('TauriService', () => {
  describe('validateConfiguration', () => {
    it('validates correct configuration format', () => {
      const validConfig = {
        accesses: [
          {
            name: 'test_user',
            type: 'credentials',
            account: 'username',
            secret: 'password',
          },
        ],
        sources: [
          {
            type: 'samba',
            url: 'smb://192.168.1.100/share',
            access: 'test_user',
          },
        ],
      }

      expect(TauriService.validateConfiguration(validConfig)).toBe(true)
    })

    it('rejects invalid configuration format', () => {
      const invalidConfig = {
        accesses: 'not an array',
        sources: [],
      }

      expect(TauriService.validateConfiguration(invalidConfig)).toBe(false)
    })

    it('rejects configuration with missing required fields', () => {
      const invalidConfig = {
        accesses: [
          {
            name: 'test_user',
            // missing type, account, secret
          },
        ],
        sources: [],
      }

      expect(TauriService.validateConfiguration(invalidConfig)).toBe(false)
    })

    it('rejects configuration with invalid source format', () => {
      const invalidConfig = {
        accesses: [
          {
            name: 'test_user',
            type: 'credentials',
            account: 'username',
            secret: 'password',
          },
        ],
        sources: [
          {
            // missing type, url, access
            invalid: 'field',
          },
        ],
      }

      expect(TauriService.validateConfiguration(invalidConfig)).toBe(false)
    })
  })

  describe('scanNetwork', () => {
    it('calls tauri invoke with correct command', async () => {
      const mockHosts = [
        {
          ip: '192.168.1.100',
          hostname: 'test-server',
          mac_address: '00:11:22:33:44:55',
          vendor: 'Test Vendor',
          open_ports: [445, 139],
          smb_shares: ['shared', 'media'],
        },
      ]

      global.mockInvoke.mockResolvedValue(mockHosts)

      const result = await TauriService.scanNetwork()

      expect(global.mockInvoke).toHaveBeenCalledWith('scan_network')
      expect(result).toEqual(mockHosts)
    })

    it('handles scan network errors', async () => {
      const errorMessage = 'Network scan failed'
      global.mockInvoke.mockRejectedValue(new Error(errorMessage))

      await expect(TauriService.scanNetwork()).rejects.toThrow(
        `Network scan failed: Error: ${errorMessage}`
      )
    })
  })

  describe('testSMBConnection', () => {
    it('calls tauri invoke with correct parameters', async () => {
      global.mockInvoke.mockResolvedValue(true)

      const result = await TauriService.testSMBConnection(
        '192.168.1.100',
        'shared',
        'username',
        'password',
        'WORKGROUP'
      )

      expect(global.mockInvoke).toHaveBeenCalledWith('test_smb_connection', {
        host: '192.168.1.100',
        share: 'shared',
        username: 'username',
        password: 'password',
        domain: 'WORKGROUP',
      })
      expect(result).toBe(true)
    })

    it('handles connection test without domain', async () => {
      global.mockInvoke.mockResolvedValue(false)

      const result = await TauriService.testSMBConnection(
        '192.168.1.100',
        'shared',
        'username',
        'password'
      )

      expect(global.mockInvoke).toHaveBeenCalledWith('test_smb_connection', {
        host: '192.168.1.100',
        share: 'shared',
        username: 'username',
        password: 'password',
        domain: undefined,
      })
      expect(result).toBe(false)
    })
  })

  describe('loadConfiguration', () => {
    it('loads configuration from file', async () => {
      const mockConfig = {
        accesses: [],
        sources: [],
      }

      global.mockInvoke.mockResolvedValue(mockConfig)

      const result = await TauriService.loadConfiguration('/path/to/config.json')

      expect(global.mockInvoke).toHaveBeenCalledWith('load_configuration', {
        filePath: '/path/to/config.json',
      })
      expect(result).toEqual(mockConfig)
    })
  })

  describe('saveConfiguration', () => {
    it('saves configuration to file', async () => {
      const mockConfig = {
        accesses: [],
        sources: [],
      }

      global.mockInvoke.mockResolvedValue(undefined)

      await TauriService.saveConfiguration('/path/to/config.json', mockConfig)

      expect(global.mockInvoke).toHaveBeenCalledWith('save_configuration', {
        filePath: '/path/to/config.json',
        config: mockConfig,
      })
    })
  })
})