import React, { useEffect, useState } from 'react'
import { useWebSocket } from '@/lib/websocket'
import { Wifi, WifiOff, Loader2 } from 'lucide-react'
import { motion, AnimatePresence } from 'framer-motion'

export const ConnectionStatus: React.FC = () => {
  const { getConnectionState } = useWebSocket()
  const [connectionState, setConnectionState] = useState<'connecting' | 'open' | 'closing' | 'closed'>('closed')
  const [showStatus, setShowStatus] = useState(false)

  useEffect(() => {
    const checkConnection = () => {
      const state = getConnectionState()
      setConnectionState(state)

      // Show status when not connected or connecting
      setShowStatus(state !== 'open')
    }

    checkConnection()
    const interval = setInterval(checkConnection, 1000)

    return () => clearInterval(interval)
  }, [getConnectionState])

  const getStatusConfig = () => {
    switch (connectionState) {
      case 'connecting':
        return {
          icon: <Loader2 className="h-4 w-4 animate-spin" />,
          text: 'Connecting...',
          color: 'bg-yellow-500',
          textColor: 'text-yellow-100'
        }
      case 'open':
        return {
          icon: <Wifi className="h-4 w-4" />,
          text: 'Connected',
          color: 'bg-green-500',
          textColor: 'text-green-100'
        }
      case 'closing':
        return {
          icon: <WifiOff className="h-4 w-4" />,
          text: 'Disconnecting...',
          color: 'bg-orange-500',
          textColor: 'text-orange-100'
        }
      case 'closed':
        return {
          icon: <WifiOff className="h-4 w-4" />,
          text: 'Disconnected',
          color: 'bg-red-500',
          textColor: 'text-red-100'
        }
    }
  }

  const config = getStatusConfig()

  return (
    <AnimatePresence>
      {showStatus && (
        <motion.div
          initial={{ opacity: 0, y: -50 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -50 }}
          className="fixed top-4 right-4 z-50"
        >
          <div className={`${config.color} ${config.textColor} px-3 py-2 rounded-lg shadow-lg flex items-center space-x-2 text-sm font-medium`}>
            {config.icon}
            <span>{config.text}</span>
          </div>
        </motion.div>
      )}
    </AnimatePresence>
  )
}