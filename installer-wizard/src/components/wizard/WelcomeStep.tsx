import { useEffect } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/Card'
import { useWizard } from '../../contexts/WizardContext'
import { Network, Folder, Settings, CheckCircle } from 'lucide-react'

export default function WelcomeStep() {
  const { setCanNext } = useWizard()

  useEffect(() => {
    // Welcome step can always proceed
    setCanNext(true)
  }, [setCanNext])

  return (
    <div className="space-y-6">
      <div className="text-center space-y-4">
        <div className="mx-auto w-24 h-24 bg-blue-100 rounded-full flex items-center justify-center">
          <Settings className="h-12 w-12 text-blue-600" />
        </div>
        <h2 className="text-2xl font-bold text-gray-900">
          Welcome to Catalogizer Installation Wizard
        </h2>
        <p className="text-lg text-gray-600 max-w-2xl mx-auto">
          This wizard will help you configure SMB network sources for your Catalogizer media collection.
          You'll be able to scan your network, browse available shares, and create a configuration file.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-8">
        <Card>
          <CardHeader className="text-center">
            <Network className="h-8 w-8 text-blue-600 mx-auto mb-2" />
            <CardTitle className="text-lg">Network Discovery</CardTitle>
          </CardHeader>
          <CardContent>
            <CardDescription>
              Automatically scan your local network to discover SMB-enabled devices and shares.
            </CardDescription>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="text-center">
            <Folder className="h-8 w-8 text-green-600 mx-auto mb-2" />
            <CardTitle className="text-lg">Share Browsing</CardTitle>
          </CardHeader>
          <CardContent>
            <CardDescription>
              Browse available SMB shares and select specific directories as media sources.
            </CardDescription>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="text-center">
            <CheckCircle className="h-8 w-8 text-purple-600 mx-auto mb-2" />
            <CardTitle className="text-lg">Configuration</CardTitle>
          </CardHeader>
          <CardContent>
            <CardDescription>
              Generate and manage configuration files that can be used with Catalogizer.
            </CardDescription>
          </CardContent>
        </Card>
      </div>

      <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 mt-8">
        <h3 className="text-lg font-semibold text-blue-900 mb-2">What you'll need:</h3>
        <ul className="space-y-2 text-blue-800">
          <li className="flex items-center gap-2">
            <CheckCircle className="h-4 w-4 text-blue-600" />
            Access to your local network with SMB-enabled devices
          </li>
          <li className="flex items-center gap-2">
            <CheckCircle className="h-4 w-4 text-blue-600" />
            Valid credentials for SMB shares you want to configure
          </li>
          <li className="flex items-center gap-2">
            <CheckCircle className="h-4 w-4 text-blue-600" />
            A location to save your configuration file
          </li>
        </ul>
      </div>

      <div className="text-center mt-8">
        <p className="text-sm text-gray-500">
          Click "Next" to begin the network scanning process
        </p>
      </div>
    </div>
  )
}